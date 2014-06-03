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

import java.io.File

import scala.collection.JavaConverters._

class GrapeSwaggerServerConfig extends GrapeSwaggerServerGenerator with ClientConfig {
  var config: ClientOpts = _
  var properties: Map[String, String] = Map()
  var outputDirectory: String = _

  val fields = Set(
    new InputOption("rootFolder", "Root path for the generated api", "api", false),
    new InputOption("appVersion", "Version for the generated server", "0.0.0", false),
    new InputOption("vendor", "the vendor used in the server definition", "wordnik", false)
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
      "rootFolder" -> apiPackage.get,
      "appVersion" -> appVersion.get,
      "vendor" -> vendor.get
    )
    super.generate(config)
  }
  def usage() = fields

  override def destinationDir = this.outputDirectory + java.io.File.separator
  override def modelPackage = Option("entities")
  override def apiPackage = Option(properties.getOrElse("rootFolder", fieldDefaultValue("rootFolder")))

  def appVersion = Option(properties.getOrElse("appVersion", fieldDefaultValue("appVersion")))
  def vendor = Option(properties.getOrElse("vendor", fieldDefaultValue("vendor")))

  // supporting classes
  override def supportingFiles = List(
    ("app.mustache", outputDirectory, "app.rb"),
    ("base.mustache", outputDirectory + File.separator + apiPackage.get, "base.rb"))

  override def toModelFilename(name: String) = {
    val to = name(0).toLower + name.substring(1)
    println("updating name from " + name + " to " + to)
    to
  }
  override def toApiFilename(name: String) = name(0).toLower + name.substring(1)
  override def processApiMap(m: Map[String, AnyRef]): Map[String, AnyRef] = {
    val mutable = scala.collection.mutable.Map() ++ m
    mutable.map(k => {
      k._1 match {
        case "httpMethod" => mutable += "httpMethod" -> k._2.toString.toLowerCase
        // convert path into ruby-ish syntax without basePart (i.e. /pet.{format}/{petId} => /:petId
        case "path" => {
          val path = {
            val arr = k._2.toString.split("/")
            if (arr.length >= 2) {
              mutable += "basePart" -> (arr.slice(2, arr.length).mkString("", "/", ""))
              "/" + arr.slice(2, arr.length).mkString("", "/", "")
            } else
              k._2.toString
          }
          // rip out the root path
          mutable += "path" -> path.replaceAll("\\{", ":").replaceAll("\\}", "")
        }
        case _ =>
      }
    })
    mutable.toMap
  }
}