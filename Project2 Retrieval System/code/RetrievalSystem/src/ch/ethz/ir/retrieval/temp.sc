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

	val df = Source.fromFile("/Users/LU/Documents/Eclipse/RetrievalSystem/df").getLines()
							.map(_.split(" ")).toVector.groupBy(_(0))
							.map(v => (v._1.toInt -> v._2.map(arr => arr(1) -> arr(2))))
                                                  //> df  : scala.collection.immutable.Map[Int,scala.collection.immutable.Vector[
                                                  //| (String, String)]] = Map(69 -> Vector((542738246,12198), (-934348459,2462),
                                                  //|  (3360,45801), (3522646,9858), (-865598561,8302), (3707,913194), (114801,97
                                                  //| 5713)), 88 -> Vector((106934601,92255), (94940391,10115), (-865586570,10094
                                                  //| ), (110034,46330)), 56 -> Vector((1638533572,5474), (62364819,6449), (10693
                                                  //| 4911,28049), (3493088,74048), (104087234,18171)), 52 -> Vector((-1077775052
                                                  //| ,9513), (1635854652,6238), (109627853,53092)), 57 -> Vector((107923,4516)),
                                                  //|  78 -> Vector((-322910901,479)), 84 -> Vector((1076356494,63921), (18843938
                                                  //| 31,1127), (-196794451,21614), (-1298713976,64063), (2037210682,14931), (106
                                                  //| 748523,37449)), 61 -> Vector((3506294,38622), (-1354778399,5985), (-1420605
                                                  //| 239,5607), (3240726,12551), (3365,894979), (2099229993,9803)), 89 -> Vector
                                                  //| ((-1077769574,50956), (-2050297182,2013), (3417663,2438), (3159,630832), (-
                                                  //| 892482046,115510), (-33
                                                  //| Output exceeds cutoff limit.
							
	val line = "81 AP880212-0001 0.44862585333181576 0.44811077724331555 4.9967180449932225E-36 8.64766079265891E-35"
                                                  //> line  : String = 81 AP880212-0001 0.44862585333181576 0.44811077724331555 4
                                                  //| .9967180449932225E-36 8.64766079265891E-35
	val queryid = line.split(" ")(0)          //> queryid  : String = 81
	val docname = line.split(" ")(1)          //> docname  : String = AP880212-0001
	val feature = line.split(" ").drop(2).toVector.map(t => t.toDouble)
                                                  //> feature  : scala.collection.immutable.Vector[Double] = Vector(0.44862585333
                                                  //| 181576, 0.44811077724331555, 4.9967180449932225E-36, 8.64766079265891E-35)
	
	val AP = Vector(0.1647444396947229, 0.13328902385940286, 0.147562270119366, 0.06017413054142767, 0.25943848163499417, 0.6947766618617696, 0.09462220837640085, 0.317655643575889, 0.09995439020326265, 0.0, 0.18467072954412855, 0.014773328606527913, 0.017175141242937852, 0.02719939473715717, 0.0, 0.0, 1.639344262295082E-4, 0.02737971342533726, 0.04099617654802119, 0.22533960189590624, 0.19438290500796115, 3.846153846153846E-4, 3.225806451612903E-4, 0.0, 0.004928571428571429, 0.0366246656280304, 0.18693788983427256, 0.42266448593078154, 4.185643027542832E-4, 0.0, 0.07857706321472792, 0.21833666873642876, 0.030917975522600387, 0.010015801776531087, 0.5390986335179204, 0.03837092442789594, 0.0, 0.0036559139784946237, 0.005417983077041322, 0.08934831412752457)
                                                  //> AP  : scala.collection.immutable.Vector[Double] = Vector(0.1647444396947229
                                                  //| , 0.13328902385940286, 0.147562270119366, 0.06017413054142767, 0.2594384816
                                                  //| 3499417, 0.6947766618617696, 0.09462220837640085, 0.317655643575889, 0.0999
                                                  //| 5439020326265, 0.0, 0.18467072954412855, 0.014773328606527913, 0.0171751412
                                                  //| 42937852, 0.02719939473715717, 0.0, 0.0, 1.639344262295082E-4, 0.0273797134
                                                  //| 2533726, 0.04099617654802119, 0.22533960189590624, 0.19438290500796115, 3.8
                                                  //| 46153846153846E-4, 3.225806451612903E-4, 0.0, 0.004928571428571429, 0.03662
                                                  //| 46656280304, 0.18693788983427256, 0.42266448593078154, 4.185643027542832E-4
                                                  //| , 0.0, 0.07857706321472792, 0.21833666873642876, 0.030917975522600387, 0.01
                                                  //| 0015801776531087, 0.5390986335179204, 0.03837092442789594, 0.0, 0.003655913
                                                  //| 9784946237, 0.005417983077041322, 0.08934831412752457)
	println(AP.dropRight(10).sum/40.0)        //> 0.08391448871139073
	val F1 = Vector(0.6554621848739495, 0.1732283464566929, 0.10730253353204172, 0.16974169741697415, 0.18461538461538463, 0.12883435582822086, 0.1746880570409982, 0.3166023166023166, 0.026509572901325478, 0.0625, 0.4248366013071896, 0.03526448362720403, 0.11038961038961038, 0.07157894736842106, 0.06584362139917695, 0.09427609427609429, 0.003154574132492114, 0.013559322033898305, 0.17105263157894737, 0.2967741935483871, 0.12916666666666668, 0.0365296803652968, Double.NaN, 0.02003338898163606, 0.05591397849462365, 0.07106598984771574, 0.3445378151260504, 0.41984732824427484, 0.006024096385542169, 0.05907172995780591, 0.18518518518518517, 0.10872675250357654, 0.054644808743169404, 0.00808080808080808, 0.14285714285714288, 0.12140575079872203, 0.006944444444444445, 0.01509433962264151, 0.043795620437956206, 0.09289617486338798)
                                                  //> F1  : scala.collection.immutable.Vector[Double] = Vector(0.6554621848739495
                                                  //| , 0.1732283464566929, 0.10730253353204172, 0.16974169741697415, 0.184615384
                                                  //| 61538463, 0.12883435582822086, 0.1746880570409982, 0.3166023166023166, 0.02
                                                  //| 6509572901325478, 0.0625, 0.4248366013071896, 0.03526448362720403, 0.110389
                                                  //| 61038961038, 0.07157894736842106, 0.06584362139917695, 0.09427609427609429,
                                                  //|  0.003154574132492114, 0.013559322033898305, 0.17105263157894737, 0.2967741
                                                  //| 935483871, 0.12916666666666668, 0.0365296803652968, NaN, 0.0200333889816360
                                                  //| 6, 0.05591397849462365, 0.07106598984771574, 0.3445378151260504, 0.41984732
                                                  //| 824427484, 0.006024096385542169, 0.05907172995780591, 0.18518518518518517, 
                                                  //| 0.10872675250357654, 0.054644808743169404, 0.00808080808080808, 0.142857142
                                                  //| 85714288, 0.12140575079872203, 0.006944444444444445, 0.01509433962264151, 0
                                                  //| .043795620437956206, 0.09289617486338798)
	val F = F1.filterNot(_.isNaN)             //> F  : scala.collection.immutable.Vector[Double] = Vector(0.6554621848739495,
                                                  //|  0.1732283464566929, 0.10730253353204172, 0.16974169741697415, 0.1846153846
                                                  //| 1538463, 0.12883435582822086, 0.1746880570409982, 0.3166023166023166, 0.026
                                                  //| 509572901325478, 0.0625, 0.4248366013071896, 0.03526448362720403, 0.1103896
                                                  //| 1038961038, 0.07157894736842106, 0.06584362139917695, 0.09427609427609429, 
                                                  //| 0.003154574132492114, 0.013559322033898305, 0.17105263157894737, 0.29677419
                                                  //| 35483871, 0.12916666666666668, 0.0365296803652968, 0.02003338898163606, 0.0
                                                  //| 5591397849462365, 0.07106598984771574, 0.3445378151260504, 0.41984732824427
                                                  //| 484, 0.006024096385542169, 0.05907172995780591, 0.18518518518518517, 0.1087
                                                  //| 2675250357654, 0.054644808743169404, 0.00808080808080808, 0.142857142857142
                                                  //| 88, 0.12140575079872203, 0.006944444444444445, 0.01509433962264151, 0.04379
                                                  //| 5620437956206, 0.09289617486338798)
	F.sum / F.length                          //> res12: Double = 0.13353939052656338
}