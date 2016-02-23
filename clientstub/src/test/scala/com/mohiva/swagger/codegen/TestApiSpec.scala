/**
 * Copyright 2016 Mohiva Organisation (license at mohiva dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mohiva.swagger.codegen

import java.io.{ File, FileNotFoundException }
import java.net.URL

import com.mohiva.swagger.codegen.core.ApiRequest.{ ApiKey, BasicCredentials }
import com.mohiva.swagger.codegen.core.{ ApiConfig, ApiError, ApiInvoker }
import com.mohiva.swagger.codegen.models.User
import mockws.{ MockWS, Route }
import org.joda.time.{ DateTime, DateTimeZone }
import org.specs2.control.NoLanguageFeatures
import org.specs2.matcher.ContentMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.BodyParsers._
import play.api.mvc.Results._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.language.postfixOps

/**
 * Test case for the [[TestApi]] class.
 */
class TestApiSpec extends Specification with NoLanguageFeatures with ContentMatchers {

  "The `API` handler" should {
    "send a GET request" in new Context {
      val route = Route { case ("GET", "/test") => Action(NoContent) }

      await(testApi.testGet()).content must beEqualTo(())
    }

    "send a POST request" in new Context {
      val route = Route { case ("POST", "/test") => Action(NoContent) }

      await(testApi.testPost()).content must beEqualTo(())
    }

    "send a POST request with body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action(parse.json) { request =>
          Ok(request.body)
        }
      }

      await(testApi.testPostWithBody(user)).content must beEqualTo(user)
    }

    "send a PUT request" in new Context {
      val route = Route { case ("PUT", "/test") => Action(NoContent) }

      await(testApi.testPut()).content must beEqualTo(())
    }

    "send a PUT request with body" in new Context {
      val route = Route {
        case ("PUT", "/test") => Action(parse.json) { request =>
          Ok(request.body)
        }
      }

      await(testApi.testPutWithBody(user)).content must beEqualTo(user)
    }

    "send a PATCH request" in new Context {
      val route = Route { case ("PATCH", "/test") => Action(NoContent) }

      await(testApi.testPatch()).content must beEqualTo(())
    }

    "send a PATCH request with body" in new Context {
      val route = Route {
        case ("PATCH", "/test") => Action(parse.json) { request =>
          Ok(request.body)
        }
      }

      await(testApi.testPatchWithBody(user)).content must beEqualTo(user)
    }

    "send a DELETE request" in new Context {
      val route = Route { case ("DELETE", "/test") => Action(NoContent) }

      await(testApi.testDelete()).content must beEqualTo(())
    }

    "send a DELETE request with body" in new Context {
      val route = Route {
        case ("DELETE", "/test") => Action(parse.json) { request =>
          Ok(request.body)
        }
      }

      await(testApi.testDeleteWithBody(user)).content must beEqualTo(user)
    }

    "send a CONNECT request" in new Context {
      val route = Route { case ("CONNECT", "/test") => Action(NoContent) }

      await(testApi.testConnect()).content must beEqualTo(())
    }

    "send a HEAD request" in new Context {
      val route = Route { case ("HEAD", "/test") => Action(NoContent) }

      await(testApi.testHead()).content must beEqualTo(())
    }

    "send a OPTIONS request" in new Context {
      val route = Route { case ("OPTIONS", "/test") => Action(NoContent) }

      await(testApi.testOptions()).content must beEqualTo(())
    }

    "send a TRACE request" in new Context {
      val route = Route { case ("TRACE", "/test") => Action(NoContent) }

      await(testApi.testTrace()).content must beEqualTo(())
    }

    "send a request with an empty body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action { request =>
          NoContent
        }
      }

      await(testApi.testRequestWithEmptyBody()).content must beEqualTo(())
    }

    "send a request with a Json object body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action(parse.json) { request =>
          Ok(request.body)
        }
      }

      await(testApi.testRequestWithJsonObjectBody(user)).content must be equalTo user
    }

    "send a request with a Json array body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action(parse.json) { request =>
          Ok(request.body)
        }
      }

      await(testApi.testRequestWithJsonArrayBody(Seq(user))).content must be equalTo Seq(user)
    }

    "send a request with Some Json body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action { request =>
          request.body.asJson match {
            case Some(txt) => Ok(txt)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithSomeJsonBody(Some(user))).content must be equalTo user
    }

    "send a request with None Json body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action { request =>
          request.body.asJson match {
            case Some(txt) => Ok(txt)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithNoneJsonBody(None)).content must beEqualTo(())
    }

    "send a request with a File body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action { request =>
          val f = request.body.asRaw.map(_.asFile).get
          Ok.sendFile(f)
        }
      }

      await(testApi.testRequestWithFileBody(file("test.txt"))).content must haveSameLinesAs(file("test.txt"))
    }

    "send a request with an Int body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action { request =>
          request.body.asText match {
            case Some(txt) => Ok(txt)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithIntBody(1)).content must be equalTo "1"
    }

    "send a request with a String body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action { request =>
          request.body.asText match {
            case Some(txt) => Ok(txt)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithStringBody("test")).content must be equalTo "test"
    }

    "send a request with a Boolean body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action { request =>
          request.body.asText match {
            case Some(txt) => Ok(txt)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithBooleanBody(body = true)).content must be equalTo "true"
    }

    "send a request with Some primitive body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action { request =>
          request.body.asText match {
            case Some(txt) => Ok(txt)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithSomePrimitiveBody(Some("test"))).content must be equalTo "test"
    }

    "send a request with None primitive body" in new Context {
      val route = Route {
        case ("POST", "/test") => Action { request =>
          request.body.asText match {
            case Some(txt) => Ok(txt)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithNonePrimitiveBody(None)).content must beEqualTo(())
    }

    "send a request with multipart-form data" in new Context {
      val route = Route {
        case ("POST", "/test") => Action(parse.multipartFormData) { request =>
          val file = request.body.file("file")
          val returnFile = request.body.dataParts.get("returnFile").exists(_.exists(_ == "true"))
          file -> returnFile match {
            case (Some(f), true) => Ok.sendFile(f.ref.file)
            case _ => NoContent
          }
        }
      }

      await(testApi.testRequestWithMultipartFormData(file("test.txt"), returnFile = true)).content must haveSameLinesAs(file("test.txt"))
    }

    "send a request with form-url-encoded data" in new Context {
      val route = Route {
        case ("POST", "/test") => Action(parse.urlFormEncoded) { request =>
          Ok(toQueryString(request.body))
        }
      }

      await(testApi.testRequestWithFormURLEncodedData("te/st", 12345)).content must be equalTo "param1=te/st&param2=12345"
    }

    "send a request with an Int header" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.headers.get("X-HEADER") match {
            case Some(header) => Ok(header)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithIntHeader(1)).content must be equalTo "1"
    }

    "send a request with a String header" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.headers.get("X-HEADER") match {
            case Some(header) => Ok(header)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithStringHeader("test")).content must be equalTo "test"
    }

    "send a request with a Boolean header" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.headers.get("X-HEADER") match {
            case Some(header) => Ok(header)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithBooleanHeader(header = true)).content must be equalTo "true"
    }

    "send a request with Some string header header" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.headers.get("X-HEADER") match {
            case Some(header) => Ok(header)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithSomeStringHeader(Some("test"))).content must be equalTo "test"
    }

    "send a request with None string header header" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.headers.get("X-HEADER") match {
            case Some(header) => Ok(header)
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithNoneStringHeader(None)).content must beEqualTo(())
    }

    "send a request with query parameters" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok(toQueryString(request.queryString))
        }
      }

      await(testApi.testRequestWithQueryParameters("test", 12345)).content must be equalTo "param1=test&param2=12345"
    }

    "send a request with array[CSV] query parameter" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok(toQueryString(request.queryString))
        }
      }

      await(testApi.testRequestWithArrayCsvQueryParameters(Seq("test1", "test2", "test3"))).content must be equalTo "param=test1,test2,test3"
    }

    "send a request with array[TSV] query parameter" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok(toQueryString(request.queryString))
        }
      }

      await(testApi.testRequestWithArrayTsvQueryParameters(Seq("test1", "test2", "test3"))).content must be equalTo "param=test1\ttest2\ttest3"
    }

    "send a request with array[SSV] query parameter" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok(toQueryString(request.queryString))
        }
      }

      await(testApi.testRequestWithArraySsvQueryParameters(Seq("test1", "test2", "test3"))).content must be equalTo "param=test1 test2 test3"
    }

    "send a request with array[PIPES] query parameter" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok(toQueryString(request.queryString))
        }
      }

      await(testApi.testRequestWithArrayPipesQueryParameters(Seq("test1", "test2", "test3"))).content must be equalTo "param=test1|test2|test3"
    }

    "send a request with array[MULTI] query parameter" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok(toQueryString(request.queryString))
        }
      }

      await(testApi.testRequestWithArrayMultiQueryParameters(Seq("test1", "test2", "test3"))).content must be equalTo "param=test3&param=test2&param=test1"
    }

    "send a request with Some array query parameter" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.queryString.get("param") match {
            case Some(param) => Ok(toQueryString(request.queryString))
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithSomeArrayQueryParameters(Some(Seq("test1", "test2", "test3")))).content must be equalTo "param=test1,test2,test3"
    }

    "send a request with None array query parameter" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.queryString.get("param") match {
            case Some(param) => Ok(toQueryString(request.queryString))
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithNoneArrayQueryParameters(None)).content must beEqualTo(())
    }

    "send a request with Some string query parameter" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.queryString.get("param") match {
            case Some(param) => Ok(toQueryString(request.queryString))
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithSomeStringQueryParameter(Some("test"))).content must be equalTo "param=test"
    }

    "send a request with None string query parameter" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.queryString.get("param") match {
            case Some(param) => Ok(toQueryString(request.queryString))
            case None => NoContent
          }
        }
      }

      await(testApi.testRequestWithNoneStringQueryParameter(None)).content must beEqualTo(())
    }

    "send a request with path parameters" in new Context {
      val route = Route {
        case ("GET", "/test/item/1") => Action { request =>
          NoContent
        }
      }

      await(testApi.testRequestWithPathParameters("item", 1)).content must beEqualTo(())
    }

    "send a request with basic credentials" in new Context {
      skipped("Cannot be tested because of missing functionality in play-mockws: https://github.com/leanovate/play-mockws/issues/12")

      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.headers.get("Authorization") match {
            case Some(header) => Ok(header)
            case None => NoContent
          }
        }
      }

      implicit val credentials = BasicCredentials("user", "password")
      await(testApi.testRequestWithBasicCredentials()).content must be equalTo ""
    }

    "send a request with API credentials in the header" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.headers.get("X-AUTH") match {
            case Some(key) => Ok(key)
            case None => NoContent
          }
        }
      }

      implicit val credentials = ApiKey("12345")
      await(testApi.testRequestWithAPICredentialsInHeader()).content must be equalTo "12345"
    }

    "send a request with API credentials in the query string" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          request.queryString.get("auth").flatMap(_.headOption) match {
            case Some(key) => Ok(key)
            case None => NoContent
          }
        }
      }

      implicit val credentials = ApiKey("12345")
      await(testApi.testRequestWithAPICredentialsInQueryString()).content must be equalTo "12345"
    }

    "return an ApiResponse with Unit as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          NoContent
        }
      }

      await(testApi.testApiResponseWithUnitAsValue()).content must beEqualTo(())
    }

    "return an ApiResponse with a Json object as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok(Json.toJson(user))
        }
      }

      await(testApi.testApiResponseWithJsonObjectAsValue()).content must be equalTo user
    }

    "return an ApiResponse with a Json array as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok(Json.toJson(Seq(user)))
        }
      }

      await(testApi.testApiResponseWithJsonArrayAsValue()).content must be equalTo Seq(user)
    }

    "return an ApiError if the Json couldn't be serialized into a class" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok(Json.obj())
        }
      }

      await(testApi.testApiResponseForUnexpectedJson()) must throwA[ApiError[_]].like {
        case e: ApiError[_] =>
          e.content must beNone
          e.getMessage must contain("serialize")
      }
    }

    "return an ApiResponse with a File as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok.sendFile(file("test.txt"))
        }
      }

      await(testApi.testApiResponseWithFileAsValue()).content must haveSameLinesAs(file("test.txt"))
    }

    "return an ApiResponse with a String as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok("test")
        }
      }

      await(testApi.testApiResponseWithStringAsValue()).content must be equalTo "test"
    }

    "return an ApiResponse with Long as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok("123456789012345")
        }
      }

      await(testApi.testApiResponseWithLongAsValue()).content must be equalTo 123456789012345L
    }

    "return an ApiResponse with Int as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok("1234567890")
        }
      }

      await(testApi.testApiResponseWithIntAsValue()).content must be equalTo 1234567890
    }

    "return an ApiResponse with Double as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok("1234567890.00")
        }
      }

      await(testApi.testApiResponseWithDoubleAsValue()).content must be equalTo 1234567890.00
    }

    "return an ApiResponse with Float as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok("1234567890.00")
        }
      }

      await(testApi.testApiResponseWithFloatAsValue()).content must be equalTo 1234567890.00F
    }

    "return an ApiResponse with Boolean as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok("true")
        }
      }

      await(testApi.testApiResponseWithBooleanAsValue()).content must beTrue
    }

    "return an ApiResponse with Byte as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok("1")
        }
      }

      await(testApi.testApiResponseWithByteAsValue()).content must be equalTo 1
    }

    "return an ApiResponse if the String couldn't be casted to a Boolean" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok("not-boolean")
        }
      }

      await(testApi.testApiResponseForUnexpectedPrimitiveType()) must throwA[ApiError[_]].like {
        case e: ApiError[_] =>
          e.content must beNone
          e.getMessage must contain("serialize")
      }
    }

    "return an ApiError with Unit as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError
        }
      }

      await(testApi.testApiErrorWithUnitAsValue()) must throwA[ApiError[Unit]].like {
        case e: ApiError[_] =>
          e.content must beNone
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError with a Json object as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError(Json.toJson(status))
        }
      }

      await(testApi.testApiErrorWithJsonObjectAsValue()) must throwA[ApiError[Status]].like {
        case e: ApiError[_] =>
          e.content must beSome(status)
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError with a Json array as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError(Json.toJson(Seq(status)))
        }
      }

      await(testApi.testApiErrorWithJsonArrayAsValue()) must throwA[ApiError[Seq[Status]]].like {
        case e: ApiError[_] =>
          e.content must beSome(Seq(status))
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError if the Json couldn't be serialized into a class" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError(Json.obj())
        }
      }

      await(testApi.testApiErrorForUnexpectedJson()) must throwA[ApiError[_]].like {
        case e: ApiError[_] =>
          e.content must beNone
          e.getMessage must contain("serialize")
      }
    }

    "return an ApiError with a File as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError.sendFile(file("test.txt"))
        }
      }

      await(testApi.testApiErrorWithFileAsValue()) must throwA[ApiError[File]].like {
        case e: ApiError[_] =>
          e.content must beSome.like {
            case f: File => f must haveSameLinesAs(file("test.txt"))
          }
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError with a String as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError("test")
        }
      }

      await(testApi.testApiErrorWithStringAsValue()) must throwA[ApiError[String]].like {
        case e: ApiError[_] =>
          e.content must beSome("test")
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError with Long as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError("123456789012345")
        }
      }

      await(testApi.testApiErrorWithLongAsValue()) must throwA[ApiError[Long]].like {
        case e: ApiError[_] =>
          e.content must beSome(123456789012345L)
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError with Int as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError("1234567890")
        }
      }

      await(testApi.testApiErrorWithIntAsValue()) must throwA[ApiError[Int]].like {
        case e: ApiError[_] =>
          e.content must beSome(1234567890)
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError with Double as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError("1234567890.00")
        }
      }

      await(testApi.testApiErrorWithDoubleAsValue()) must throwA[ApiError[Double]].like {
        case e: ApiError[_] =>
          e.content must beSome(1234567890.00)
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError with Float as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError("1234567890.00")
        }
      }

      await(testApi.testApiErrorWithFloatAsValue()) must throwA[ApiError[Float]].like {
        case e: ApiError[_] =>
          e.content must beSome(1234567890.00F)
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError with Boolean as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError("true")
        }
      }

      await(testApi.testApiErrorWithBooleanAsValue()) must throwA[ApiError[Boolean]].like {
        case e: ApiError[_] =>
          e.content must beSome(true)
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError with Byte as value" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError("1")
        }
      }

      await(testApi.testApiErrorWithByteAsValue()) must throwA[ApiError[Byte]].like {
        case e: ApiError[_] =>
          e.content must beSome(1)
          e.getMessage must contain(ApiInvoker.ApiResponseError)
      }
    }

    "return an ApiError if the String couldn't be casted to a Boolean" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          InternalServerError("not-boolean")
        }
      }

      await(testApi.testApiErrorForUnexpectedPrimitiveType()) must throwA[ApiError[_]].like {
        case e: ApiError[_] =>
          e.content must beNone
          e.getMessage must contain("serialize")
      }
    }

    "be able to extract typed headers" in new Context {
      val route = Route {
        case ("GET", "/test") => Action { request =>
          Ok.withHeaders(
            "X-STRING" -> "test",
            "X-INT" -> "1234567890",
            "X-LONG" -> "1234567890123456789",
            "X-FLOAT" -> "1234567890.00",
            "X-DOUBLE" -> "1234567890.00",
            "X-BOOLEAN" -> "true"
          )
        }
      }

      val result = await(testApi.testResponseHeaders())
      val headers = TestApi.TestResponseHeaders(result)

      headers.`x-String`() must beSome("test")
      headers.`x-Int`() must beSome(1234567890)
      headers.`x-Long`() must beSome(1234567890123456789L)
      headers.`x-Float`() must beSome(1234567890.00F)
      headers.`x-Double`() must beSome(1234567890.00)
      headers.`x-Boolean`() must beSome(true)
    }
  }

  /**
   * The context.
   */
  trait Context extends Scope {

    /**
     * The route.
     */
    def route: Route

    /**
     * A user.
     */
    lazy val user = User(
      id = 1L,
      name = "Lucky Luke",
      visits = 45,
      roles = Seq("user", "admin"),
      gender = User.Gender.Male,
      activated = true,
      lastLogin = None,
      insertDate = new DateTime(2016, 2, 19, 13, 28, 43, 0).withZone(DateTimeZone.UTC)
    )

    /**
     * A status.
     */
    lazy val status = models.Status("", Some(models.Error("", "")))

    /**
     * The WS client.
     */
    lazy val wsClient = MockWS(route)

    /**
     * Thw API config.
     */
    lazy val apiConfig: ApiConfig = ApiConfig()

    /**
     * The API invoker.
     */
    lazy val apiInvoker: ApiInvoker = new ApiInvoker(apiConfig, wsClient)

    /**
     * The API to test.
     */
    lazy val testApi: TestApi = new TestApi(apiInvoker)

    /**
     * Helper method to await futures.
     */
    def await[A](f: Future[A]) = Await.result(f, 10 seconds)

    /**
     * Helper function to load a file from class path.
     */
    def file(path: String) = {
      val url: URL = Option(this.getClass.getClassLoader.getResource(path)).getOrElse {
        throw new FileNotFoundException("Cannot find test file: " + path)
      }
      new File(url.getFile)
    }

    /**
     * Helper function which converts the given data into a query string format.
     */
    def toQueryString(data: Map[String, Seq[String]]) = {
      data.map { case (k, v) => k -> v.map(v => k -> v) }.values.flatten.map { case (key, value) => key + "=" + value }.mkString("&")
    }
  }
}
