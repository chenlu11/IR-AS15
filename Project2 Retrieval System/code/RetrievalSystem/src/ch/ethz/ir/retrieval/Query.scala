package ch.ethz.ir.retrieval
import scala.collection.mutable.PriorityQueue
import ch.ethz.ir.preprocessing._

class Query(val id : Int, val query : String){
  type Term = Int
  val qterms = Tokenizer.tokenizerToHashCode(query).distinct
  // Term-based Model
  val df = collection.mutable.Map[Term, Int]()
  val idf = collection.mutable.Map[Term, Double]()
  val topdocsTermModel = new PriorityQueue[(Double, String)]()(Ordering.by((_: (Double, String))._1).reverse)  // (score, doc-name) in ascending order
  var topdocsListTermModel = Vector[String]() // doc-name ranking
  
  // Language-based Model
  val cf = collection.mutable.Map[Term,Int]() ++ qterms.map{case term => (term, 0)}
  val pw = collection.mutable.Map[Term, Double]()
  val topdocsLanguageModel = new PriorityQueue[(Double, String)]()(Ordering.by((_: (Double, String))._1).reverse)
  var topdocsListLanguageModel = Vector[String]() // doc-name ranking
  
  // Learning to Rank: Ordinal SVM
  val topdocsSVMModel = new PriorityQueue[(Double, String)]()(Ordering.by((_: (Double, String))._1).reverse)
  var topdocsListSVMModel = Vector[String]() // doc-name ranking
}