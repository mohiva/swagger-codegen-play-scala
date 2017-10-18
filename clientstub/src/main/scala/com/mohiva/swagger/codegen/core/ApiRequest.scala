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
package com.mohiva.swagger.codegen.core

import com.mohiva.swagger.codegen.core.ApiRequest._
import play.api.libs.json.{ Json, Reads, Writes }

import scala.concurrent.duration._
import scala.reflect.runtime.universe._

/**
 * The API request.
 *
 * @param method        The HTTP request method.
 * @param basePath      The API base path.
 * @param operationPath The API operation path.
 * @param contentType   The content type.
 * @param config        The request specific config.
 * @param responses     The list of responses.
 * @param bodyParam     The body param.
 * @param formParams    The form params.
 * @param pathParams    The path params.
 * @param queryParams   The query params.
 * @param headerParams  The header params.
 * @param credentials   The credentials.
 */
case class ApiRequest(
  method: RequestMethod,
  basePath: String,
  operationPath: String,
  contentType: Option[String],
  config: Config,
  responses: Map[Int, (ResponseState, TypeTag[_], Option[Reads[_]])] = Map.empty,
  bodyParam: Option[Any] = None,
  formParams: Map[String, Any] = Map.empty,
  pathParams: Map[String, Any] = Map.empty,
  queryParams: Map[String, Any] = Map.empty,
  headerParams: Map[String, Any] = Map.empty,
  credentials: Seq[Credentials] = List.empty) {

  /**
   * Adds a Json success response to the request.
   *
   * @param code The HTTP status code.
   * @tparam T The type of the response.
   * @return The request to provide a fluent interface.
   */
  def withJsonSuccessResponse[T: TypeTag: Reads](code: Int): ApiRequest = copy(responses = responses +
    ((code, (ResponseState.Success, implicitly[TypeTag[T]], Some(implicitly[Reads[T]]))))
  )

  /**
   * Adds a Json error response to the request.
   *
   * @param code The HTTP status code.
   * @tparam T The type of the response.
   * @return The request to provide a fluent interface.
   */
  def withJsonErrorResponse[T: TypeTag: Reads](code: Int): ApiRequest = copy(responses = responses +
    ((code, (ResponseState.Error, implicitly[TypeTag[T]], Some(implicitly[Reads[T]]))))
  )

  /**
   * Adds a primitive success response to the request.
   *
   * @param code The HTTP status code.
   * @tparam T The type of the response.
   * @return The request to provide a fluent interface.
   */
  def withPrimitiveSuccessResponse[T: TypeTag](code: Int): ApiRequest = copy(responses = responses +
    ((code, (ResponseState.Success, implicitly[TypeTag[T]], None)))
  )

  /**
   * Adds a primitive error response to the request.
   *
   * @param code The HTTP status code.
   * @tparam T The type of the response.
   * @return The request to provide a fluent interface.
   */
  def withPrimitiveErrorResponse[T: TypeTag](code: Int): ApiRequest = copy(responses = responses +
    ((code, (ResponseState.Error, implicitly[TypeTag[T]], None)))
  )

  /**
   * Adds a default Json success response to the request.
   *
   * @tparam T The type of the response.
   * @return The request to provide a fluent interface.
   */
  def withDefaultJsonSuccessResponse[T: TypeTag: Reads]: ApiRequest = withJsonSuccessResponse[T](0)

  /**
   * Adds a default Json error response to the request.
   *
   * @tparam T The type of the response.
   * @return The request to provide a fluent interface.
   */
  def withDefaultJsonErrorResponse[T: TypeTag: Reads]: ApiRequest = withJsonErrorResponse[T](0)

  /**
   * Adds a default primitive success response to the request.
   *
   * @tparam T The type of the response.
   * @return The request to provide a fluent interface.
   */
  def withDefaultPrimitiveSuccessResponse[T: TypeTag]: ApiRequest = withPrimitiveSuccessResponse[T](0)

  /**
   * Adds a default primitive error response to the request.
   *
   * @tparam T The type of the response.
   * @return The request to provide a fluent interface.
   */
  def withDefaultPrimitiveErrorResponse[T: TypeTag]: ApiRequest = withPrimitiveErrorResponse[T](0)

  /**
   * Adds a Json body to the request.
   *
   * @param body The Json body to add.
   * @return The request to provide a fluent interface.
   */
  def withJsonBody[B: Writes](body: B): ApiRequest = copy(bodyParam = Some(Json.toJson(body)))

  /**
   * Adds an optional Json body to the request.
   *
   * @param body The Json body to add.
   * @return The request to provide a fluent interface.
   */
  def withJsonBody[B: Writes](body: Option[B]): ApiRequest = copy(bodyParam = body.map(j => Json.toJson(j)))

  /**
   * Adds a primitive body to the request.
   *
   * @param body The primitive body to add.
   * @return The request to provide a fluent interface.
   */
  def withPrimitiveBody(body: Any): ApiRequest = copy(bodyParam = Some(body))

  /**
   * Adds a form param to the request.
   *
   * @param name  The param name.
   * @param value The param value.
   * @return The request to provide a fluent interface.
   */
  def withFormParam(name: String, value: Any): ApiRequest = copy(formParams = formParams + (name -> value))

  /**
   * Adds a path param to the request.
   *
   * @param name  The param name.
   * @param value The param value.
   * @return The request to provide a fluent interface.
   */
  def withPathParam(name: String, value: Any): ApiRequest = copy(pathParams = pathParams + (name -> value))

  /**
   * Adds a query param to the request.
   *
   * @param name  The param name.
   * @param value The param value.
   * @return The request to provide a fluent interface.
   */
  def withQueryParam(name: String, value: Any): ApiRequest = copy(queryParams = queryParams + (name -> value))

  /**
   * Adds a header param to the request.
   *
   * @param name  The param name.
   * @param value The param value.
   * @return The request to provide a fluent interface.
   */
  def withHeaderParam(name: String, value: Any): ApiRequest = copy(headerParams = headerParams + (name -> value))

  /**
   * Adds credentials to the request.
   *
   * @param credentials The credentials to add.
   * @return The request to provide a fluent interface.
   */
  def withCredentials(credentials: Credentials): ApiRequest = copy(credentials = this.credentials :+ credentials)
}

