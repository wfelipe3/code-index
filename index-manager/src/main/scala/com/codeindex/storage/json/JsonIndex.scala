package com.codeindex.storage.json

import io.circe.parser._
import io.circe.syntax._

/**
  * Created by feliperojas on 6/19/17.
  */
object JsonIndex {

  type Index = Map[String, Map[String, Set[String]]]

  def addIndex(index: Index)(words: Seq[String], value: String): Either[String, Index] = {
    if (!index.contains("keys"))
      Left("no keys attribute found")
    else
      Right(Map("keys" -> words.foldLeft(index.getOrElse("keys", Map.empty[String, Set[String]])) { (i, w) =>
        val v: Set[String] = i.get(w).map(s => s + value).getOrElse(Set(value))
        i + (w -> v)
      }))
  }

  def search(index: Index)(term: String): Set[String] =
    index.getOrElse("keys", Map.empty[String, Set[String]]).getOrElse(term, Set.empty[String])


  def decode(value: String): Either[String, Index] = {
    parse(value)
      .flatMap(j => j.as[Index].left.map(_.toString()))
      .left.map(_.toString)
  }

  def encode(index: Index): Either[String, String] = Right(index.asJson.toString())

}
