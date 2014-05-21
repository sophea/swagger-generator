package com.wordnik.swagger.online.configs

import com.wordnik.swagger.sample.model._
import com.wordnik.swagger.sample.exception._

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
  override def modelPackage = Option(properties.getOrElse("modelPackage", fieldDefaultValue("modelPackage")))
  override def apiPackage = Option(properties.getOrElse("apiPackage", fieldDefaultValue("apiPackage")))

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