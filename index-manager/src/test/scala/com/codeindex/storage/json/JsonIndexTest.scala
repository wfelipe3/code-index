package com.codeindex.storage.json

import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by feliperojas on 6/19/17.
  */
class JsonIndexTest extends FreeSpec with Matchers {

  type Index = Map[String, Map[String, Set[String]]]

  "test parsing bad json" in {
    JsonIndex.decode("""yolo""") should be(Left("io.circe.ParsingFailure: expected json value got y (line 1, column 1)"))
  }

  "test parsing well formed json with error structure" in {
    JsonIndex.decode("""{"test": ["value1", "value2"]}""") should be(Left("DecodingFailure([K, V]Map[K, V], List(DownField(test)))"))
  }

  "test parsing well formed json with correct structure" in {
    JsonIndex.decode("""{"keys":{"k1":["test"], "k2":["test"]}}""") should be(Right(Map("keys" -> Map("k1" -> Set("test"), "k2" -> Set("test")))))
  }

  "test add index to structure without keys attribute it should return Left" in {
    JsonIndex.addIndex(Map[String, Map[String, Set[String]]]())(Seq(), "") should be(Left("no keys attribute found"))
  }

  "test add index to correct structure with no words it should return same indexes" in {
    JsonIndex.addIndex(Map("keys" -> Map[String, Set[String]]()))(Seq(), "value") should be(Right(Map("keys" -> Map[String, Set[String]]())))
  }

  "test add index with words not indexed yet it should add index words with given value" in {
    JsonIndex.addIndex(Map("keys" -> Map[String, Set[String]]()))(Seq("k1", "k2"), "test") should be(Right(Map("keys" -> Map("k1" -> Set("test"), "k2" -> Set("test")))))
  }

  "test add index with words that where already indexed it should return index with words with new values" in {
    JsonIndex.addIndex(Map("keys" -> Map("k1" -> Set("value"))))(Seq("k1"), "test") should be(Right(Map("keys" -> Map("k1" -> Set("test", "value")))))
    JsonIndex.addIndex(Map("keys" -> Map("k1" -> Set("a value"))))(Seq("k1"), "test") should be(Right(Map("keys" -> Map("k1" -> Set("a value", "test")))))
    JsonIndex.addIndex(Map("keys" -> Map("k1" -> Set("a value"))))(Seq("k1", "k2"), "test") should be(Right(Map("keys" -> Map("k2" -> Set("test"), "k1" -> Set("a value", "test")))))
  }

  "test index duplicate value should leave one value" in {
    JsonIndex.addIndex(Map("keys" -> Map("k1" -> Set("value"))))(Seq("k1", "k2"), "value") should be(Right(Map("keys" -> Map("k2" -> Set("value"), "k1" -> Set("value")))))
  }

  "test encode bad json" in {
    JsonIndex.decode(JsonIndex.encode(Map("keys" -> Map("k1" -> Set("value")))).right.get) should be(Right(Map("keys" -> Map("k1" -> Set("value")))))
  }

  "test search index when there is not an index it should return none " in {
    JsonIndex.search(Map[String, Map[String, Set[String]]]())("test") should be(Set())
  }

  "test search index when the is a value it shoud return the value" in {
    JsonIndex.search(Map("keys" -> Map("k1" -> Set("value"))))("k1") should be(Set("value"))
  }


}
