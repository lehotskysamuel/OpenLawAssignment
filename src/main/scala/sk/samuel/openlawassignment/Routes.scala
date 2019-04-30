package sk.samuel.openlawassignment

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.stream.Materializer
import akka.stream.scaladsl.{Framing, Source}
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
            //todo - better whitespace delimiter
            def words: Source[String, Any] = byteSource.via(Framing.delimiter(ByteString(" "), 1024, allowTruncation = true))
              .map(_.utf8String)

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
