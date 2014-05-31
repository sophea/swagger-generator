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

package com.wordnik.swagger.generator.model;

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