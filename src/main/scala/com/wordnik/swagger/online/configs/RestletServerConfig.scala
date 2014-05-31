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
import scala.collection.mutable.HashSet

class RestletServerConfig extends BasicJavaGenerator with ClientConfig {
  var config: ClientOpts = _
  var properties: Map[String, String] = Map()
  var outputDirectory: String = _
  var codeDir: String = _

  val fields = Set(
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
    this.codeDir = this.outputDirectory + "/src/main/java"
    additionalParams ++= properties
    super.generate(config)
  }
  apiTemplateFiles.clear
  apiTemplateFiles += ("resource.mustache" -> "Resource.java")
  apiTemplateFiles += ("serverResource.mustache" -> "ServerResource.java")

  override def templateDir = "restlet/templates"
  def usage() = fields
  def artifactId = properties.getOrElse("artifactId", fieldDefaultValue("artifactId"))
  def artifactVersion = properties.getOrElse("artifactVersion", fieldDefaultValue("artifactVersion"))
  def groupId = properties.getOrElse("groupId", fieldDefaultValue("groupId"))

  override def destinationDir = codeDir
  override def modelPackage = Option(properties.getOrElse("modelPackage", fieldDefaultValue("modelPackage")))
  override def apiPackage = Option(properties.getOrElse("apiPackage", fieldDefaultValue("apiPackage")))

  // supporting classes
  override def supportingFiles = List(
    ("pom.mustache", outputDirectory, "pom.xml"),
    ("restletApplication.mustache", codeDir, "RestletApplication.java")
  )

  val pathCombos = new HashSet[String]

  override def processApiMap(m: Map[String, AnyRef]): Map[String, AnyRef] = {
    print(".")
    val mutable = scala.collection.mutable.Map() ++ m

    if(!mutable.contains("httpMethod")) {
      import org.json4s.jackson.JsonMethods._
      import org.json4s.jackson.Serialization.write
      import com.wordnik.swagger.codegen.model.SwaggerSerializers
      implicit val formats = SwaggerSerializers.formats("1.2")
//      println(pretty(render(parse(write(mutable)))))
      println(mutable)
    }
    val method = mutable("httpMethod").toString
    val path = mutable("path").toString

    pathCombos.contains(path) match {
      case false => {
        mutable += "uniquePath" -> "true"
        pathCombos += path
      }
      case _ =>
    }

    mutable.map(k => {
      k._1 match {
        // make initial caps
        case "returnType" => mutable += "returnType" -> (k._2.asInstanceOf[Option[String]])
        case "httpMethod" => mutable += "httpMethod" -> (k._2.toString.substring(0, 1) + k._2.toString.substring(1).toLowerCase)
        case _ =>
      }
    })
    mutable.toMap
  }

  import com.wordnik.swagger.codegen.model._
  import scala.collection.mutable.{HashMap, ListBuffer}

  def classNameFromPath(path: String) = {
    ((for(m <- path.replaceAll("\\{\\w+}", "").replaceAll("\\/\\/","/").split("\\/"))
      yield (
        if(m.length > 0)
          m.charAt(0).toUpper + m.substring(1)
        else ""
      )).mkString("") + (
        if(path.endsWith("}")) ""
        else "Root")
    )
  }

  override def groupOperationsToFiles(operations: List[(String, String, Operation)]): Map[(String, String), List[(String, Operation)]] = {
    val opMap = new HashMap[(String, String), ListBuffer[(String, Operation)]]
    for ((basePath, apiPath, operation) <- operations) {
      // val className = resourceNameFromFullPath(apiPath)
      val className = classNameFromPath(apiPath)
//      println(basePath, className)
      val listToAddTo = opMap.getOrElse((basePath, className), {
        val l = new ListBuffer[(String, Operation)]
        opMap += (basePath, className) -> l
        l
      })
      listToAddTo += Tuple2(apiPath, operation)
    }

    val o = opMap.map(m => (m._1, m._2.toList)).toMap
    o
  }
}
