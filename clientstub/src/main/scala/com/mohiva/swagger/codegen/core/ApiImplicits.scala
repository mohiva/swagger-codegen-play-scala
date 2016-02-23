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

import java.io.File
import java.nio.file.{ Files, Paths }

import com.mohiva.swagger.codegen.core.ApiRequest._
import org.joda.time.{ DateTime, DateTimeZone }
import play.api.http.{ HeaderNames, MediaType, Writeable }
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{ Codec, MultipartFormData }

import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect.ClassTag
import scala.util.{ Failure, Success, Try }

/**
 * Some default Json formats.
 */
object ApiJsonFormats {

  /**
   * Converts [[org.joda.time.DateTime]] object to JSON and vice versa.
   */
  implicit object DateTimeFormat extends Format[DateTime] {
    def reads(json: JsValue): JsResult[DateTime] = json.asOpt[String] map { str =>
      Try(new DateTime(str, DateTimeZone.UTC)) match {
        case Success(date) => JsSuccess(date)
        case Failure(e) => JsError("format")
      }
    } getOrElse JsError("string")

    def writes(o: DateTime): JsValue = JsString(o.toString)
  }

  /**
   * Converts an enum value to Json and vice versa.
   */
  implicit def enumValueFormat[T <: Enumeration: ClassTag]: Format[T#Value] = new Format[T#Value] {
    def reads(json: JsValue): JsResult[T#Value] = {
      val c = implicitly[ClassTag[T]].runtimeClass
      val enum = c.getField("MODULE$").get(null).asInstanceOf[T]
      json.asOpt[String].map { value =>
        enum.values.find(_.toString == value) match {
          case Some(v) => JsSuccess(v)
          case _ => JsError("invalid")
        }
      } getOrElse JsError("string")
    }

    def writes(value: T#Value): JsValue = JsString(value.toString)
  }
}

/**
 * Some API param types and implicits.
 */
object ApiParams {

  /**
   * Extractor used to unapply empty values only in pattern matching.
   */
  object EmptyValue {
    def unapply(n: Any): Option[Any] = n match {
      case (None | Seq() | "" | ArrayValues(Seq(), _)) => Some(n)
      case _ => None
    }
  }

  /**
   * Case class used to unapply numeric values only in pattern matching.
   */
  sealed case class NumericValue(value: String)
  object NumericValue {
    def unapply(n: Any): Option[NumericValue] = n match {
      case (_: Int | _: Long | _: Float | _: Double | _: Boolean | _: Byte) => Some(NumericValue(String.valueOf(n)))
      case _ => None
    }
  }

  /**
   * Used for params being arrays.
   */
  sealed case class ArrayValues(values: Seq[Any], format: CollectionFormat = CollectionFormats.CSV)
  object ArrayValues {
    def apply(values: Option[Seq[Any]], format: CollectionFormat): ArrayValues =
      ArrayValues(values.getOrElse(Seq.empty), format)

    def apply(values: Option[Seq[Any]]): ArrayValues = ArrayValues(values, CollectionFormats.CSV)
  }

  /**
   * Defines how arrays should be rendered in query strings.
   */
  sealed trait CollectionFormat
  object CollectionFormats {
    trait MergedArrayFormat extends CollectionFormat {
      def separator: String
    }

    case object CSV extends MergedArrayFormat { override val separator = "," }
    case object TSV extends MergedArrayFormat { override val separator = "\t" }
    case object SSV extends MergedArrayFormat { override val separator = " " }
    case object PIPES extends MergedArrayFormat { override val separator = "|" }
    case object MULTI extends CollectionFormat
  }

  /**
   * Normalize `Map[String, Any]` parameters so that it can be used without hassle.
   *
   * None values will be filtered out.
   */
  implicit class AnyMapNormalizers(val m: Map[String, Any]) {
    def normalize: Seq[(String, Any)] = m.mapValues(_.normalize).toSeq.flatMap {
      case (name, EmptyValue(_)) => Seq()
      case (name, ArrayValues(values, format)) if format == CollectionFormats.MULTI => values.map { v => name -> v }
      case (k, v) => Seq(k -> v)
    }
  }

