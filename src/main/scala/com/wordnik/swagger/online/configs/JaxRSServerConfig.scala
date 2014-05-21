package com.wordnik.swagger.online.configs

import com.wordnik.swagger.sample.model._
import com.wordnik.swagger.sample.exception._

import com.wordnik.swagger.codegen._
import com.wordnik.swagger.codegen.model.ClientOpts

import scala.collection.JavaConverters._

class JaxRSServerConfig extends JavaJaxRSServerGenerator with ClientConfig {
  var config: ClientOpts = _
  var properties: Map[String, String] = Map()
  var outputDirectory: String = _
  var javaDir: String = _

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

  override def supportingFiles = List(
    ("README.mustache", outputDirectory, "README.md"),
    ("ApiException.mustache", javaDir + "/" + apiPackage.get.replaceAll("\\.", "/"), "ApiException.java"),
    ("ApiOriginFilter.mustache", javaDir + "/" + apiPackage.get.replaceAll("\\.", "/"), "ApiOriginFilter.java"),
    ("ApiResponseMessage.mustache", javaDir + "/" + apiPackage.get.replaceAll("\\.", "/"), "ApiResponseMessage.java"),
    ("JacksonJsonProvider.mustache", javaDir + "/" + apiPackage.get.replaceAll("\\.", "/"), "JacksonJsonProvider.java"),
    ("NotFoundException.mustache", javaDir + "/" + apiPackage.get.replaceAll("\\.", "/"), "NotFoundException.java"),
    ("pom.mustache", outputDirectory, "pom.xml"),
    ("web.mustache", outputDirectory + "/src/main/webapp/WEB-INF", "web.xml")
  )
}