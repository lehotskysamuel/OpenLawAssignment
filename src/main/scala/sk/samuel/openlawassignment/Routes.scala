package sk.samuel.openlawassignment

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging

import scala.collection.mutable

trait Routes extends JsonSupport with StrictLogging {

  implicit def system: ActorSystem

  lazy val parseRoute: Route =
    path("parse") {
      extractRequestContext { ctx =>
        implicit val materializer: Materializer = ctx.materializer

        (withSizeLimit(10 * 1024 * 1024 /* 10MB */) & fileUpload("words")) {
          case (metadata, byteSource) =>

            def words: Source[String, Any] = byteSource
              .map(_.utf8String)
              .via(new WhitespaceFraming)

            val wordCounts = mutable.HashMap.empty[String, Int]

            val total = words
              .map(_.toLowerCase)
              .map(word => {
                wordCounts += (word -> (wordCounts.getOrElse(word, 0) + 1))
                word
              })
              .runFold(0) { (acc, _) => acc + 1 }

            onSuccess(total) {
              count => complete(ParseResponse(count, wordCounts.toMap))
            }
        }
      }
    }
}
