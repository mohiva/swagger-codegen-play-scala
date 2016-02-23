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

import java.io.{ File, FileOutputStream }
import javax.inject.Inject

import com.mohiva.swagger.codegen.core.ApiInvoker._
import com.mohiva.swagger.codegen.core.ApiRequest._
import com.mohiva.swagger.codegen.core.PlayRequest._
import play.api.libs.ws._

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.{ implicitConversions, reflectiveCalls }
import scala.reflect.runtime.universe.{ Type, TypeTag, typeOf }
import scala.util.{ Failure, Success, Try }

/**
 * The API invoker.
 *
 * @param config   The API config.
 * @param wsClient The Play WS client.
 */
class ApiInvoker @Inject() (config: ApiConfig, wsClient: WSClient) {

  /**
   * Executes the request and returns a response.
   *
   * @param apiRequest The request to send.
   * @param ec         The execution context.
   * @tparam C The type of the response content.
   * @return The response.
   */
  def execute[C](apiRequest: ApiRequest)(
    implicit ec: ExecutionContext): Future[ApiResponse[C]] = {

    val playRequest = apiRequest.toPlay(config, wsClient)
    playRequest.execute().flatMap { response =>
      Future.fromTry(parseResponse(apiRequest, response))
    }
  }

  /**
   * Tries to parses the response.
   *
   * @param apiRequest The API request.
   * @param response The response to parse.
   * @tparam C The type of the expected response.
   * @return The parsed response on success or an error on failure.
   */
  private def parseResponse[C](apiRequest: ApiRequest, response: WSResponse): Try[ApiResponse[C]] = {
    apiRequest.responses.get(response.status) match {
      // Parse success response as Unit
      case Some((ResponseState.Success, TypeTag.Unit, _)) =>
        Success(ApiResponse[C](response.status, ().asInstanceOf[C], response.allHeaders))

      // Parse success response as Json
      case Some((ResponseState.Success, tag, Some(reads))) =>
        serialize(response, tag)(response.json.as(reads)) { result =>
          Success(ApiResponse(response.status, result.asInstanceOf[C], response.allHeaders))
        }

      // Parse success response as File
      case Some((ResponseState.Success, tag, None)) if tag.tpe <:< typeOf[File] =>
        serialize(response, tag)(createTempFile(response.bodyAsBytes)) { result =>
          Success(ApiResponse(response.status, result.asInstanceOf[C], response.allHeaders))
        }

      // Parse success response as primitive type
      case Some((ResponseState.Success, tag, None)) =>
        serialize(response, tag)(castValue(response.body, tag.tpe)) { result =>
          Success(ApiResponse(response.status, result.asInstanceOf[C], response.allHeaders))
        }

      // Parse error response as Unit
      case Some((ResponseState.Error, TypeTag.Unit, _)) =>
        Failure(ApiError(response.status, ApiResponseError, None, headers = response.allHeaders))

      // Parse error response as Json
      case Some((ResponseState.Error, tag, Some(reads))) =>
        serialize(response, tag)(response.json.as(reads)) { result =>
          Failure(ApiError(response.status, ApiResponseError, Some(result), headers = response.allHeaders))
        }

      // Parse error response as File
      case Some((ResponseState.Error, tag, None)) if tag.tpe <:< typeOf[File] =>
        serialize(response, tag)(createTempFile(response.bodyAsBytes)) { result =>
          Failure(ApiError(response.status, ApiResponseError, Some(result), headers = response.allHeaders))
        }

      // Parse error response as primitive type
      case Some((ResponseState.Error, tag, None)) =>
        serialize(response, tag)(castValue(response.body, tag.tpe)) { result =>
          Failure(ApiError(response.status, ApiResponseError, Some(result), headers = response.allHeaders))
        }

      // Unexpected response
      case None =>
        val message = UnexpectedStatusCodeError.format(response.status)
        Failure(ApiError(response.status, message, None, headers = response.allHeaders))
    }
  }

  /**
   * Serialize a response.
   *
   * @param response The WS response.
   * @param tag The type tag of the response value.
   * @param op The serialization operation.
   * @param r The success response.
   * @tparam T The type of the result.
   * @return The parsed response on success or an error on failure.
   */
  private def serialize[T](response: WSResponse, tag: TypeTag[_])(op: => Any)(r: Any => Try[ApiResponse[T]]): Try[ApiResponse[T]] = {
    Try(op) match {
      case Success(result) => r(result)
      case Failure(e) =>
        val runtimeClass = tag.mirror.runtimeClass(tag.tpe)
        val message = ResponseSerializationError.format(response.body, runtimeClass)
        Failure(ApiError(response.status, message, None, e, response.allHeaders))
    }
  }

  /**
   * Casts a value to the expected type.
   *
   * @param expectedType The expected type.
   * @param value        The value to cast.
   * @return The value with casted to the expected type.
   */
  private def castValue(value: Any, expectedType: Type): Any = {
    import scala.reflect.runtime.currentMirror
    import scala.reflect.runtime.universe._
    val valueType = if (value != null) currentMirror.classSymbol(value.getClass).toType else typeOf[Null]
    try {
      expectedType match {
        case _ if valueType =:= typeOf[Null] => value
        case _ if valueType =:= typeOf[None.type] => value
        case t: Type if t <:< typeOf[Option[Any]] => try {
          val TypeRef(_, _, args) = t
          Some(castValue(value, args.head))
        } catch {
          case e: Exception => None
        }
        case t: Type if t =:= typeOf[String] => value.toString
        case t: Type if t =:= typeOf[Long] => value.toString.trim.toLong
        case t: Type if t =:= typeOf[Int] => value.toString.trim.toInt
        case t: Type if t =:= typeOf[Double] => value.toString.trim.toDouble
        case t: Type if t =:= typeOf[Float] => value.toString.trim.toFloat
        case t: Type if t =:= typeOf[Boolean] => value.toString.trim.toBoolean
        case t: Type if t =:= typeOf[Byte] => value.toString.trim.toByte
        case t: Type => throw new RuntimeException("Unexpected type: " + t)
      }
    } catch {
      case e: Exception =>
        throw new RuntimeException(s"Cannot cast type `$valueType` to expected type `$expectedType`", e)
    }
  }

  /**
   * Creates a temporary file.
   *
   * @param bytes The file content.
   * @return a reference to the temporary file.
   */
  private def createTempFile(bytes: Array[Byte]) = {
    val temp = File.createTempFile("swagger_client", ".tmp")
    val fileOutFile: FileOutputStream = new FileOutputStream(temp)
    fileOutFile.write(bytes)
    fileOutFile.close()
    temp.deleteOnExit()
    temp
  }
}

/**
 * The companion object.
 */
object ApiInvoker {

  /**
   * Some error messages.
   */
  val ApiResponseError = "Retrieved error from API"
  val ResponseSerializationError = "Couldn't serialize response: %s; to type: %s"
  val UnexpectedStatusCodeError = "API returns unexpected status code: %s"
}
