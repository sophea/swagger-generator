package com.wordnik.swagger.online.configs

import com.wordnik.swagger.generator.model._
import com.wordnik.swagger.generator.exception._

import com.wordnik.swagger.codegen._
import com.wordnik.swagger.codegen.model.ClientOpts

import scala.collection.JavaConverters._

class JavaClientConfig extends BasicJavaGenerator with ClientConfig {
  var config: ClientOpts = _
  var properties: Map[String, String] = Map()
  var outputDirectory: String = _
  var javaDir: String = _

  val fields = Set(
    new InputOption("modelPackage", "The package for generated model files", "com.wordnik.model", false),
    new InputOption("apiPackage", "Package for generated API files", "com.wordnik.api", false),
    new InputOption("artifactId", "Identifier for the generated artifact", "swagger-java-client", false),
    new InputOption("artifactVersion", "Version for the generated artifact", "0.0.0", false),
    new InputOption("groupId", "group ID for the generated artifact", "com.wordnik", false),
    new InputOption("invokerPackage", "package for the common HTTP components", "com.wordnik.common", false)
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
  override def invokerPackage = Option(properties.getOrElse("invokerPackage", fieldDefaultValue("invokerPackage")))
  def artifactId = properties.getOrElse("artifactId", fieldDefaultValue("artifactId"))
  def artifactVersion = properties.getOrElse("artifactVersion", fieldDefaultValue("artifactVersion"))
  def groupId = properties.getOrElse("groupId", fieldDefaultValue("groupId"))

  override def destinationDir = this.javaDir
  override def modelPackage = Option(properties.getOrElse("modelPackage", fieldDefaultValue("modelPackage")))
  override def apiPackage = Option(properties.getOrElse("apiPackage", fieldDefaultValue("apiPackage")))

  override def supportingFiles =
    List(
      ("apiInvoker.mustache", javaDir + java.io.File.separator + invokerPackage.get.replace(".", java.io.File.separator) + java.io.File.separator, "ApiInvoker.java"),
      ("JsonUtil.mustache", javaDir + java.io.File.separator + invokerPackage.get.replace(".", java.io.File.separator) + java.io.File.separator, "JsonUtil.java"),
      ("apiException.mustache", javaDir + java.io.File.separator + invokerPackage.get.replace(".", java.io.File.separator) + java.io.File.separator, "ApiException.java"),
      ("pom.mustache", outputDirectory, "pom.xml"))
}