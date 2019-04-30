package sk.samuel.openlawassignment

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}

import scala.collection.mutable

/**
  * Frames messages on whitespace, ie. changes stream of arbitrary strings into stream of single words.
  * For example: "message with one sentence" => "message", "with", "one", "sentence"
  * Takes all whitespace into account (whitespace is recognized with \s+ regex pattern)
  */
class WhitespaceFraming extends GraphStage[FlowShape[String, String]] {

  val in: Inlet[String] = Inlet("WhitespaceFraming.in")
  val out: Outlet[String] = Outlet("WhitespaceFraming.out")

  override val shape: FlowShape[String, String] = FlowShape.of(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      private val buffer = mutable.Queue[String]()

      setHandler(in, new InHandler {
        override def onPush(): Unit = {

          val str = grab(in)

          if (str == "") { //swallow empty messages
            pull(in)
            return
          }

          val splits = str.split("\\s+", -1)

          var emitted = false
          val firstSplit = splits.take(1)(0)
          val lastSplit = splits.takeRight(1)(0)
          val words = splits.drop(1).dropRight(1)

          buffer += firstSplit

          if (splits.length != 1) { //skip if firstSplit is the lastSplit
            emitted ||= maybeFlush //there's been a whitespace (splits.length != 1) -> flush buffer

            if (words.nonEmpty) {
              emitMultiple(out, words.toIterator)
              emitted = true
            }

            if (!lastSplit.isEmpty) { //buffer until next whitespace
              buffer += lastSplit
            }
          }

          if (!emitted) pull(in)
        }

        override def onUpstreamFinish(): Unit = {
          maybeFlush
          complete(out)
        }

        override def onUpstreamFailure(ex: Throwable): Unit = {
          maybeFlush
          fail(out, ex)
        }
      })

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })

      private def maybeFlush = {
        val toFlush = buffer.dequeueAll(_ => true).mkString("")

        if (toFlush.nonEmpty) {
          emit(out, toFlush)
          true
        } else {
          false
        }
      }

    }
}
