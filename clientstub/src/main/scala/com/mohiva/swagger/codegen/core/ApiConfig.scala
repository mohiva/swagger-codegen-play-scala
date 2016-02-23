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

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * The API configuration.
 *
 * @param url             The optional API URL. If this URL is defined then it has precedence over the URL defined in the Swagger spec.
 * @param requestTimeout  The request timeout. Defaults to 20 seconds.
 */
case class ApiConfig(url: Option[String] = None, requestTimeout: FiniteDuration = 20 seconds)
