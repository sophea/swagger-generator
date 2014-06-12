/**
 *  Copyright 2014 Reverb, Inc.
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

package com.wordnik.swagger.generator.resource;

import com.wordnik.swagger.util.ValidationException;

import com.wordnik.swagger.generator.exception.ApiException;
import com.wordnik.swagger.generator.exception.BadRequestException;
import com.wordnik.swagger.generator.exception.NotFoundException;
import com.wordnik.swagger.generator.model.ApiResponse;

import javax.ws.rs.ext.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Provider
public class ExceptionWriter implements ExceptionMapper<Exception> {
  public Response toResponse(Exception exception) {
    if (exception instanceof javax.ws.rs.WebApplicationException) {
      javax.ws.rs.WebApplicationException e = (javax.ws.rs.WebApplicationException) exception;
      return Response
          .status(e.getResponse().getStatus())
          .entity(new ApiResponse(e.getResponse().getStatus(),
              exception.getMessage())).build();
    } else if (exception instanceof com.fasterxml.jackson.core.JsonParseException) {
      return Response.status(400)
          .entity(new ApiResponse(400, "bad input")).build();
    } else if (exception instanceof ValidationException) {
      ValidationException e = (ValidationException) exception;
      return Response.status(Status.BAD_REQUEST).entity(e.messages()).build();
    } else if (exception instanceof NotFoundException) {
      return Response
          .status(Status.NOT_FOUND)
          .entity(new ApiResponse(ApiResponse.ERROR, exception
              .getMessage())).build();
    } else if (exception instanceof BadRequestException) {
      return Response
          .status(Status.BAD_REQUEST)
          .entity(new ApiResponse(ApiResponse.ERROR, exception
              .getMessage())).build();
    } else if (exception instanceof ApiException) {
      return Response
          .status(Status.BAD_REQUEST)
          .entity(new ApiResponse(ApiResponse.ERROR, exception
              .getMessage())).build();
    } else {
      return Response.status(500)
          .entity(new ApiResponse(500, "something bad happened"))
          .build();
    }
  }
}