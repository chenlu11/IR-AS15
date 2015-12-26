package ch.ethz.ir.retrieval

import scala.collection.mutable.PriorityQueue
import scala.io.Source
import ch.ethz.ir.preprocessing._
import ch.ethz.ir.evaluation._
import java.io._
import scala.math.sqrt
import scala.math.min

object LearningToRank {
  final val tipsterFolder = "/Users/LU/Downloads/tipster/"
  final val path = tipsterFolder + "zips/zips-1"
  final val queryPath = tipsterFolder + "topics-final"
  final val evaluationPath = tipsterFolder + "qrels"
  final val trainingDataPath = "train"
  final val RETRIEVE_SIZE = 100;  // retrieve top 100 documents for each query 
  val query : collection.parallel.immutable.ParVector[Query]  = importQuery(queryPath)
  
  def main(args : Array[String]) = {
    val startTimeMillis = System.currentTimeMillis()
    
//    constructTrainingData
//    train
    test
    
    val endTimeMillis = System.currentTimeMillis();
	  System.out.println("\nTotal Running Time: " + ((endTimeMillis - startTimeMillis) / 1000) + "s");
  }
  
  // ######################### Construct Training Data ######################### 
  def constructTrainingData = {	
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
		}

		val train = new PrintWriter(trainingDataPath)
    val iter2 = new TipsterCorpusIterator(path)
		while(iter2.hasNext) {
		  val doc = iter2.next
		  println("Second Scan --- processing the doc: " + doc.name)
		  val tokens = doc.tokens
		  val tfs = tokens.groupBy(identity).mapValues(l => l.length)
		  for (q <- query.take(40)) { // only query 51~90 has corresponding label to make training set
		    val score = Vector(TermBasedModel.score1(tfs, q), TermBasedModel.score2(tfs, q),
                            LanguageBasedModel.score1(tfs, q), LanguageBasedModel.score2(tfs, q))
		    train.write(q.id + " " + doc.name + " " + score.mkString(" ") + "\n")
		  }
	  }
    train.close()
	}
  
  // ######################### Train Point-wise SVM ######################### 
  // use AdaGrad Algorithm to solve this online SVM optimization problem
  def train() = {
    val numFeatures = 4 // Number of parameters
    val lambda = 0.00001
    var Eta = 1.0 // learning rate
    var timeStep = 0 // time step
    var S: Vector[Double] = Vector.fill(numFeatures)(1)  // Diag(G)
    var Grad: Vector[Double] = Vector.fill(numFeatures)(0)  // Gradient of f(W) at each step
    var W: Vector[Double] =  Vector.fill(numFeatures)(0)  // Parameter W
    
    val relevantDic = Source.fromFile(Retrieval.evaluationPath).getLines //line example: "51 0 AP880301-0271 1"
                      .map(_.split(" ")).filter(m => m(3).toInt == 1)
                      .toList.groupBy(_(0)).map(v => (v._1.toInt -> v._2.map(arr => arr(2).filter((_ != ' ')))))
    val train = Source.fromFile(trainingDataPath)
    train.getLines.foreach { line =>
      val queryid = line.split(" ")(0).toInt
    	val docname = line.split(" ")(1)
    	val feature = line.split(" ").drop(2).toVector.map(t => t.toDouble)
    	val label = if (relevantDic(queryid).contains(docname)) 1 else -1
      
      timeStep = timeStep + 1 // update time step
      // if this new data point is classified correctly, do not update W, else compute f(W) and update W
      val dotProduct: Double = feature.zip(W).map(t => t._1 * t._2).sum // dot product of x and W
      if(label * dotProduct < 1.0) { // update W
        val tmp1 = W.map(t => lambda * t) // Lambda * W
        val tmp2 = feature.map(t => label * t) // label * x
        Grad = tmp1.zip(tmp2).map(t=>t._1 - t._2)// Grad = Lambda * W - label * x
        S = S.zip(Grad).map(t => t._1 + t._2 * t._2) // S = S + np.square(Grad)
        Eta = 1.0 / (lambda * timeStep).toDouble
        if (label == -1) Eta = Eta / 3265.6
        
        val tmp3 = S.zip(Grad).map(t => sqrt(t._1) / t._2 )// np.sqrt(S) / Grad
        W = W.zip(tmp3).map(t => t._1 - Eta/t._2 )    // W = W - Eta / np.sqrt(S) * Grad 
       
        // W = W * min(1, 1.0 / ( np.linalg.norm(W) * np.sqrt(Lambda) ) )
        val norm_W = W.map(t => t * t).sum // np.linalg.norm(W)
        val tmp4: Double = min(1, 1.0 / ( sqrt(norm_W * lambda) ) ) // min(1, 1.0 / ( np.linalg.norm(W) * np.sqrt(Lambda) ) )
        W = W.map(t => t * tmp4)        
      }            
    }
    new PrintWriter("W") { write(W.mkString("\n")); close }
  }
  
  // ######################### Output Results & Evaluate Performance ######################### 
  def test() = {
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
		}
		
    val W = Source.fromFile("W").getLines().toVector.map(_.toDouble)
    val iter2 = new TipsterCorpusIterator(path)
		while(iter2.hasNext) {
		  val doc = iter2.next
		  println("Second Scan --- processing the doc: " + doc.name)
		  val tokens = doc.tokens
		  val tfs = tokens.groupBy(identity).mapValues(l => l.length)
		  for (q <- query) {
		    val feature = Vector(TermBasedModel.score1(tfs, q), TermBasedModel.score2(tfs, q),
                            LanguageBasedModel.score1(tfs, q), LanguageBasedModel.score2(tfs, q))
		    val score = (feature zip W).map{ case (x, w) => w * x}.sum
		    if (q.topdocsSVMModel.length < RETRIEVE_SIZE)
		      q.topdocsSVMModel.enqueue(score -> doc.name)
        else if (q.topdocsSVMModel.min._1 < score) {
          q.topdocsSVMModel.enqueue(score -> doc.name)
          q.topdocsSVMModel.dequeue()  // remove the (score, doc-name) pair with minimum score
        }
		  }
	  }
    
    // Output results
    val writer = new PrintWriter("ranking-svm-9.run")
    for (q <- query.toVector) {
      val topdocsSVMModel = q.topdocsSVMModel.dequeueAll.reverseIterator.toVector
      q.topdocsListSVMModel = topdocsSVMModel.map(rank => rank._2)
		  println("#Learning Model for query \"" + q.query + "\": " 
		      + topdocsSVMModel.mkString(", "))
      for (i <- 1 to RETRIEVE_SIZE)
        writer.write(q.id + " " + i + " " + q.topdocsListSVMModel(i-1) + "\n")
		}
	  writer.close
	  
	  // Evaluate performance
	  println("\nPerformance Evaluation for learning model")
	  val e = new Evaluation(query.take(40).map(q => (q.id, q.topdocsListSVMModel)).toVector)
    e.outputEvaluation
  }
  
  def importQuery(querypath : String) : collection.parallel.immutable.ParVector[Query]  = {
    val queries = for {line <- Source.fromFile(querypath).getLines().filter(_ contains "<title>")
  		val query = line.substring(line.indexOf(": ") + 1).trim()
    } yield query                                   //> queries  : Iterator[String] = non-empty iterator
    (((51 to 100) zip queries.toList) map { case (id, query) => new Query(id, query)}).toVector.par
  }
}