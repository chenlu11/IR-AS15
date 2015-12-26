package ch.ethz.ir.evaluation

import javax.management.Query
import scala.io.Source
import ch.ethz.ir.retrieval.Retrieval

/**
 * Author : yeyaozhang
 * Function : IR System Evaluation Values (P, R, F1, MAP)
 * Input :List of tuples(whose first element is the id of a single query, the second element corresponds to the list containing top 100 doc names of this query)
 */

class Evaluation(result: Vector[(Int, Vector[String])]) {
  private final val keys = result.map(x => x._1)
  private final val relevantDic = Source.fromFile(Retrieval.evaluationPath).getLines //line example: "51 0 AP880301-0271 1"
    .map(_.split(" ")).filter(m => m(3).toInt == 1)
    .toList.groupBy(_(0)).filter(x => keys.contains(x._1.toInt))
    .map(v => (v._1.toInt -> v._2.map(arr => arr(2).filter((_ != ' ')))))

  val recall = result.map { res => res._2.filter { d => relevantDic(res._1).contains(d) }.length.toDouble / relevantDic(res._1).length }
  val precision = result.map { res => res._2.filter { d => relevantDic(res._1).contains(d) }.length.toDouble / res._2.length }
  val F1 = recall.zip(precision).map(v => (2*v._1*v._2) / (v._1+v._2))
  
  val AP = result.map { res =>
    {
      val true_list = relevantDic(res._1)
      var counter = 0.0
      var rel_counter = 0.0
      var ap = 0.0
      res._2.foreach { d =>
        counter += 1
        if (true_list.contains(d)) {
          rel_counter += 1
          ap += (rel_counter / counter)
        }
      }
      if (res._2.length < true_list.length)
        ap / res._2.length
      else
        ap / true_list.length
    }
  }
  
  def outputEvaluation {
    println("Recall: " + recall.mkString(", "))
    println("Average Recall: " + recall.sum / result.length)
    
    println("Precison: " + precision.mkString(", "))
    println("Average Precison: " + precision.sum / result.length)
    
    println("F1: " + F1.mkString(", "))
    val F = F1.filterNot(_.isNaN)
    println("Average F1: " + F.sum / F.length)
    
    println("AP: " + AP.mkString(", "))
    println("MAP: " + AP.sum / result.length)
  }
}

object Evaluation {
  def main(args: Array[String]) {
    val top100docs = Source.fromFile("testres.txt").getLines.toVector.map(_.split(" ")).filter(m => m(3).toInt == 1).groupBy(_(0)).map(v => (v._1.toInt, v._2.map(arr => arr(2)).take(100))).toVector
    val e = new Evaluation(top100docs)
    e.outputEvaluation
  }
}

