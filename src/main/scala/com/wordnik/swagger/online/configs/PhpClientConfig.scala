package com.wordnik.swagger.online.configs

import com.wordnik.swagger.sample.model._
import com.wordnik.swagger.sample.exception._

import com.wordnik.swagger.codegen._
import com.wordnik.swagger.codegen.model.ClientOpts

import scala.collection.JavaConverters._

class PhpClientConfig extends BasicPHPGenerator with ClientConfig {
  var config: ClientOpts = _
  var properties: Map[String, String] = Map()
  var outputDir: String = _

  val fields: Set[InputOption] = Set()

  override def generate(config: ClientOpts): Seq[java.io.File] = {
    this.config = config
    this.properties = config.properties.asScala.toMap

    val requiredFields = (fields.filter(_.getRequired() == true).map(_.getName)).toSet
    if((properties.keys.toSet & requiredFields).size != requiredFields.size) {
      throw new BadRequestException(400, "missing required fields!")
    }

    this.outputDir = config.outputDirectory
    additionalParams ++= properties
    super.generate(config)
  }
  def usage() = fields

  override def invokerPackage = Option("")
  override def destinationDir = this.outputDir
  override def modelPackage = Option(properties.getOrElse("modelPackage", fieldDefaultValue("modelPackage")))
  override def apiPackage = Option("")

  override def supportingFiles =
    List(
      ("Swagger.mustache", outputDir + java.io.File.separator + apiPackage.get, "Swagger.php")
    )
}
