package sk.samuel.openlawassignment

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class RoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with Routes {

  "ParseRoute" should {

    "TODO test not yet implemented" in { //todo
      val formData =
        Multipart.FormData(Multipart.FormData.BodyPart.Strict(
          "words",
          HttpEntity(ContentTypes.`text/plain(UTF-8)`, "one two two three three three"),
          Map("filename" -> "words.txt")))

      Post(uri = "/parse", formData) ~> parseRoute ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

  }
}
