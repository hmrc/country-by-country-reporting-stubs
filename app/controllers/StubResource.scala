/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import play.api.http.ContentTypes.JSON
import play.api.mvc.Result
import play.api.mvc.Results._

import java.io.InputStream
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

trait StubResource {

  def jsonResourceAsResponse(path: String): Result = resourceAsResponse(path, JSON)

  def jsonAsyncResourceResponse(path: String)(implicit ec: ExecutionContext): Future[Result] = Future[Result] {
    resourceAsResponse(path, JSON)
  }

  private def readStreamToString(is: InputStream): String =
    try Source.fromInputStream(is).mkString
    finally is.close()

  def findResource(path: String): Option[String] =
    Option(getClass.getResourceAsStream(path)) map { is =>
      readStreamToString(is)
    }

  def resourceAsResponse(path: String, mimeType: String): Result =
    findResource(path) match {
      case Some(content) => Ok(content).as(mimeType)
      case _             => NotFound
    }

}
