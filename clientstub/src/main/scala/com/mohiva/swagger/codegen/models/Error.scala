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
package com.mohiva.swagger.codegen.models

/**
 * A Json model to test.
 */
case class Error(
  code: String,
  details: String
)

/**
 * The companion object.
 */
object Error {
  import ai.x.play.json.Jsonx
  import play.api.libs.json.OFormat

  /**
   * Converts a [[Error]] class into a JSON object.
   */
  implicit val jsonFormat: OFormat[Error] = Jsonx.formatCaseClass[Error]
}
