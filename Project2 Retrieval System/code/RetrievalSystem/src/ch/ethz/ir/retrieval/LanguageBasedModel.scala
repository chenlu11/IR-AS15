package ch.ethz.ir.retrieval
import scala.collection.mutable.PriorityQueue
import scala.math.log

object LanguageBasedModel {
  type Term = Int
  
  final val lambda : Double = 0.3
  var n : Int = 0  // #words in collection
  var k : Int = 0  // #terms in all queries
  
  def score (tfs: Map[Term, Int], q: Query) : Double = {
    val tfs_n = tf(tfs)   
    val qtfs = tfs_n.filterKeys { term => q.qterms.contains(term) }
    val pws = q.pw
    val pwds = pwd(qtfs, pws)
//    val logpwds = pwds.values.map(pwd => log(pwd))
//    val score = logpwds.sum
    val score = pwds.values.product
    score
  }
	
	def tf(tf: Map[Term,Int]) : Map[Term, Double] = {
		val sum = tf.values.sum.toDouble
		tf.mapValues( v => v.toDouble / sum )
	}
	
	def pw(cf: collection.mutable.Map[Term,Int]) = 
		cf.mapValues( v => (v.toDouble + 1.0) / (n + k) )
		
	def pwd(tf: Map[Term,Double], pw: collection.Map[Term,Double]) =
	  pw.map{ case (term, pw) => (term, lambda * pw + (1.0 - lambda) * tf.getOrElse(term, 0.0))}
	  
	// Learning to Rank
	def pwd(tf: Map[Term,Double], pw: collection.Map[Term,Double], lambda: Double) =
	  pw.map{ case (term, pw) => (term, lambda * pw + (1.0 - lambda) * tf.getOrElse(term, 0.0))}
	
	def score1 (tfs: Map[Term, Int], q: Query) : Double = {
    val tfs_n = tf(tfs)   
    val qtfs = tfs_n.filterKeys { term => q.qterms.contains(term) }
    val pws = q.pw
    val pwds = pwd(qtfs, pws, 0.3)
    val score = pwds.values.product
    score
  }
	def score2 (tfs: Map[Term, Int], q: Query) : Double = {
    val tfs_n = tf(tfs)   
    val qtfs = tfs_n.filterKeys { term => q.qterms.contains(term) }
    val pws = q.pw
    val pwds = pwd(qtfs, pws, 0.5)
    val score = pwds.values.product
    score
  }
}