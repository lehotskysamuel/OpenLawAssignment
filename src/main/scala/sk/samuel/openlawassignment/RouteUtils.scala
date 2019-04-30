package sk.samuel.openlawassignment

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, extractMethod, extractUri}
import akka.http.scaladsl.server.{Directive0, ExceptionHandler}
import com.typesafe.scalalogging.StrictLogging

object RouteUtils extends StrictLogging {

  /**
   * Only logs which request failed (method and uri) and completes with InternalServerError.
   * This ExceptionHandler should be used to help linking client errors with logs when troubleshooting.
   */
  lazy val rootExceptionHandler = ExceptionHandler {
    case e =>
      (extractMethod & extractUri) { (method, uri) =>
        val errorCode = s"ERROR-${UUID.randomUUID().toString}"
        logger.error(s"Request ${method.value} $uri caused an unexpected exception. Error code: $errorCode", e)
        complete(StatusCodes.InternalServerError, s"Unexpected error occurred. Please report this error code: $errorCode")
      }
  }

  lazy val logRequest: Directive0 = {
    (extractMethod & extractUri).tmap(tuple => {
      logger.info(s"Request received: ${tuple._1.value} ${tuple._2}")
    })
  }

}
