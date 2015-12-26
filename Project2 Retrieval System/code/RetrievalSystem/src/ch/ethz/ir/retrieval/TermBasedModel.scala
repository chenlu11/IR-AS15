package ch.ethz.ir.retrieval
import scala.math.log

object TermBasedModel {	
  type Term = Int
  
  var n = 0 // n: #documents
  
  def score (tfs: Map[Term, Int], q: Query) : Double = {
    val tfs_n = BM25tf(tfs)     
    val qtfs = tfs_n.filterKeys { term => q.qterms.contains(term) }
    val idfs = q.idf
    val tfidfs = tfidf(qtfs, idfs)
    val score = tfidfs.values.sum
    score
  }
  
  // Okapi BM25
  // k1 and b are free parameters, usually chosen, in absence of an advanced optimization, 
  // as k1 in [1.2,2.0] and b = 0.75
  final val k1 = 1.2
  final val b = 0.75
  var l = 0.0  // average document length in the text collection
  
	def log2(x : Double) : Double = log(x) / log(2)
	def log10(x : Double) : Double = log(x) / log(10)
	
	def tf(tf: Map[Term,Int]) : Map[Term, Double] = {
		val sum = tf.values.sum.toDouble
		tf.mapValues( v => v.toDouble / sum )
	}
	def logtf(tf: Map[Term,Int]) : Map[Term, Double] = {
		tf.mapValues( v => 1 + log10(v.toDouble) )
	}
	def BM25tf(tfs: Map[Term,Int]) : Map[Term, Double] = {
	  val Ld = tfs.values.sum
	  tfs.mapValues { tf => (k1 + 1.0) * tf / (k1 * (1 - b + b * Ld / l) + tf) }
	}
	
	def idf(df: collection.mutable.Map[Term,Int]) =
		df.mapValues(log10(n) - log10(_))
		
	def tfidf(tf: Map[Term,Double], idf: collection.Map[Term,Double]) =
		tf map {case(term,tf) => (term, tf * idf(term))}
	
	// Learning to Rank
	def score1 (tfs: Map[Term, Int], q: Query) : Double = {
    val tfs_n = logtf(tfs)     
    val qtfs = tfs_n.filterKeys { term => q.qterms.contains(term) }
    val idfs = q.idf
    val tfidfs = tfidf(qtfs, idfs)
    val score = tfidfs.values.sum
    score
  }
	def score2 (tfs: Map[Term, Int], q: Query) : Double = {
    val tfs_n = BM25tf(tfs)     
    val qtfs = tfs_n.filterKeys { term => q.qterms.contains(term) }
    val idfs = q.idf
    val tfidfs = tfidf(qtfs, idfs)
    val score = tfidfs.values.sum
    score
  }
}