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

package com.wordnik.swagger.online.configs

import com.wordnik.swagger.generator.model._
import com.wordnik.swagger.codegen.model.ClientOpts

trait ClientConfig {
  val fields: Set[InputOption]
  var properties: Map[String, String]

  def generate(config: ClientOpts): Seq[java.io.File]

  def fieldDefaultValue(key: String): String = {
    properties.getOrElse(key, 
      fields.filter(_.getName == key).headOption match {
        case Some(value) => value.getDefaultValue
        case _ => null
      }
    )
  }
}