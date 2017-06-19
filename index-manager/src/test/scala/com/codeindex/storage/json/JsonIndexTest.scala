package com.codeindex.storage.json

import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by feliperojas on 6/19/17.
  */
class JsonIndexTest extends FreeSpec with Matchers {

  "test parsing bad json" in {
    JsonIndex.decode("""yolo""") should be(Left("io.circe.ParsingFailure: expected json value got y (line 1, column 1)"))
  }

  "test parsing well formed json with error structure" in {
    JsonIndex.decode("""{"test": ["value1", "value2"]}""") should be(Left("DecodingFailure([K, V]Map[K, V], List(DownField(test)))"))
  }

  "test parsing well formed json with correct structure" in {
    JsonIndex.decode("""{"keys":{"k1":["test"], "k2":["test"]}}""") should be(Right(Map("keys" -> Map("k1" -> Seq("test"), "k2" -> Seq("test")))))
  }

  "test add index to structure without keys attribute it should return Left" in {
    JsonIndex.addIndex(Map[String, Map[String, Seq[String]]]())(Seq(), "") should be(Left("no keys attribute found"))
  }

  "test add index to correct structure with no words it should return same indexes" in {
    JsonIndex.addIndex(Map("keys" -> Map[String, Seq[String]]()))(Seq(), "value") should be(Right(Map("keys" -> Map[String, Seq[String]]())))
  }

  "test add index with words not indexed yet it should add index words with given value" in {
    JsonIndex.addIndex(Map("keys" -> Map[String, Seq[String]]()))(Seq("k1", "k2"), "test") should be(Right(Map("keys" -> Map("k1" -> Seq("test"), "k2" -> Seq("test")))))
  }

  "test add index with words that where already indexed it should return index with words with new values" in {
    JsonIndex.addIndex(Map("keys" -> Map("k1" -> Seq("value"))))(Seq("k1"), "test") should be(Right(Map("keys" -> Map("k1" -> Seq("test", "value")))))
    JsonIndex.addIndex(Map("keys" -> Map("k1" -> Seq("a value"))))(Seq("k1"), "test") should be(Right(Map("keys" -> Map("k1" -> Seq("a value", "test")))))
    JsonIndex.addIndex(Map("keys" -> Map("k1" -> Seq("a value"))))(Seq("k1", "k2"), "test") should be(Right(Map("keys" -> Map("k2" -> Seq("test"), "k1" -> Seq("a value", "test")))))
  }

  "test encode bad json" in {
    JsonIndex.decode(JsonIndex.encode(Map("keys" -> Map("k1" -> Seq("value")))).right.get) should be(Right(Map("keys" -> Map("k1" -> Seq("value")))))
  }

}
