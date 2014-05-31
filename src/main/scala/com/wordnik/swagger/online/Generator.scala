package com.wordnik.swagger.online

import com.wordnik.swagger.generator.model._
import com.wordnik.swagger.online.configs._
import com.wordnik.swagger.codegen.model._
import com.wordnik.swagger.codegen._
import com.wordnik.swagger.generator.util.ZipUtil;

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.{read, write}

import org.apache.commons.io.FileUtils

import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.node._

import java.io.{ File, PrintWriter }

import scala.collection.JavaConverters._

object Generator {
  implicit val formats = SwaggerSerializers.formats("1.2")

  val mapper = new ObjectMapper()
  val factory = mapper.getJsonFactory()

  def clientOptions(language: String) = {
    val clientConfig = language.toLowerCase match {
      case "java" => new JavaClientConfig()
      case "objc" => new ObjCClientConfig()
      case "android" => new AndroidClientConfig()
      case "php" => new PhpClientConfig()
      case "docs" => new StaticDocsConfig()
      case _ => null
    }
    Option(clientConfig) match {
      case Some(c) => c.fields.toList.sortWith(_.getName < _.getName).asJava
      case _ => List().asJava
    }
  }

  def serverOptions(language: String) = {
    val clientConfig = language.toLowerCase match {
      case "jaxrs" => new JaxRSServerConfig()
      case "nodejs" => new NodeJSServerConfig()
      case "restlet" => new RestletServerConfig()
      case _ => null
    }
    Option(clientConfig) match {
      case Some(c) => c.fields.toList.sortWith(_.getName < _.getName).asJava
      case _ => List().asJava
    }
  }

  def generateClient(language: String, input: ClientOptInput) = {
    val opts = input.getOpts()

    // temp dir to write to
    val outputFolder = File.createTempFile("codegen-", "-tmp")
    outputFolder.delete()
    outputFolder.mkdir()
    outputFolder.deleteOnExit()

    new File(outputFolder.getAbsolutePath + File.separator + "files").mkdir()
    val specFolder = outputFolder + File.separator + "specs"
    new File(specFolder).mkdir()

    writeModel(specFolder, input.getModel())

    opts.setOutputDirectory(outputFolder.getAbsolutePath + File.separator + "files")
    opts.properties = (opts.properties.asScala ++ Map("fileMap" -> (specFolder + File.separator + "/api-docs"))).asJava

    val clientConfig = language.toLowerCase match {
      case "java" => new JavaClientConfig()
      case "objc" => new ObjCClientConfig()
      case "android" => new AndroidClientConfig()
      case "php" => new PhpClientConfig()
      case "docs" => new StaticDocsConfig()
      case _ => null
    }

    Option(clientConfig) match {
      case Some(c) => {
        com.wordnik.swagger.codegen.Codegen.templates.clear
        val files = c.generate(opts)
        val zip = new ZipUtil()
        zip.compressFiles(List(
          new File(outputFolder.getAbsolutePath + File.separator + "files"),
          new File(specFolder)).asJava, outputFolder.getAbsolutePath + File.separator + "bundle.zip")
        outputFolder.getAbsolutePath + File.separator + "bundle.zip"        
      }
      case _ => null
    }
  }

  def generateServer(language: String, input: ClientOptInput) = {
    val opts = input.getOpts()

    // temp dir to write to
    val outputFolder = File.createTempFile("codegen-", "-tmp")
    outputFolder.delete()
    outputFolder.mkdir()
    outputFolder.deleteOnExit()

    new File(outputFolder.getAbsolutePath + File.separator + "files").mkdir()
    val specFolder = outputFolder + File.separator + "specs"
    new File(specFolder).mkdir()

    writeModel(specFolder, input.getModel())

    opts.setOutputDirectory(outputFolder.getAbsolutePath + File.separator + "files")
    opts.properties = (opts.properties.asScala ++ Map("fileMap" -> (specFolder + File.separator + "/api-docs"))).asJava

    val clientConfig = language.toLowerCase match {
      case "jaxrs" => new JaxRSServerConfig()
      case "nodejs" => new NodeJSServerConfig()
      case "restlet" => new RestletServerConfig()
      case _ => null
    }

    Option(clientConfig) match {
      case Some(c) => {
        com.wordnik.swagger.codegen.Codegen.templates.clear
        val files = c.generate(opts)
        val zip = new ZipUtil()
        zip.compressFiles(List(
          new File(outputFolder.getAbsolutePath + File.separator + "files"),
          new File(specFolder)).asJava, outputFolder.getAbsolutePath + File.separator + "bundle.zip")
        outputFolder.getAbsolutePath + File.separator + "bundle.zip"        
      }
      case _ => null
    }
  }

  def writeModel(outputFolder: String, model: JsonNode) = {
    val declarationList = model.get("apiDeclarations")

    for(i <- 0 until declarationList.size) yield {
      val json = parse(declarationList.get(i).toString)
      val listing = json.extract[ApiListing]
      val resourcePath = {
        if(listing.resourcePath.startsWith("/")) listing.resourcePath.substring(1)
        else listing.resourcePath
      }
      val filename = outputFolder + File.separator + resourcePath.replace("/", "_")
      val writer = new PrintWriter(new File(filename))
      writer.write(write(listing))
      writer.close()
    }
    model.asInstanceOf[ObjectNode].remove("apiDeclarations")

    val filename = outputFolder + File.separator + "api-docs"
    val writer = new PrintWriter(new File(filename))
    writer.write(model.toString)
    writer.close()
  }
}