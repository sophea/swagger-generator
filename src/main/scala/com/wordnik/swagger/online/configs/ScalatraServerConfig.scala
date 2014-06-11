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

class ScalatraServerConfig extends ScalatraServerGenerator with ClientConfig {
  var config: ClientOpts = _
  var properties: Map[String, String] = Map()
  var outputDirectory: String = _
  var javaDir: String = _

  val fields = Set(
    new InputOption("appName", "The application name", "sample app", false),
    new InputOption("appDescription", "The application description", "A sample app generated from http://generator.wordnik.com", false),
    new InputOption("infoUrl", "A URL for more info", "http://swagger.wordnik.com", false),
    new InputOption("infoEmail", "The developer contact email address", "apiteam@wordnik.com", false),
    new InputOption("licenseInfo", "The license for the API", "Creative Commons 4.0.0", false),
    new InputOption("licenseUrl", "The license URL for the API", "http://swagger.wordnik.com", false),
    new InputOption("modelPackage", "The package for generated model files", "com.wordnik.model", false),
    new InputOption("apiPackage", "Package for generated API files", "com.wordnik.api", false),
    new InputOption("artifactId", "Identifier for the generated artifact", "swagger-jaxrs-server", false),
    new InputOption("artifactVersion", "Version for the generated artifact", "0.0.0", false),
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
    this.javaDir = this.outputDirectory + "/src/main/java"
    additionalParams ++= properties
    super.generate(config)
  }
  def usage() = fields
  def artifactId = properties.getOrElse("artifactId", fieldDefaultValue("artifactId"))
  def artifactVersion = properties.getOrElse("artifactVersion", fieldDefaultValue("artifactVersion"))
  def groupId = properties.getOrElse("groupId", fieldDefaultValue("groupId"))

  override def destinationDir = this.javaDir
  override def modelPackage = Option(properties.getOrElse("modelPackage", fieldDefaultValue("modelPackage")))
  override def apiPackage = Option(properties.getOrElse("apiPackage", fieldDefaultValue("apiPackage")))

  // supporting classes
  override def supportingFiles = List(
    ("README.mustache", this.outputDirectory, "README.md"),
    ("build.sbt", this.outputDirectory, "build.sbt"),
    ("web.xml", this.outputDirectory + "/src/main/webapp/WEB-INF", "web.xml"),
    ("JettyMain.scala", this.outputDirectory + "/src/main/scala", "JettyMain.scala"),
    ("Bootstrap.mustache", destinationDir, "ScalatraBootstrap.scala"),
    ("ServletApp.mustache", destinationDir, "ServletApp.scala"),
    ("project/build.properties", this.outputDirectory, "project/build.properties"),
    ("project/plugins.sbt", this.outputDirectory, "project/plugins.sbt"),
    ("sbt", this.outputDirectory, "sbt"))
}