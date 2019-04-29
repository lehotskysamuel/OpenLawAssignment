package sk.samuel.openlawassignment

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging

trait Routes extends StrictLogging {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])

  lazy val parseRoute: Route =
    path("parse") {
      extractRequestContext { ctx =>
        implicit val materializer: Materializer = ctx.materializer

        fileUpload("words") {
          case (metadata, byteSource) =>
            complete("TODO not yet implemented") //todo
        }
      }
    }
}
