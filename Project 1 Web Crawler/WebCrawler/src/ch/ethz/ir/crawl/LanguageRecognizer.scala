package ch.ethz.ir.crawl
import java.io.File
import scala.collection.mutable
import scala.collection.mutable.Map
import scala.io.Source
import scala.math.log

object LanguageRecognizer {
  val modelEN: Map[String, Int] = readMap("dictEN.txt")
  val modelDE: Map[String, Int] = readMap("dictDE.txt")

  private def readMap(filepath: String): Map[String, Int] = {
    val map: Map[String, Int] = Map[String, Int]()
    for (line <- scala.io.Source.fromFile(new File(filepath)).getLines()) {
      val temp = line.split(",")
      map += (temp(0) -> Integer.parseInt(temp(1)))
    }
    map
  }

  def isMostInEn(text: String): Boolean = {
    // Score a sentence under a certain model. Exclude tokens not in the dictionary.
    def scoreSentence(sentence: String, model: Map[String, Int]): Double = {
      def tokenize(text: String) = text.split("\\W+")
      val total: Double = model.values.sum.toDouble
      tokenize(sentence).map(tok => log((model.getOrElse(tok, 0) + 1) / (total + 1))).sum
    }
    var scoreEN: Double = scoreSentence(text, modelEN)
    var scoreDE: Double = scoreSentence(text, modelDE)
    if (scoreEN > scoreDE) true else false
  }
}