package sk.samuel.openlawassignment

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with Routes {

  "ParseRoute" should {

    "count words correctly" in {
      postParseRequest("one two two three three three") ~> check {
        status shouldEqual StatusCodes.OK

        val response = responseAs[ParseResponse]
        response.total shouldEqual 6
        response.wordCount.size shouldEqual 3
        response.wordCount.get("one") shouldEqual Some(1)
        response.wordCount.get("two") shouldEqual Some(2)
        response.wordCount.get("three") shouldEqual Some(3)
      }
    }

    "handle empty files" in {
      postParseRequest("") ~> check {
        status shouldEqual StatusCodes.OK

        val response = responseAs[ParseResponse]
        response.total shouldEqual 0
        response.wordCount.size shouldEqual 0
      }
    }

    "handle whitespace-only files" in {
      postParseRequest(" \t \n \r ") ~> check {
        status shouldEqual StatusCodes.OK

        val response = responseAs[ParseResponse]
        response.total shouldEqual 0
        response.wordCount.size shouldEqual 0
      }
    }

    "handle multiple lines" in {
      postParseRequest("one two three\ntwo three\nthree") ~> check {
        status shouldEqual StatusCodes.OK

        val response = responseAs[ParseResponse]
        response.total shouldEqual 6
        response.wordCount.get("one") shouldEqual Some(1)
        response.wordCount.get("two") shouldEqual Some(2)
        response.wordCount.get("three") shouldEqual Some(3)
      }
    }

    "handle single word" in {
      postParseRequest("word") ~> check {
        status shouldEqual StatusCodes.OK

        val response = responseAs[ParseResponse]
        response.total shouldEqual 1
        response.wordCount.size shouldEqual 1
        response.wordCount.get("word") shouldEqual Some(1)
      }
    }

    "ignore case" in {
      postParseRequest("one Two TWO THREE ThReE three") ~> check {
        status shouldEqual StatusCodes.OK

        val response = responseAs[ParseResponse]
        response.total shouldEqual 6
        response.wordCount.size shouldEqual 3
        response.wordCount.get("one") shouldEqual Some(1)
        response.wordCount.get("two") shouldEqual Some(2)
        response.wordCount.get("three") shouldEqual Some(3)
      }
    }

    "count characters as words" in {
      postParseRequest("a b b c c c") ~> check {
        status shouldEqual StatusCodes.OK

        val response = responseAs[ParseResponse]
        response.total shouldEqual 6
        response.wordCount.size shouldEqual 3
        response.wordCount.get("a") shouldEqual Some(1)
        response.wordCount.get("b") shouldEqual Some(2)
        response.wordCount.get("c") shouldEqual Some(3)
      }
    }

  }

  private def postParseRequest(fileContent: String) = {
    val formData =
      Multipart.FormData(Multipart.FormData.BodyPart.Strict(
        "words",
        HttpEntity(ContentTypes.`text/plain(UTF-8)`, fileContent),
        Map("filename" -> "words.txt")))

    Post(uri = "/parse", formData) ~> parseRoute
  }
}
