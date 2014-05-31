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

class NodeJSServerConfig extends NodeJSServerGenerator with ClientConfig {
  var config: ClientOpts = _
  var properties: Map[String, String] = Map()
  var outputDirectory: String = _

  val fields = Set(
    new InputOption("codeDir", "The target folder for generated code", "app", false),
    new InputOption("modelFolder", "The package for generated model files", "apis", false),
    new InputOption("apiFolder", "Package for generated API files", "apis", false),
    new InputOption("artifactId", "Identifier for the generated artifact", "swagger-server", false),
    new InputOption("artifactVersion", "Version for the generated artifact", "0.0.0", false),
    new InputOption("homepage", "Homepage for the app", "https://github.com/wordnik/swagger-codegen", false),
    new InputOption("groupId", "group ID for the generated artifact", "com.wordnik", false)
  )

  override def generate(config: ClientOpts): Seq[java.io.File] = {
    this.config = config
    this.properties = config.properties.asScala.toMap

    val requiredFields = (fields.filter(_.getRequired() == true).map(_.getName)).toSet
    if((properties.keys.toSet & requiredFields).size != requiredFields.size) {
      throw new BadRequestException(400, "missing required fields!")
    }

    this.outputDirectory = config.outputDirectory
    additionalParams ++= properties
    additionalParams ++= Map(
      "codeDir" -> codeDir,
      "modelFolder" -> modelPackage.get,
      "apiFolder" -> apiPackage.get,
      "homepage" -> homepage
    )
    super.generate(config)
  }
  def usage() = fields
  def homepage = properties.getOrElse("homepage", fieldDefaultValue("homepage"))
  def codeDir = properties.getOrElse("codeDir", fieldDefaultValue("codeDir"))
  def artifactId = properties.getOrElse("artifactId", fieldDefaultValue("artifactId"))
  def artifactVersion = properties.getOrElse("artifactVersion", fieldDefaultValue("artifactVersion"))
  def groupId = properties.getOrElse("groupId", fieldDefaultValue("groupId"))

  override def destinationDir = this.outputDirectory + java.io.File.separator + codeDir
  override def modelPackage = Option(properties.getOrElse("modelFolder", fieldDefaultValue("modelFolder")))
  override def apiPackage = Option(properties.getOrElse("apiFolder", fieldDefaultValue("apiFolder")))

  // supporting classes
  override def supportingFiles = List(
    ("package.mustache", outputDirectory, "package.json"),
    ("README.mustache", outputDirectory, "README.md"),
    ("main.mustache", destinationDir, "main.js"),
    ("models.mustache", destinationDir, "models.js"))
}