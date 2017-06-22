package com.codeindex.console.impure

import java.io.File

import cats.{Id, ~>}
import com.codeindex.console.pure.IndexConsoleUI.{CodeIndexConfig, Console, EmptyIndexConfig, ErrorIndexConfig, GetParameters, Index, IndexConfig, Println, Search, SearchConfig}
import com.codeindex.storage.json.JsonIndex
import com.codeindex.storage.{FileConnection, FileStorage, InMemoryConnection, InMemoryStorage}

import scala.util.{Failure, Success}

/**
  * Created by feliperojas on 6/19/17.
  */
object ConsoleInterpreters {
  def scoptInterpreter(): Console ~> Id = new (Console ~> Id) {
    override def apply[A](fa: Console[A]): Id[A] = fa match {
      case GetParameters(args) =>
        parseArgs(args)
      case Println(a) =>
        println(a).asInstanceOf[A]
      case Index(indexConfig) =>
        FileStorage.load(FileConnection(indexConfig.indexFile))
          .flatMap(s => JsonIndex.decode(s))
          .flatMap(ji => JsonIndex.addIndex(ji)(indexConfig.words, indexConfig.value))
          .flatMap(JsonIndex.encode)
          .flatMap(s => FileStorage.save(FileConnection(indexConfig.indexFile), s))
          .fold(s => Failure(new Exception(s)), Success(_))
          .asInstanceOf[A]

      case Search(searchConfig) =>
        FileStorage.load(FileConnection(searchConfig.indexFile))
          .flatMap(s => JsonIndex.decode(s))
          .map(ji => JsonIndex.search(ji)(searchConfig.word))
          .fold(s => Failure(new Exception(s)), Success(_))
          .asInstanceOf[A]

    }
  }

  private def parseArgs[A](args: Array[String]) = {
    val parser = new scopt.OptionParser[CodeIndexConfig]("scopt") {
      cmd("index")
        .action((_, c) => IndexConfig(indexFile = new File("."), words = Seq(), value = ""))
        .text("index a value with the given keys")
        .children(
          opt[Seq[String]]('w', "words")
            .valueName("<word1>,<word2>...")
            .required()
            .action((k, c) => c.asInstanceOf[IndexConfig].copy(words = k))
            .text("words to be index for this value"),
          opt[String]("value")
            .required()
            .abbr("v")
            .action((v, c) => c.asInstanceOf[IndexConfig].copy(value = v))
            .text("value to be indexed"),
          opt[File]("indexFile")
            .required()
            .abbr("i")
            .action((i, c) => c.asInstanceOf[IndexConfig].copy(indexFile = i))
            .text("file where indexes are stored")
        )
      cmd("search")
        .action((_, c) => SearchConfig(indexFile = new File("."), word = ""))
        .text("search for values given a term")
        .children(
          opt[String]("term")
            .required()
            .abbr("t")
            .action((term, c) => c.asInstanceOf[SearchConfig].copy(word = term))
            .text("Term to search"),
          opt[File]("indexFile")
            .required()
            .abbr("i")
            .action((i, c) => c.asInstanceOf[SearchConfig].copy(indexFile = i))
            .text("file where indexes are stored")
        )
    }
    parser.parse(args, EmptyIndexConfig).getOrElse(ErrorIndexConfig).asInstanceOf[A]
  }
}

