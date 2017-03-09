/**
 * Original work: Swagger Codegen (https://github.com/swagger-api/swagger-codegen)
 * Copyright 2016 Swagger (http://swagger.io)
 *
 * Derivative work: Swagger Codegen - Play Scala (https://github.com/mohiva/swagger-codegen-play-scala)
 * Modifications Copyright 2016 Mohiva Organisation (license at mohiva dot com)
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

import javax.inject.Inject

import com.mohiva.swagger.codegen.core.ApiParams.{ ArrayValues, CollectionFormats }
import com.mohiva.swagger.codegen.core.ApiRequest._
import com.mohiva.swagger.codegen.core._
import com.mohiva.swagger.codegen.models.{ Status, User }

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.reflectiveCalls

/**
 * A test API.
 *
 * @param apiInvoker The API invoker.
 */
class TestApi @Inject() (apiInvoker: ApiInvoker) {

  /**
   * Test the execution of GET request.
   */
  def testGet(rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[Unit]] = {
    apiInvoker.execute[Unit](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test the execution of POST request.
   */
  def testPost(rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[Unit]] = {
    apiInvoker.execute[Unit](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test the execution of POST request with a body.
   */
  def testPostWithBody(body: User, rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[User]] = {
    apiInvoker.execute[User](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withJsonBody[User](body)
      .withJsonSuccessResponse[User](200)
    )
  }

  /**
   * Test the execution of PUT request.
   */
  def testPut(rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[Unit]] = {
    apiInvoker.execute[Unit](ApiRequest(RequestMethod.PUT, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test the execution of PUT request with a body.
   */
  def testPutWithBody(body: User, rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[User]] = {
    apiInvoker.execute[User](ApiRequest(RequestMethod.PUT, "", "/test", None, rc)
      .withJsonBody[User](body)
      .withJsonSuccessResponse[User](200)
    )
  }

  /**
   * Test the execution of PATCH request.
   */
  def testPatch(rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[Unit]] = {
    apiInvoker.execute[Unit](ApiRequest(RequestMethod.PATCH, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test the execution of PATCH request with a body.
   */
  def testPatchWithBody(body: User, rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[User]] = {
    apiInvoker.execute[User](ApiRequest(RequestMethod.PATCH, "", "/test", None, rc)
      .withJsonBody[User](body)
      .withJsonSuccessResponse[User](200)
    )
  }

  /**
   * Test the execution of DELETE request.
   */
  def testDelete(rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[Unit]] = {
    apiInvoker.execute[Unit](ApiRequest(RequestMethod.DELETE, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test the execution of DELETE request with a body.
   */
  def testDeleteWithBody(body: User, rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[User]] = {
    apiInvoker.execute[User](ApiRequest(RequestMethod.DELETE, "", "/test", None, rc)
      .withJsonBody[User](body)
      .withJsonSuccessResponse[User](200)
    )
  }

  /**
   * Test the execution of CONNECT request.
   */
  def testConnect(rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[Unit]] = {
    apiInvoker.execute[Unit](ApiRequest(RequestMethod.CONNECT, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test the execution of HEAD request.
   */
  def testHead(rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[Unit]] = {
    apiInvoker.execute[Unit](ApiRequest(RequestMethod.HEAD, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test the execution of OPTIONS request.
   */
  def testOptions(rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[Unit]] = {
    apiInvoker.execute[Unit](ApiRequest(RequestMethod.OPTIONS, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test the execution of TRACE request.
   */
  def testTrace(rc: Config = Config())(implicit ec: ExecutionContext): Future[ApiResponse[Unit]] = {
    apiInvoker.execute[Unit](ApiRequest(RequestMethod.TRACE, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test if a request with an empty body will be sent successfully.
   */
  def testRequestWithEmptyBody(rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test if a request with a Json object body will be sent successfully.
   */
  def testRequestWithJsonObjectBody(body: User, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[User]] = {

    apiInvoker.execute[User](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withJsonBody[User](body)
      .withJsonSuccessResponse[User](200)
    )
  }

  /**
   * Test if a request with a Json array body will be sent successfully.
   */
  def testRequestWithJsonArrayBody(body: Seq[User], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Seq[User]]] = {

    apiInvoker.execute[Seq[User]](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withJsonBody[Seq[User]](body)
      .withJsonSuccessResponse[Seq[User]](200)
    )
  }

  /**
   * Test if a request with Some Json body will be sent successfully.
   */
  def testRequestWithSomeJsonBody(body: Option[User], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[User]] = {

    apiInvoker.execute[User](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withJsonBody[User](body)
      .withJsonSuccessResponse[User](200)
    )
  }

  /**
   * Test if a request with None Json body will be sent successfully.
   */
  def testRequestWithNoneJsonBody(body: Option[User], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withJsonBody[User](body)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test if a request with a File body will be sent successfully.
   */
  def testRequestWithFileBody(body: ApiFile, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[ApiFile]] = {

    apiInvoker.execute[ApiFile](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withPrimitiveBody(body)
      .withPrimitiveSuccessResponse[ApiFile](200)
    )
  }

  /**
   * Test if a request with an Int body will be sent successfully.
   */
  def testRequestWithIntBody(body: Int, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withPrimitiveBody(body)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with a String body will be sent successfully.
   */
  def testRequestWithStringBody(body: String, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withPrimitiveBody(body)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with a Boolean body will be sent successfully.
   */
  def testRequestWithBooleanBody(body: Boolean, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withPrimitiveBody(body)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with Some primitive body will be sent successfully.
   */
  def testRequestWithSomePrimitiveBody(body: Option[String], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withPrimitiveBody(body)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with None primitive body will be sent successfully.
   */
  def testRequestWithNonePrimitiveBody(body: Option[String], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withPrimitiveBody(body)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test if a request with multipart form data will be sent successfully.
   */
  def testRequestWithMultipartFormData(
    file: ApiFile,
    files: Seq[ApiFile],
    param: String,
    returnFile: Boolean,
    rc: Config = Config()
  )(
    implicit
    ec: ExecutionContext
  ): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.POST, "", "/test", Some("multipart/form-data; charset=utf-8"), rc)
      .withFormParam("file", file)
      .withFormParam("files", ApiParams.ArrayValues(files, ApiParams.CollectionFormats.MULTI))
      .withFormParam("param", param)
      .withFormParam("returnFile", returnFile)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with form-url-encoded data will be sent successfully.
   */
  def testRequestWithFormURLEncodedData(param1: String, param2: Int, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.POST, "", "/test", Some("application/x-www-form-urlencoded; charset=utf-8"), rc)
      .withFormParam("param1", param1)
      .withFormParam("param2", param2)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with an Int header will be sent successfully.
   */
  def testRequestWithIntHeader(header: Int, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withHeaderParam("X-HEADER", header)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with a String header will be sent successfully.
   */
  def testRequestWithStringHeader(header: String, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withHeaderParam("X-HEADER", header)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with a Boolean header will be sent successfully.
   */
  def testRequestWithBooleanHeader(header: Boolean, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withHeaderParam("X-HEADER", header)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with Some string header will be sent successfully.
   */
  def testRequestWithSomeStringHeader(header: Option[String] = None, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withHeaderParam("X-HEADER", header)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with None string header will be sent successfully.
   */
  def testRequestWithNoneStringHeader(header: Option[String] = None, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withHeaderParam("X-HEADER", header)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test if a request with query parameters will be sent successfully.
   */
  def testRequestWithQueryParameters(param1: String, param2: Int, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param1", param1)
      .withQueryParam("param2", param2)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with array values[CSV] in query parameter will be sent successfully.
   */
  def testRequestWithArrayCsvQueryParameters(param: Seq[String], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param", ArrayValues(param, CollectionFormats.CSV))
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with array values[TSV] in query parameter will be sent successfully.
   */
  def testRequestWithArrayTsvQueryParameters(param: Seq[String], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param", ArrayValues(param, CollectionFormats.TSV))
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with array values[SSV] in query parameter will be sent successfully.
   */
  def testRequestWithArraySsvQueryParameters(param: Seq[String], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param", ArrayValues(param, CollectionFormats.SSV))
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with array values[PIPES] in query parameter will be sent successfully.
   */
  def testRequestWithArrayPipesQueryParameters(param: Seq[String], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param", ArrayValues(param, CollectionFormats.PIPES))
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with array values[MULTI] in query parameter will be sent successfully.
   */
  def testRequestWithArrayMultiQueryParameters(param: Seq[String], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param", ArrayValues(param, CollectionFormats.MULTI))
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with Some array in query parameter will be sent successfully.
   */
  def testRequestWithSomeArrayQueryParameters(param: Option[Seq[String]], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param", ArrayValues(param, CollectionFormats.CSV))
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with None array in query parameter will be sent successfully.
   */
  def testRequestWithNoneArrayQueryParameters(param: Option[Seq[String]], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param", ArrayValues(param, CollectionFormats.CSV))
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test if a request with Some string parameter will be sent successfully.
   */
  def testRequestWithSomeStringQueryParameter(param: Option[String], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param", param)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with None string parameter will be sent successfully.
   */
  def testRequestWithNoneStringQueryParameter(param: Option[String], rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withQueryParam("param", param)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test if a request with path parameters will be sent successfully.
   */
  def testRequestWithPathParameters(param1: String, param2: Int, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.GET, "", "/test/{param1}/{param2}", None, rc)
      .withPathParam("param1", param1)
      .withPathParam("param2", param2)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test if a request with basic auth credentials will be sent successfully.
   */
  def testRequestWithBasicCredentials(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext, basicAuth: ApiRequest.BasicCredentials): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withCredentials(basicAuth)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with API credentials will be sent successfully in the header.
   */
  def testRequestWithAPICredentialsInHeader(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext, apiKey: ApiRequest.ApiKey): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withCredentials(ApiRequest.ApiKeyCredentials(apiKey, "X-AUTH", ApiRequest.ApiKeyLocations.HEADER))
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a request with API credentials will be sent successfully in the query string.
   */
  def testRequestWithAPICredentialsInQueryString(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext, apiKey: ApiRequest.ApiKey): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withCredentials(ApiRequest.ApiKeyCredentials(apiKey, "auth", ApiRequest.ApiKeyLocations.QUERY))
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a response can return Unit as value.
   */
  def testApiResponseWithUnitAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](204)
    )
  }

  /**
   * Test if a request with a String body will be sent successfully.
   */
  def testRequestWithDefaultPrimitiveType(body: String, rc: Config = Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.POST, "", "/test", None, rc)
      .withPrimitiveBody(body)
      .withDefaultPrimitiveSuccessResponse[String]
    )
  }

  /**
   * Test if a response can return a Json object as value.
   */
  def testApiResponseWithJsonObjectAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[User]] = {

    apiInvoker.execute[User](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withJsonSuccessResponse[User](200)
    )
  }

  /**
   * Test if a response can return a Json array as value.
   */
  def testApiResponseWithJsonArrayAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Seq[User]]] = {

    apiInvoker.execute[Seq[User]](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withJsonSuccessResponse[Seq[User]](200)
    )
  }

  /**
   * Test if the client returns an error if the API returns unexpected Json.
   */
  def testApiResponseForUnexpectedJson(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[User]] = {

    apiInvoker.execute[User](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withJsonSuccessResponse[User](200)
    )
  }

  /**
   * Test if a response can return a Json object as value.
   */
  def testApiResponseWithDefaultJsonResponse(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[User]] = {

    apiInvoker.execute[User](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withDefaultJsonSuccessResponse[User]
    )
  }

  /**
   * Test if a response can return a File as value.
   */
  def testApiResponseWithFileAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[ApiFile]] = {

    apiInvoker.execute[ApiFile](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[ApiFile](200)
    )
  }

  /**
   * Test if a response can return a String as value.
   */
  def testApiResponseWithStringAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[String](200)
    )
  }

  /**
   * Test if a response can return a Long as value.
   */
  def testApiResponseWithLongAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Long]] = {

    apiInvoker.execute[Long](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Long](200)
    )
  }

  /**
   * Test if a response can return a Int as value.
   */
  def testApiResponseWithIntAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Int]] = {

    apiInvoker.execute[Int](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Int](200)
    )
  }

  /**
   * Test if a response can return a Double as value.
   */
  def testApiResponseWithDoubleAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Double]] = {

    apiInvoker.execute[Double](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Double](200)
    )
  }

  /**
   * Test if a response can return a Float as value.
   */
  def testApiResponseWithFloatAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Float]] = {

    apiInvoker.execute[Float](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Float](200)
    )
  }

  /**
   * Test if a response can return a Boolean as value.
   */
  def testApiResponseWithBooleanAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Boolean]] = {

    apiInvoker.execute[Boolean](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Boolean](200)
    )
  }

  /**
   * Test if a response can return a Byte as value.
   */
  def testApiResponseWithByteAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Byte]] = {

    apiInvoker.execute[Byte](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Byte](200)
    )
  }

  /**
   * Test if the client returns an error if the API returns an unexpected primitive type.
   */
  def testApiResponseForUnexpectedPrimitiveType(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Boolean]] = {

    apiInvoker.execute[Boolean](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Boolean](200)
    )
  }

  /**
   * Test if a error can return Unit as value.
   */
  def testApiErrorWithUnitAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[Unit](500)
    )
  }

  /**
   * Test if a error can return a Json object as value.
   */
  def testApiErrorWithJsonObjectAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[User]] = {

    apiInvoker.execute[User](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withJsonErrorResponse[Status](500)
    )
  }

  /**
   * Test if a error can return a Json array as value.
   */
  def testApiErrorWithJsonArrayAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Seq[User]]] = {

    apiInvoker.execute[Seq[User]](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withJsonErrorResponse[Seq[Status]](500)
    )
  }

  /**
   * Test if the client returns an error if the API returns unexpected Json.
   */
  def testApiErrorForUnexpectedJson(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[User]] = {

    apiInvoker.execute[User](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withJsonErrorResponse[Status](500)
    )
  }

  /**
   * Test if a error can return a default JSON response.
   */
  def testApiErrorWithDefaultJsonResponse(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[User]] = {

    apiInvoker.execute[User](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withDefaultJsonErrorResponse[Status]
    )
  }

  /**
   * Test if a error can return a File as value.
   */
  def testApiErrorWithFileAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[ApiFile]] = {

    apiInvoker.execute[ApiFile](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[ApiFile](500)
    )
  }

  /**
   * Test if a error can return a String as value.
   */
  def testApiErrorWithStringAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[String]] = {

    apiInvoker.execute[String](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[String](500)
    )
  }

  /**
   * Test if a error can return a Long as value.
   */
  def testApiErrorWithLongAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Long]] = {

    apiInvoker.execute[Long](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[Long](500)
    )
  }

  /**
   * Test if a error can return a Int as value.
   */
  def testApiErrorWithIntAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Int]] = {

    apiInvoker.execute[Int](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[Int](500)
    )
  }

  /**
   * Test if a error can return a Double as value.
   */
  def testApiErrorWithDoubleAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Double]] = {

    apiInvoker.execute[Double](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[Double](500)
    )
  }

  /**
   * Test if a error can return a Float as value.
   */
  def testApiErrorWithFloatAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Float]] = {

    apiInvoker.execute[Float](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[Float](500)
    )
  }

  /**
   * Test if a error can return a Boolean as value.
   */
  def testApiErrorWithBooleanAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Boolean]] = {

    apiInvoker.execute[Boolean](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[Boolean](500)
    )
  }

  /**
   * Test if a error can return a Byte as value.
   */
  def testApiErrorWithByteAsValue(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Byte]] = {

    apiInvoker.execute[Byte](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[Byte](500)
    )
  }

  /**
   * Test if the client returns an error if the API returns an unexpected primitive type.
   */
  def testApiErrorForUnexpectedPrimitiveType(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Boolean]] = {

    apiInvoker.execute[Boolean](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveErrorResponse[Boolean](500)
    )
  }

  /**
   * Test if the client returns an error for a default primitive type.
   */
  def testApiErrorForDefaultPrimitiveType(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Boolean]] = {

    apiInvoker.execute[Boolean](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withDefaultPrimitiveErrorResponse[Boolean]
    )
  }

  /**
   * Test if the client returns extractable headers.
   */
  def testResponseHeaders(rc: ApiRequest.Config = ApiRequest.Config())(
    implicit
    ec: ExecutionContext): Future[ApiResponse[Unit]] = {

    apiInvoker.execute[Unit](ApiRequest(RequestMethod.GET, "", "/test", None, rc)
      .withPrimitiveSuccessResponse[Unit](200)
    )
  }
}

/**
 * The companion object.
 */
object TestApi {

  /**
   * Helper to extract headers for the `testResponseHeaders` method.
   */
  case class TestResponseHeaders(r: { def headers: Map[String, Seq[String]] }) {
    def `x-String`(index: Int = 0) = ApiHeaderExtractor(r.headers).asString("X-STRING", index)
    def `x-Int`(index: Int = 0) = ApiHeaderExtractor(r.headers).asInt("X-INT", index)
    def `x-Long`(index: Int = 0) = ApiHeaderExtractor(r.headers).asLong("X-LONG", index)
    def `x-Float`(index: Int = 0) = ApiHeaderExtractor(r.headers).asFloat("X-FLOAT", index)
    def `x-Double`(index: Int = 0) = ApiHeaderExtractor(r.headers).asDouble("X-DOUBLE", index)
    def `x-Boolean`(index: Int = 0) = ApiHeaderExtractor(r.headers).asBoolean("X-BOOLEAN", index)
  }
}
