package com.wordnik.swagger.sample.resource;

import com.wordnik.swagger.annotations.*;
import com.wordnik.swagger.sample.model.*;
import com.wordnik.swagger.sample.exception.BadRequestException;
import com.wordnik.swagger.online.Generator;

import com.wordnik.swagger.codegen.model.ClientOpts;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/gen")
@Api(value = "/gen", description = "Resource for generating swagger components")
public class SwaggerResource {
  @POST
  @Path("/clients/{language}")
  @Produces({"application/zip", "application/json"})
  @ApiOperation(value = "Generates a client library based on the config",
    notes = "The model representing this is not accurate, it needs to contain a consolidated JSON structure")
  public Response generateClient(
    @ApiParam(value = "The target language for the client library", allowableValues = "android,java,php,objc,docs", required = true) @PathParam("language") String language,
    @ApiParam(value = "Configuration for building the client library", required = true) ClientOptInput opts) throws Exception {
    System.out.println(opts);
    String filename = Generator.generateClient(language, opts);

    if(filename != null) {
      byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(new java.io.File(filename));

      return Response.ok(bytes, MediaType.APPLICATION_OCTET_STREAM)
            .header("content-disposition","attachment; filename = " + language + "-client.zip")
            .build();
    }
    else {
      return Response.status(500).build();
    }
  }

  @GET
  @Path("/clients")
  @ApiOperation(value = "Gets languages supported by the client generator",
    response = String.class,
    responseContainer = "List")
  public Response clientOptions() {
    String[] languages = {"android", "java", "php", "objc", "docs"};
    return Response.ok().entity(languages).build();
  }

  @GET
  @Path("/clients/{language}")
  @ApiOperation(value = "Gets options for a client generation",
    notes = "Values which are not required will use the provided default values",
    response = InputOption.class,
    responseContainer = "List")
  public Response clientLibraryOptions(
    @ApiParam(value = "The target language for the client library", allowableValues = "android,java,php,objc,docs", required = true) @PathParam("language") String language) {
    return Response.ok().entity(Generator.clientOptions(language)).build();
  }

  @GET
  @Path("/servers")
  @ApiOperation(value = "Gets languages supported by the server generator",
    response = String.class,
    responseContainer = "List")
  public Response serverOptions() {
    String[] languages = {"jaxrs","nodejs"};
    return Response.ok().entity(languages).build();
  }

  @GET
  @Path("/servers/{language}")
  @ApiOperation(value = "Gets options for a server generation",
    notes = "Values which are not required will use the provided default values",
    response = InputOption.class,
    responseContainer = "List")
  public Response serverFrameworkOptions(
    @ApiParam(value = "The target framework for the client library", allowableValues = "jaxrs,nodejs", required = true) @PathParam("language") String framework) {
    return Response.ok().entity(Generator.serverOptions(framework)).build();
  }

  @POST
  @Path("/servers/{framework}")
  @ApiOperation(value = "Generates a server library for the supplied server framework",
    notes = "The model representing this is not accurate, it needs to contain a consolidated JSON structure")
  public Response generateServerForLanguage(
    @ApiParam(value = "framework", allowableValues = "jaxrs,nodejs", required = true) @PathParam("framework") String framework,
    @ApiParam(value = "parameters", required = true) ClientOptInput opts)
      throws Exception {
    if(framework == null)
      throw new BadRequestException(400, "Framework is required");
    String filename = Generator.generateServer(framework, opts);
    if(filename != null) {
      byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(new java.io.File(filename));

      return Response.ok(bytes, MediaType.APPLICATION_OCTET_STREAM)
            .header("content-disposition","attachment; filename = " + framework + "-server.zip")
            .build();
    }
    else {
      return Response.status(500).build();
    }
  }
}
