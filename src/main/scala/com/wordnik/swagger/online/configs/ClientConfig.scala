package com.wordnik.swagger.online.configs

import com.wordnik.swagger.generator.model._
import com.wordnik.swagger.codegen.model.ClientOpts

trait ClientConfig {
  val fields: Set[InputOption]
  var properties: Map[String, String]

  def generate(config: ClientOpts): Seq[java.io.File]

  def fieldDefaultValue(key: String): String = {
    properties.getOrElse(key, 
      fields.filter(_.getName == key).headOption match {
        case Some(value) => value.getDefaultValue
        case _ => null
      }
    )
  }
}