package com.wordnik.swagger.online.configs

import com.wordnik.swagger.sample.model._
import com.wordnik.swagger.sample.exception._

import com.wordnik.swagger.codegen._
import com.wordnik.swagger.codegen.model.ClientOpts

import scala.collection.JavaConverters._

class StaticDocsConfig extends SwaggerDocGenerator with ClientConfig {
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
  override def destinationDir = this.outputDir
  override def templateDir = "swagger-static"
}