  /**
   * Normalize `Any` parameters so that it can be used without hassle.
   */
  implicit class AnyParametersNormalizer(value: Any) {
    import CollectionFormats._

    def normalize = {
      def n(value: Any): Any = value match {
        case Some(opt) => n(opt)
        case arr @ ArrayValues(values, CollectionFormats.MULTI) => arr.copy(values.map(n))
        case ArrayValues(values, format: MergedArrayFormat) => values.mkString(format.separator)
        case sequence: Seq[Any] => n(ArrayValues(sequence))
        case NumericValue(numeric) => numeric.value
        case v => v
      }

      n(value)
    }
  }
}

/**
 * The companion object of the [[PlayRequest]].
 */
object PlayRequest {
  import ApiParams._

  /**
   * An implicits that allows to convert an API request into a Play request.
   *
   * @param apiRequest The API request to convert.
   */
  implicit class ApiRequestToPlayRequest(apiRequest: ApiRequest) {

    /**
     * Converts an API request to a Play request.
     *
     * @param config   The global API config.
     * @param wsClient The Play WS client.
     * @param ec       The execution context.
     * @return A Play request.
     */
    def toPlay(config: ApiConfig, wsClient: WSClient)(implicit ec: ExecutionContext) = new {

      /**
       * A request pipeline.
       */
      type Pipeline = PartialFunction[WSRequest, WSRequest]

      /**
       * The request pipeline.
       */
      private val requestPipeline =
        requestTimeoutPipeline andThen
          requestMethodPipeline andThen
          authenticationPipeline andThen
          headerPipeline andThen
          queryPipeline

      /**
       * The complete configured request to execute.
       */
      private val request = requestPipeline(wsClient.url(url))

      /**
       * Executes the request.
       *
       * @return A response.
       */
      def execute(): Future[WSResponse] = request.execute()

      /**
       * Builds the URL to which the request should be sent.
       *
       * Searches in the following locations in the following order:
       * - request config
       * - global config
       * - Swagger SPEC file
       *
       * @return The URL to which the request should be sent.
       */
      private def url: String = {
        val base = apiRequest.config.url.getOrElse(config.url.getOrElse(apiRequest.basePath))
        val path = apiRequest.operationPath.replaceAll("\\{format\\}", "json")
        val pathParams = apiRequest.pathParams.normalize
          .foldLeft(path) {
            case (p, (name, value)) => p.replaceAll(s"\\{$name\\}", String.valueOf(value))
          }

        base.stripSuffix("/") + pathParams
      }

      /**
       * Adds the request method, and the body based on the this method.
       *
       * The body will be sent only for the following request methods:
       * - POST
       * - PUT
       * - PATCH
       * - DELETE
       */
      private def requestMethodPipeline: Pipeline = {
        case wsRequest =>
          apiRequest.method match {
            case RequestMethod.POST | RequestMethod.PUT | RequestMethod.PATCH | RequestMethod.DELETE =>
              bodyPipeline(wsRequest.withMethod(apiRequest.method.name))
            case _ => wsRequest.withMethod(apiRequest.method.name)
          }
      }

      /**
       * Adds the request timeout.
       */
      private def requestTimeoutPipeline: Pipeline = {
        case wsRequest =>
          wsRequest.withRequestTimeout(config.requestTimeout.toMillis)
      }

      /**
       * Ads the body.
       *
       * First it checks the `bodyParam` of the API request and then it checks the form part of the request.
       */
      private def bodyPipeline: Pipeline = {
        case wsRequest =>
          apiRequest.bodyParam.normalize match {
            case file: File => wsRequest.withBody(FileBody(file))
            case NumericValue(numeric) => wsRequest.withBody(numeric.value)
            case string: String => wsRequest.withBody(String.valueOf(string))
            case json: JsValue => wsRequest.withBody(json)
            case _ =>
              val contentType = apiRequest.contentType.flatMap(MediaType.parse.apply).map(mt => mt.mediaType + "/" + mt.mediaSubType)
              apiRequest.formParams.normalize match {
                case p if p.isEmpty => wsRequest
                case p if contentType.contains("multipart/form-data") =>
                  import PlayRequest.MultipartFormDataWritable._
                  wsRequest.withBody(p.foldLeft(MultipartFormData(Map[String, Seq[String]](), Seq[FilePart[File]](), Seq(), Seq())) {
                    case (formData, (key, value)) =>
                      value match {
                        case f: File => formData.copy(files = formData.files :+ FilePart(key, f.getName, None, f))
                        case _ => formData.copy(dataParts = formData.dataParts + (key -> Seq(String.valueOf(value))))
                      }
                  })
                case p => // default: application/x-www-form-urlencoded
                  wsRequest.withBody(p.toMap.mapValues(v => Seq(String.valueOf(v))))
              }
          }
      }

      /**
       * Adds the authentication.
       */
      private def authenticationPipeline: Pipeline = {
        case wsRequest =>
          apiRequest.credentials.foldLeft(wsRequest) {
            case (req, BasicCredentials(username, password)) =>
              req.withAuth(username, password, WSAuthScheme.BASIC)
            case (req, ApiKeyCredentials(keyValue, keyName, ApiKeyLocations.HEADER)) =>
              req.withHeaders(keyName -> keyValue.value)
            case (req, _) => req
          }
      }

      /**
       * Adds the headers.
       */
      private def headerPipeline: Pipeline = {
        case wsRequest =>
          wsRequest.withHeaders(apiRequest.headerParams.normalize.map { case (k, v) => k -> String.valueOf(v) }: _*)
      }

      /**
       * Adds the query params.
       */
      private def queryPipeline: Pipeline = {
        case wsRequest =>
          val queryParams = apiRequest.credentials.foldLeft(apiRequest.queryParams) {
            case (params, ApiKeyCredentials(key, keyName, ApiKeyLocations.QUERY)) =>
              params + (keyName -> key.value)
            case (params, _) => params
          }.normalize.map { case (k, v) => k -> String.valueOf(v) }

          wsRequest.withQueryString(queryParams.toList: _*)
      }
    }
  }

