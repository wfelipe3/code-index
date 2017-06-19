package com.codeindex.storage.json

import io.circe.parser._
import io.circe.syntax._

/**
  * Created by feliperojas on 6/19/17.
  */
object JsonIndex {

  type Index = Map[String, Map[String, Seq[String]]]

  def addIndex(index: Index)(words: Seq[String], value: String): Either[String, Index] = {
    if (!index.contains("keys"))
      Left("no keys attribute found")
    else
      Right(Map("keys" -> words.foldLeft(index.getOrElse("keys", Map.empty[String, Seq[String]])) { (i, w) =>
        val v = i.get(w).map(s => value +: s).getOrElse(Seq(value))
        i + (w -> v.sorted)
      }))
  }

  def decode(value: String): Either[String, Index] = {
    parse(value)
      .flatMap(j => j.as[Index].left.map(_.toString()))
      .left.map(_.toString)
  }

  def encode(index: Index): Either[String, String] = Right(index.asJson.toString())

}
