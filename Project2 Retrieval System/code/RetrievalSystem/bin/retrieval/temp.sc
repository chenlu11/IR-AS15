package ch.ethz.ir.retrieval
import scala.collection.mutable.PriorityQueue
import scala.io.Source

object temp {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
	var x = new PriorityQueue[(Double, Int)]()(Ordering.by((_: (Double, Int))._1).reverse)  // ascending order
                                                  //> x  : scala.collection.mutable.PriorityQueue[(Double, Int)] = PriorityQueue()
                                                  //| 
	x.enqueue(2.0 -> 1)
	x.enqueue(1.0 -> 2)
	x.enqueue(3.0 -> 3)
	x.enqueue(1.0 -> 4)
	x.enqueue(4.0 -> 0)
  x.dequeueAll.reverseIterator.mkString(", ")     //> res0: String = (4.0,0), (3.0,3), (2.0,1), (1.0,4), (1.0,2)
  x.enqueue(5.0 -> 1)
  x.min                                           //> res1: (Double, Int) = (5.0,1)
  x.length                                        //> res2: Int = 1
  x.drop(3)                                       //> res3: scala.collection.mutable.PriorityQueue[(Double, Int)] = PriorityQueue(
                                                  //| )
  x.min                                           //> res4: (Double, Int) = (5.0,1)
  x = x.dropRight(x.length - 3)
  x                                               //> res5: scala.collection.mutable.PriorityQueue[(Double, Int)] = PriorityQueue(
                                                  //| (5.0,1))
  val a = collection.mutable.Map("a"->1, "b"->2, "c"->3)
                                                  //> a  : scala.collection.mutable.Map[String,Int] = Map(b -> 2, a -> 1, c -> 3)
  val b = List("a","b")                           //> b  : List[String] = List(a, b)
  b.contains("a")                                 //> res6: Boolean = true
  b.contains("c")                                 //> res7: Boolean = false
  a.retain((k,v) => b.contains(k))                //> res8: ch.ethz.ir.retrieval.temp.a.type = Map(b -> 2, a -> 1)
	
	class Query(val id : Int, val query : String){}
	val querypath = "/Users/LU/Downloads/tipster/topics"
                                                  //> querypath  : String = /Users/LU/Downloads/tipster/topics
	val queries = for {line <- Source.fromFile(querypath).getLines().filter(_ contains "<title>")
		val query = line.substring(line.indexOf(": ") + 1).trim()
  } yield query                                   //> queries  : Iterator[String] = non-empty iterator
  val query = ((51 to 90) zip queries.toList) map { case (id, query) => new Query(id, query)}
                                                  //> query  : scala.collection.immutable.IndexedSeq[ch.ethz.ir.retrieval.temp.Qu
                                                  //| ery] = Vector(ch.ethz.ir.retrieval.temp$$anonfun$main$1$Query$1@1e88b3c, ch
                                                  //| .ethz.ir.retrieval.temp$$anonfun$main$1$Query$1@42d80b78, ch.ethz.ir.retrie
                                                  //| val.temp$$anonfun$main$1$Query$1@3bfdc050, ch.ethz.ir.retrieval.temp$$anonf
                                                  //| un$main$1$Query$1@1bce4f0a, ch.ethz.ir.retrieval.temp$$anonfun$main$1$Query
                                                  //| $1@5e3a8624, ch.ethz.ir.retrieval.temp$$anonfun$main$1$Query$1@5c3bd550, ch
                                                  //| .ethz.ir.retrieval.temp$$anonfun$main$1$Query$1@91161c7, ch.ethz.ir.retriev
                                                  //| al.temp$$anonfun$main$1$Query$1@604ed9f0, ch.ethz.ir.retrieval.temp$$anonfu
                                                  //| n$main$1$Query$1@6a4f787b, ch.ethz.ir.retrieval.temp$$anonfun$main$1$Query$
                                                  //| 1@685cb137, ch.ethz.ir.retrieval.temp$$anonfun$main$1$Query$1@6a41eaa2, ch.
                                                  //| ethz.ir.retrieval.temp$$anonfun$main$1$Query$1@7cd62f43, ch.ethz.ir.retriev
                                                  //| al.temp$$anonfun$main$1$Query$1@6d4b1c02, ch.ethz.ir.retrieval.temp$$anonfu
                                                  //| n$main$1$Query$1@6093dd
                                                  //| Output exceeds cutoff limit.
  
  val text = "\"Downstream\" Investments <doc>by OPEC Member States"
                                                  //> text  : String = "Downstream" Investments <doc>by OPEC Member States
  text                                            //> res9: String = "Downstream" Investments <doc>by OPEC Member States
  text.toLowerCase().split("\\W+").toList         //> res10: List[String] = List("", downstream, investments, doc, by, opec, memb
                                                  //| er, states)
  
  text.split("\\<[^>]+>").mkString(" ")           //> res11: String = "Downstream" Investments  by OPEC Member States
}