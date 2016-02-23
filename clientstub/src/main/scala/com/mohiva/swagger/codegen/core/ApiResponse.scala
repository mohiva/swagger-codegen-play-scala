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

import scala.util.Try

/**
 * A successful API response.
 *
 * @param code    The HTTP status code.
 * @param content The response content.
 * @param headers The response headers.
 * @tparam T The type of the response content.
 */
case class ApiResponse[T](code: Int, content: T, headers: Map[String, Seq[String]] = Map.empty)

/**
 * A API error.
 *
 * @param code    The HTTP status code.
 * @param message The error message.
 * @param content The response content.
 * @param cause   The cause.
 * @param headers The response headers.
 * @tparam T The type of the response content.
 */
case class ApiError[T](
  code: Int,
  message: String,
  content: Option[T],
  cause: Throwable = null,
  headers: Map[String, Seq[String]] = Map.empty)
  extends RuntimeException(s"($code) $message.${content.map(s => s" Content : $s").getOrElse("")}", cause)

/**
 * An helper that can extract headers from a response.
 *
 * @param headers A list of headers.
 */
case class ApiHeaderExtractor(headers: Map[String, Seq[String]]) {

  /**
   * Gets a header as string.
   *
   * @param name  The name of the header to return.
   * @param index The index of the value to select in the header sequence.
   * @return The header value as string.
   */
  def asString(name: String, index: Int = 0): Option[String] = headers.get(name).flatMap { seq =>
    Try(seq(index)).toOption
  }

  /**
   * Gets a header as int.
   *
   * @param name  The name of the header to return.
   * @param index The index of the value to select in the header sequence.
   * @return The header value as int.
   */
  def asInt(name: String, index: Int = 0): Option[Int] = castedHeader(name, index, java.lang.Integer.parseInt)

  /**
   * Gets a header as long.
   *
   * @param name  The name of the header to return.
   * @param index The index of the value to select in the header sequence.
   * @return The header value as long.
   */
  def asLong(name: String, index: Int = 0): Option[Long] = castedHeader(name, index, java.lang.Long.parseLong)

  /**
   * Gets a header as float.
   *
   * @param name  The name of the header to return.
   * @param index The index of the value to select in the header sequence.
   * @return The header value as float.
   */
  def asFloat(name: String, index: Int = 0): Option[Float] = castedHeader(name, index, java.lang.Float.parseFloat)

  /**
   * Gets a header as double.
   *
   * @param name  The name of the header to return.
   * @param index The index of the value to select in the header sequence.
   * @return The header value as double.
   */
  def asDouble(name: String, index: Int = 0): Option[Double] = castedHeader(name, index, java.lang.Double.parseDouble)

  /**
   * Gets a header as boolean.
   *
   * @param name  The name of the header to return.
   * @param index The index of the value to select in the header sequence.
   * @return The header value as boolean.
   */
  def asBoolean(name: String, index: Int = 0): Option[Boolean] = castedHeader(name, index, java.lang.Boolean.parseBoolean)

  /**
   * Tries to cast the header value to the appropriated type.
   *
   * @param name       The name of the header to cast.
   * @param index      The index of the value to select in the header sequence.
   * @param conversion The cast function.
   * @return The header value casted by the given conversion.
   */
  private def castedHeader[U](name: String, index: Int = 0, conversion: String => U): Option[U] = {
    Try {
      asString(name, index).map(conversion)
    }.get
  }
}
