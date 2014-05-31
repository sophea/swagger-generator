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
import com.wordnik.swagger.generator.exception._

import com.wordnik.swagger.codegen._
import com.wordnik.swagger.codegen.model.ClientOpts

import scala.collection.JavaConverters._

class ObjCClientConfig extends BasicObjcGenerator with ClientConfig {
  var config: ClientOpts = _
  var properties: Map[String, String] = Map()
  var outputDirectory: String = _
  var outputDir: String = _

  val fields: Set[InputOption] = Set()

  override def generate(config: ClientOpts): Seq[java.io.File] = {
    this.config = config
    this.properties = config.properties.asScala.toMap

    val requiredFields = (fields.filter(_.getRequired() == true).map(_.getName)).toSet
    if((properties.keys.toSet & requiredFields).size != requiredFields.size) {
      throw new BadRequestException(400, "missing required fields!")
    }

    this.outputDirectory = config.outputDirectory
    this.outputDir = this.outputDirectory + "/client"
    additionalParams ++= properties
    super.generate(config)
  }

  def usage() = fields
  override def invokerPackage = Option("")
  override def destinationDir = this.outputDir
  override def modelPackage = None
  override def apiPackage = None

  override def supportingFiles =
    List(
      ("SWGObject.h", outputDir, "SWGObject.h"),
      ("SWGObject.m", outputDir, "SWGObject.m"),
      ("SWGApiClient.h", outputDir, "SWGApiClient.h"),
      ("SWGApiClient.m", outputDir, "SWGApiClient.m"),
      ("SWGFile.h", outputDir, "SWGFile.h"),
      ("SWGFile.m", outputDir, "SWGFile.m"),
      ("SWGDate.h", outputDir, "SWGDate.h"),
      ("SWGDate.m", outputDir, "SWGDate.m"),
      ("Podfile.mustache", outputDirectory, "Podfile")
    )
}