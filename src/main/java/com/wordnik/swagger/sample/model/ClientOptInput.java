package com.wordnik.swagger.sample.model;

import com.wordnik.swagger.codegen.model.ClientOpts;

import com.wordnik.swagger.annotations.*;

import com.fasterxml.jackson.databind.JsonNode;

public class ClientOptInput {
  private ClientOpts opts;
  private JsonNode model;

  public void setOpts(ClientOpts opts) {
    this.opts = opts;
  }

  public ClientOpts getOpts() {
    return opts;
  }

  public void setModel(JsonNode model) {
    this.model = model;
  }

  @ApiModelProperty(hidden = true)
  public JsonNode getModel() {
    return model;
  }
}