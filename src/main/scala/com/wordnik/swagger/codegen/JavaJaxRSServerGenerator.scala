package com.wordnik.swagger.codegen

import scala.collection.mutable.{ HashMap, ListBuffer }

class JavaJaxRSServerGenerator extends BasicJavaGenerator {
  override def templateDir = "java-jaxrs/templates"

  val outputFolder = "samples/server-generator/java-jaxrs/output"

  // where to write generated code
  override def destinationDir = outputFolder + "/src/main/java"

  override def modelPackage: Option[String] = Some("com.wordnik.client.model")

  // template used for apis
  apiTemplateFiles ++= Map("api.mustache" -> ".java",
    "serviceClass.mustache" -> "Service.java")

  modelTemplateFiles ++= Map("model.mustache" -> ".java")

  override def apiPackage: Option[String] = Some("com.wordnik.api")


  override def processApiMap(m: Map[String, AnyRef]): Map[String, AnyRef] = {
    val mutable = scala.collection.mutable.Map() ++ m
    mutable += "modelPackage" -> modelPackage
    mutable.map(k => {
      k._1 match {
        case "allParams" => {
          val paramList = k._2.asInstanceOf[List[_]]
          paramList.foreach(param => {
            val map = param.asInstanceOf[scala.collection.mutable.HashMap[String, AnyRef]]
            if(map.contains("dataType")){
              val dataType = map("dataType")
              map += "dataType" -> dataType.toString.replaceAll("Array\\[","List[")
            }
            if(map.contains("required")) {
              if(map("required") == "false") map += "notRequired" -> "true"
            }
            if(map.contains("defaultValue")) {
              // unquote default value
              val defaultValue = {
                map("defaultValue") match {
                  case Some(d) => {
                    val str = d.toString
                    if(str.startsWith("\"") && str.endsWith("\""))
                      Some(str.substring(1, str.length-1))
                    else Some(d)
                  }
                  case None => None
                }
              }
              map += "defaultValue" -> defaultValue
            }
            if(map.contains("allowableValues")) {
              val allowableValues = map("allowableValues")
              val quote = map("swaggerDataType") match {
                case "string" => "\""
                case _ => ""
              }
              
              val pattern = "([A-Z]*)\\[(.*)\\]".r
              val str = allowableValues match {
                case Some(pattern(valueType, values)) => {
                  valueType match {
                    case "LIST" => {
                      val l = values.split(",").toList
                      Some(l.mkString(","))
                    }
                    case "RANGE" => {
                      val r = values.split(",")
                      Some("AllowableValues(Range(" + r(0) + "," + r(1) + ", 1))")
                    }
                  }
                }
                case _ => None
              }
              str match {
                case Some(s) => map += "allowableValues" -> s
                case _ =>
              }
            }
          })
        }
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
          mutable += "path" -> path
        }
        case "returnType" => {
          k._2 match {
            case Some(returnType) =>
            case None => 
          }
        }
        case _ =>
      }
    })
    mutable.toMap
  }
}
