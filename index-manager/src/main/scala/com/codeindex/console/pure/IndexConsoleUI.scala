package com.codeindex.console.pure

import java.io.File

import cats.free.Free

import scala.util.Try

/**
  * Created by feliperojas on 6/19/17.
  */
object IndexConsoleUI {

  sealed trait Console[A]

  case class GetParameters(args: Array[String]) extends Console[CodeIndexConfig]

  case class Println(text: String) extends Console[Unit]

  case class Index(config: IndexConfig) extends Console[Try[Unit]]

  case class Search(config: SearchConfig) extends Console[Try[Seq[String]]]

  object ConsoleFree {
    def getParameters(args: Array[String]): Free[Console, CodeIndexConfig] = Free.liftF(GetParameters(args))

    def println(text: String): Free[Console, Unit] = Free.liftF(Println(text))

    def index(config: IndexConfig): Free[Console, Try[Unit]] = Free.liftF(Index(config))

    def search(config: SearchConfig): Free[Console, Try[Seq[String]]] = Free.liftF(Search(config))
  }

  sealed trait CodeIndexConfig

  case class IndexConfig(indexFile: File, words: Seq[String], value: String) extends CodeIndexConfig

  case class SearchConfig(indexFile: File, word: String) extends CodeIndexConfig

  case object EmptyIndexConfig extends CodeIndexConfig

  case object ErrorIndexConfig extends CodeIndexConfig


}
