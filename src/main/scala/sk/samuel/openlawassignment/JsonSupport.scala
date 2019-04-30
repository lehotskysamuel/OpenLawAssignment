package sk.samuel.openlawassignment

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

final case class ParseResponse(total: Int, wordCount: Map[String, Int])

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val parseResponseFormat: RootJsonFormat[ParseResponse] = jsonFormat2(ParseResponse)

}
