package ch.ethz.ir.retrieval

import scala.collection.mutable.PriorityQueue
import scala.io.Source
import ch.ethz.ir.preprocessing._
import ch.ethz.ir.evaluation._
import java.io._

object Retrieval {
  final val tipsterFolder = "/Users/LU/Downloads/tipster/"
  final val path = tipsterFolder + "zips/zips"
  final val queryPath = tipsterFolder + "topics-final"
  final val evaluationPath = tipsterFolder + "qrels"
  final val RETRIEVE_SIZE = 100;  // retrieve top 100 documents for each query 
  val query : collection.parallel.immutable.ParVector[Query]  = importQuery(queryPath)
    
  def main(args : Array[String]) = {
		val startTimeMillis = System.currentTimeMillis()
		
		// ######################### First Scan ######################### 
		val skipFirstScan = true
		if (skipFirstScan) {
		  val idf = Source.fromFile(if (Tokenizer.usingStem) "idf-stem" else "idf").getLines()
							    .map(_.split(" ")).toVector.groupBy(_(0))
							    .map(v => (v._1.toInt -> v._2.map(arr => arr(1).toInt -> arr(2).toDouble)))
			val pw = Source.fromFile(if (Tokenizer.usingStem) "pw-stem" else "pw").getLines()
							    .map(_.split(" ")).toVector.groupBy(_(0))
							    .map(v => (v._1.toInt -> v._2.map(arr => arr(1).toInt -> arr(2).toDouble)))
			for (q <- query) {
  		  q.idf ++= idf.getOrElse(q.id, collection.mutable.Map[Int, Double]())
  		  q.pw ++= pw.getOrElse(q.id, collection.mutable.Map[Int, Double]())
  		}
		  TermBasedModel.l = if (Tokenizer.usingStem) 299.5061063844787 else 409.4136396018003
		}
		else {
  	  val iter1 = new TipsterCorpusIterator(path)
  		while(iter1.hasNext) {
  		  val doc = iter1.next
  		  println("First Scan --- processing the doc: " + doc.name)
  		  val tokens = doc.tokens
  		  val tfs = tokens.groupBy(identity).mapValues(l => l.length)
  		  // Term-based Model
  		  TermBasedModel.n = TermBasedModel.n + 1
  		  TermBasedModel.l = TermBasedModel.l + tfs.values.sum
  		  // Language-based Model
  		  LanguageBasedModel.n = LanguageBasedModel.n + tokens.length	  
  		  for (q <- query) {
  		    // Term-based Model
  		    q.df ++= tokens.distinct.filter{ term => q.qterms.contains(term) }.map(t => t -> (1 + q.df.getOrElse(t, 0)))
  		    // Language-based Model
  		    q.cf ++= q.cf.map{ case (term, cf) => (term, cf + tfs.getOrElse(term, 0)) }
  		  }		  
  	  }
  	  TermBasedModel.l = TermBasedModel.l / TermBasedModel.n
  		LanguageBasedModel.k = query.map{case q => q.qterms.length}.sum
  		for (q <- query) {
  		  q.idf ++= TermBasedModel.idf(q.df)
  		  q.pw ++= LanguageBasedModel.pw(q.cf)
  		}
			new PrintWriter("df") { write((for (q <- query; df <- q.df) yield (q.id + " " + df._1 + " " + df._2)).mkString("\n")); close }
  		new PrintWriter("cf") { write((for (q <- query; cf <- q.cf) yield (q.id + " " + cf._1 + " " + cf._2)).mkString("\n")); close }
  		new PrintWriter("idf") { write((for (q <- query; idf <- q.idf) yield (q.id + " " + idf._1 + " " + idf._2)).mkString("\n")); close }
  		new PrintWriter("pw") { write((for (q <- query; pw <- q.pw) yield (q.id + " " + pw._1 + " " + pw._2)).mkString("\n")); close }
  		new PrintWriter("count") {write(TermBasedModel.n + "\n" + TermBasedModel.l + "\n" + LanguageBasedModel.n + "\n" + LanguageBasedModel.k); close}
		}
		
		// ######################### Second Scan #########################
    val iter2 = new TipsterCorpusIterator(path)
		while(iter2.hasNext) {
		  val doc = iter2.next
		  println("Second Scan --- processing the doc: " + doc.name)
		  val tokens = doc.tokens
		  val tfs = tokens.groupBy(identity).mapValues(l => l.length)
		  for (q <- query) {
		    // Term-based Model
		    val score1 = TermBasedModel.score(tfs, q)
		    if (q.topdocsTermModel.length < RETRIEVE_SIZE)
		      q.topdocsTermModel.enqueue(score1 -> doc.name)
        else if (q.topdocsTermModel.min._1 < score1) {
          q.topdocsTermModel.enqueue(score1 -> doc.name)
          q.topdocsTermModel.dequeue()  // remove the (score, doc-name) pair with minimum score
        }
		    // Language-based Model
		    val score2 = LanguageBasedModel.score(tfs, q)
		    if (q.topdocsLanguageModel.length < RETRIEVE_SIZE)
		      q.topdocsLanguageModel.enqueue(score2 -> doc.name)
        else if (q.topdocsLanguageModel.min._1 < score2) {
          q.topdocsLanguageModel.enqueue(score2 -> doc.name)
          q.topdocsLanguageModel.dequeue()  // remove the (score, doc-name) pair with minimum score
        }
		  }
	  }
    
    // ######################### Output Results #########################		
    val writer1 = new PrintWriter("ranking-t-9.run")
    val writer2 = new PrintWriter("ranking-l-9.run")
    for (q <- query.toVector) {
      val topdocsTermModel = q.topdocsTermModel.dequeueAll.reverseIterator.toVector
      val topdocsLanguageModel = q.topdocsLanguageModel.dequeueAll.reverseIterator.toVector
      q.topdocsListTermModel = topdocsTermModel.map(rank => rank._2)
		  q.topdocsListLanguageModel = topdocsLanguageModel.map(rank => rank._2)
		  println("#Term-based Model for query \"" + q.query + "\": " 
		      + topdocsTermModel.mkString(", "))
      println("#Language-based Model for query \"" + q.query + "\": " 
	      + topdocsLanguageModel.mkString(", "))
      for (i <- 1 to RETRIEVE_SIZE) {
        writer1.write(q.id + " " + i + " " + q.topdocsListTermModel(i-1) + "\n")
        writer2.write(q.id + " " + i + " " + q.topdocsListLanguageModel(i-1) + "\n")
      }
		}
	  writer1.close
	  writer2.close

	  // ###################### Evaluate Performance #######################
	  println("\nPerformance Evaluation for term-based model")
	  val e1 = new Evaluation(query.take(40).map(q => (q.id, q.topdocsListTermModel)).toVector)
    e1.outputEvaluation
    println("\nPerformance Evaluation for language model")
	  val e2 = new Evaluation(query.take(40).map(q => (q.id, q.topdocsListLanguageModel)).toVector)
    e2.outputEvaluation
	  
    val endTimeMillis = System.currentTimeMillis();
	  System.out.println("\nTotal Running Time: " + ((endTimeMillis - startTimeMillis) / 1000) + "s");
	}
  
  def importQuery(querypath : String) : collection.parallel.immutable.ParVector[Query]  = {
    val queries = for {line <- Source.fromFile(querypath).getLines().filter(_ contains "<title>")
  		val query = line.substring(line.indexOf(": ") + 1).trim()
    } yield query                                   //> queries  : Iterator[String] = non-empty iterator
    (((51 to 100) zip queries.toList) map { case (id, query) => new Query(id, query)}).toVector.par
  }
}