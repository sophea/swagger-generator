/**
 *  Copyright 2012 Wordnik, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.wordnik.swagger.online

import com.wordnik.swagger.util._

import javax.ws.rs.ext.{ ExceptionMapper, Provider }
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

@Provider
class ExceptionWriter extends ExceptionMapper[Exception] {
  def toResponse(e: Exception): Response = {
    e match {
      case e: ValidationException =>
        Response.status(Status.BAD_REQUEST).entity(e.messages).build
      case _ =>
        Response.status(Status.INTERNAL_SERVER_ERROR).entity("a system error occured").build
    }
  }
}

