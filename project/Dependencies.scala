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
import sbt._

object Dependencies {

  object Versions {
    val crossScala = Seq("2.12.3", "2.11.11")
    val scalaVersion = crossScala.head
  }

  val resolvers = Seq(
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
    "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  )

  object Library {

    object Play {
      val version = "2.6.6"
      val ws = "com.typesafe.play" %% "play-ahc-ws" % version
      val json = "com.typesafe.play" %% "play-json" % version
      val test = "com.typesafe.play" %% "play-test" % version
      val specs2 = "com.typesafe.play" %% "play-specs2" % version
      object Specs2 {
        private val version = "3.8.9"
        val matcherExtra = "org.specs2" %% "specs2-matcher-extra" % version
        val mock = "org.specs2" %% "specs2-mock" % version
      }
    }

    val playJsonExtension = "ai.x" %% "play-json-extensions" % "0.10.0"
    val playWSMock = "de.leanovate.play-mockws" %% "play-mockws" % "2.6.2"
    val swaggerCodegen = "io.swagger" % "swagger-codegen" % "2.2.3"
    val javaxInject = "javax.inject" % "javax.inject" % "1"
    val testNG = "org.testng" % "testng" % "6.8"
  }
}
