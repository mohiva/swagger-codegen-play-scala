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

import com.mohiva.swagger.codegen.core.ApiJsonFormats._
import org.cvogt.play.json.Jsonx
import org.joda.time.DateTime

/**
 * A Json model to test.
 */
case class User(
  id: Long,
  name: String,
  visits: Int,
  roles: Seq[String],
  gender: User.Gender,
  activated: Boolean,
  lastLogin: Option[DateTime],
  insertDate: DateTime)

/**
 * The companion object.
 */
object User {

  /**
   * Converts a [[User]] class into a JSON object.
   */
  implicit val jsonFormat = Jsonx.formatCaseClass[User]

  /**
   * The `Gender` enum.
   */
  type Gender = Gender.Value
  object Gender extends Enumeration {
    val Male = Value("Male")
    val Female = Value("Female")
  }
}
