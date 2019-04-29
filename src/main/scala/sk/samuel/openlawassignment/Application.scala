package sk.samuel.openlawassignment

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.handleExceptions
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import sk.samuel.openlawassignment.RouteUtils.{logRequest, rootExceptionHandler}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object Application extends App with Routes with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("httpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  try {
    val rootRoute: Route =
      handleExceptions(rootExceptionHandler) {
        logRequest {
          parseRoute
        }
      }

    val config: Config = ConfigFactory.parseResources(this.getClass.getClassLoader, "application.conf")

    val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(rootRoute, "localhost", config.getInt("port"))

    serverBinding.onComplete {
      case Success(bound) =>
        logger.info(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
      case Failure(e) =>
        logger.error(s"Server could not start!", e)
        system.terminate()
    }

    Await.result(system.whenTerminated, Duration.Inf)

  } catch {
    case e: Exception =>
      logger.error("Unhandled error on the main thread. Shutting down", e)
      System.exit(1)
  }
}