/**
 * The `APIRequest` companion object.
 */
object ApiRequest {

  /**
   * A request specific config.
   *
   * Use this to override the global config.
   *
   * @param url     The optional API URL. If this URL is defined then it has precedence over the URL defined in the
   *                Swagger spec and the global config.
   * @param timeout The request timeout. Defaults to None, which means the global config has precedence.
   */
  case class Config(url: Option[String] = None, timeout: Option[FiniteDuration] = None)

  /**
   * The response state.
   */
  sealed trait ResponseState
  object ResponseState {
    case object Success extends ResponseState
    case object Error extends ResponseState
  }

  /**
   * The HTTP request methods.
   */
  sealed trait RequestMethod { def name: String }
  object RequestMethod {
    case object CONNECT extends RequestMethod { val name = "CONNECT" }
    case object DELETE extends RequestMethod { val name = "DELETE" }
    case object GET extends RequestMethod { val name = "GET" }
    case object HEAD extends RequestMethod { val name = "HEAD" }
    case object OPTIONS extends RequestMethod { val name = "OPTIONS" }
    case object PATCH extends RequestMethod { val name = "PATCH" }
    case object POST extends RequestMethod { val name = "POST" }
    case object PUT extends RequestMethod { val name = "PUT" }
    case object TRACE extends RequestMethod { val name = "TRACE" }
  }

  /**
   * Single trait defining a credential that can be transformed to a paramName / paramValue tupple.
   */
  sealed trait Credentials

  /**
   * Basic auth credentials.
   *
   * @param user     The username.
   * @param password The password.
   */
  sealed case class BasicCredentials(user: String, password: String) extends Credentials

  /**
   * The API key.
   *
   * @param value The API key.
   */
  sealed case class ApiKey(value: String)

  /**
   * API key credentials.
   *
   * @param key      The API key.
   * @param name     The name where the key should be transported in query string.
   * @param location The location where the key should be transported.
   */
  sealed case class ApiKeyCredentials(key: ApiKey, name: String, location: ApiKeyLocation) extends Credentials

  /**
   * The location where the API can be defined.
   */
  sealed trait ApiKeyLocation
  object ApiKeyLocations {
    case object QUERY extends ApiKeyLocation
    case object HEADER extends ApiKeyLocation
  }
}
