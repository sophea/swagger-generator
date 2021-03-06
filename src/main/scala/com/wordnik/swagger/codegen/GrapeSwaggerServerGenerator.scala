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

package com.wordnik.swagger.codegen

import scala.collection.mutable.{ HashMap, ListBuffer }

object GrapeSwaggerServerGenerator extends GrapeSwaggerServerGenerator {
  def main(args: Array[String]) = generateClient(args)
}

class GrapeSwaggerServerGenerator extends BasicScalaGenerator {
  override def templateDir = "grape-swagger/templates"

  // template used for models
  modelTemplateFiles.clear
  modelTemplateFiles ++= Map("entity.mustache" -> ".rb")

  // template used for apis
  apiTemplateFiles ++= Map("api.mustache" -> ".rb")
}
