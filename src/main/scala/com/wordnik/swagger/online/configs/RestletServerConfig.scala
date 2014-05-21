package com.wordnik.swagger.online.configs

import com.wordnik.swagger.sample.model._
import com.wordnik.swagger.sample.exception._

import com.wordnik.swagger.codegen._
import com.wordnik.swagger.codegen.model.ClientOpts

import java.io.File

import scala.collection.JavaConverters._

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

  override def processApiMap(m: Map[String, AnyRef]): Map[String, AnyRef] = {
    val mutable = scala.collection.mutable.Map() ++ m
    mutable.map(k => {
      k._1 match {
        // make initial caps
        case "returnType" => mutable += "returnType" -> (k._2.asInstanceOf[Option[String]].getOrElse(null))
        case "httpMethod" => mutable += "httpMethod" -> (k._2.toString.substring(0, 1) + k._2.toString.substring(1).toLowerCase)
        case _ =>
      }
    })
    mutable.toMap
  }
}
