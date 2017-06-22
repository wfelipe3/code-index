package com.codeindex.storage

import java.io.{File, PrintWriter}
import java.nio.file.{Files, OpenOption, StandardOpenOption}

import scala.util.Try

/**
  * Created by williame on 6/21/17.
  */

sealed trait Connection

case object InMemoryConnection extends Connection

case class FileConnection(file: File) extends Connection

trait IndexStorage {
  def load(connection: Connection): Either[String, String]
  def save(connection: Connection, value: String): Either[String, Unit]
}

object FileStorage extends IndexStorage {
  override def load(connection: Connection): Either[String, String] = Try {
    scala.io.Source.fromFile(connection.asInstanceOf[FileConnection].file).mkString
  }.fold(t => Left(t.getMessage), Right(_))

  override def save(connection: Connection, value: String): Either[String, Unit] = Try {
    Files.write(connection.asInstanceOf[FileConnection].file.toPath, value.getBytes, StandardOpenOption.TRUNCATE_EXISTING)
  }.fold(t => Left(t.getMessage), Right(_))
}

object InMemoryStorage extends IndexStorage {
  var index: String =
    """{
      |  "keys" : {
      |    "hello" : [
      |      "hello"
      |    ],
      |    "world" : [
      |      "hello"
      |    ]
      |  }
      |}""".stripMargin


  override def load(connection: Connection): Either[String, String] = {
    println(index)
    Right(index)
  }
  override def save(connection: Connection, value: String): Either[String, Unit] = {
    index = value
    println(index)
    Right()
  }
}
