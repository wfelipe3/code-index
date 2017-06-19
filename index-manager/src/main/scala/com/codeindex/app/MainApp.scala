package com.codeindex.app

import com.codeindex.console.impure.ConsoleInterpreters
import com.codeindex.console.pure.IndexConsoleUI._

/**
  * Created by feliperojas on 6/19/17.
  */
object MainApp extends App {

  val x = for {
    c <- ConsoleFree.getParameters(args)
    _ <- processConfig(c)
  } yield ()

  x.foldMap(ConsoleInterpreters.scoptInterpreter())

  private def processConfig(c: CodeIndexConfig) = {
    c match {
      case c@IndexConfig(_, _, _) =>
        ConsoleFree.index(c).flatMap(v => v
          .map(_ => ConsoleFree.println("index correctly"))
          .getOrElse(ConsoleFree.println("error")))

      case s@SearchConfig(_, _) =>
        ConsoleFree.search(s).flatMap(v => v
          .map(s => ConsoleFree.println(s.mkString("\n")))
          .getOrElse(ConsoleFree.println("Error")))

      case e@EmptyIndexConfig => ConsoleFree.println("no command selected")

      case e@ErrorIndexConfig => ConsoleFree.println("error in config")
    }
  }
}

