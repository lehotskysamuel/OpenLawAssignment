package sk.samuel.openlawassignment

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.stream.testkit.{TestPublisher, TestSubscriber}
import org.scalatest.{Matchers, WordSpec}


class WhitespaceFramingSpec extends WordSpec with Matchers with ScalatestRouteTest with Routes {

  "WhitespaceFraming Flow" should {

    "frame messages on whitespace" in {
      val (source, sink) = setupTestProbes

      source.sendNext("one two\n")
      source.sendNext("three\tfour")
      source.sendComplete()

      sink.expectNext("one", "two", "three", "four")
      sink.expectComplete()
    }

    "trim all whitespace" in {
      val (source, sink) = setupTestProbes

      source.sendNext("\t  \none  \n    two \n\t  ")
      source.sendComplete()

      sink.expectNext("one", "two")
      sink.expectComplete()
    }

    "swallow whitespace-only message" in {
      val (source, sink) = setupTestProbes

      source.sendNext("      ")
      source.sendComplete()

      sink.expectComplete()
    }

    "swallow all whitespace-only messages" in {
      val (source, sink) = setupTestProbes

      source.sendNext("      ")
      source.sendNext("one")
      source.sendNext("      ")
      source.sendComplete()

      sink.expectNext("one")
      sink.expectComplete()
    }

    "buffer characters between messages from source" in {
      val (source, sink) = setupTestProbes

      source.sendNext(" one ")
      source.sendNext("two")
      source.sendNext(" th")
      source.sendNext("r")
      source.sendNext("ee f")
      source.sendNext("")
      source.sendNext("our")
      source.sendComplete()

      sink.expectNext("one", "two", "three", "four")
      sink.expectComplete()
    }

    "clear buffer before completing" in {
      val (source, sink) = setupTestProbes

      source.sendNext("one two th")
      source.sendComplete()

      sink.expectNext("one", "two", "th")
      sink.expectComplete()
    }

    "fail on source failing" in {
      val (source, sink) = setupTestProbes

      val error = new Exception()
      source.sendNext("one two th")
      source.sendError(error)

      sink.expectNext("one", "two", "th")
      sink.expectError(error)
    }

  }

  private def setupTestProbes: (TestPublisher.Probe[String], TestSubscriber.Probe[String]) = {
    val (source, sink) = TestSource.probe[String].via(Flow.fromGraph(new WhitespaceFraming)).toMat(TestSink.probe[String])(Keep.both).run()
    sink.request(10)

    (source, sink)
  }
}
