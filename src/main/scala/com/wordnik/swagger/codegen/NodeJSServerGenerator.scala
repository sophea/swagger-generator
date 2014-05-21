package com.wordnik.swagger.codegen

import scala.collection.mutable.{ HashMap, ListBuffer }

object NodeJSServerGenerator extends NodeJSServerGenerator {
  def main(args: Array[String]) = generateClient(args)
}

class NodeJSServerGenerator extends BasicScalaGenerator {
  override def templateDir = "nodejs/templates"

  // template used for apis
  apiTemplateFiles ++= Map("api.mustache" -> ".js")
  
  modelTemplateFiles.clear
}