  /**
   * A Play writable for multipart/form-data.
   *
   * @see http://tech.fongmun.com/post/125479939452/test-multipartformdata-in-play
   */
  object MultipartFormDataWritable {
    val boundary = "--------ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

    /**
     * Create the form data parts.
     *
     * @param data The form data.
     * @return The form data part as string.
     */
    def formatDataParts(data: Map[String, Seq[String]]) = {
      val dataParts = data.flatMap {
        case (key, values) =>
          values.map { value =>
            val name = s""""$key""""
            s"--$boundary\r\n${HeaderNames.CONTENT_DISPOSITION}: form-data; name=$name\r\n\r\n$value\r\n"
          }
      }.mkString("")
      Codec.utf_8.encode(dataParts)
    }

    /**
     * Creates the file header parts.
     *
     * @param file The file data.
     * @return The file data parts as string.
     */
    def filePartHeader(file: FilePart[File]) = {
      val name = s""""${file.key}""""
      val filename = s""""${file.filename}""""
      val contentType = file.contentType.map { ct =>
        s"${HeaderNames.CONTENT_TYPE}: $ct\r\n"
      }.getOrElse("")
      Codec.utf_8.encode(s"--$boundary\r\n${HeaderNames.CONTENT_DISPOSITION}: form-data; name=$name; filename=$filename\r\n$contentType\r\n")
    }

    /**
     * The writeable.
     *
     * @param ec The execution context.
     */
    implicit def writeable(implicit ec: ExecutionContext): Writeable[MultipartFormData[File]] = {
      Writeable[MultipartFormData[File]](
        transform = { form: MultipartFormData[File] =>
          formatDataParts(form.dataParts) ++
            form.files.flatMap { file =>
              val fileBytes = Files.readAllBytes(Paths.get(file.ref.getAbsolutePath))
              filePartHeader(file) ++ fileBytes ++ Codec.utf_8.encode("\r\n")
            } ++
            Codec.utf_8.encode(s"--$boundary--")
        },
        contentType = Some(s"multipart/form-data; boundary=$boundary")
      )
    }
  }
}
