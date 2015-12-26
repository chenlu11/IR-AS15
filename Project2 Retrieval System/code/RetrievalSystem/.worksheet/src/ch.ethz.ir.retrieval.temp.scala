package ch.ethz.ir.retrieval
import scala.collection.mutable.PriorityQueue
import scala.io.Source

object temp {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(156); 
  println("Welcome to the Scala worksheet");$skip(111); 
  
	var x = new PriorityQueue[(Double, Int)]()(Ordering.by((_: (Double, Int))._1).reverse);System.out.println("""x  : scala.collection.mutable.PriorityQueue[(Double, Int)] = """ + $show(x ));$skip(21);   // ascending order
	x.enqueue(2.0 -> 1);$skip(21); 
	x.enqueue(1.0 -> 2);$skip(21); 
	x.enqueue(3.0 -> 3);$skip(21); 
	x.enqueue(1.0 -> 4);$skip(21); 
	x.enqueue(4.0 -> 0);$skip(46); val res$0 = 
  x.dequeueAll.reverseIterator.mkString(", ");System.out.println("""res0: String = """ + $show(res$0));$skip(22); 
  x.enqueue(5.0 -> 1);$skip(8); val res$1 = 
  x.min;System.out.println("""res1: (Double, Int) = """ + $show(res$1));$skip(11); val res$2 = 
  x.length;System.out.println("""res2: Int = """ + $show(res$2));$skip(12); val res$3 = 
  x.drop(3);System.out.println("""res3: scala.collection.mutable.PriorityQueue[(Double, Int)] = """ + $show(res$3));$skip(8); val res$4 = 
  x.min;System.out.println("""res4: (Double, Int) = """ + $show(res$4));$skip(32); 
  x = x.dropRight(x.length - 3);$skip(4); val res$5 = 
  x;System.out.println("""res5: scala.collection.mutable.PriorityQueue[(Double, Int)] = """ + $show(res$5));$skip(57); 
  val a = collection.mutable.Map("a"->1, "b"->2, "c"->3);System.out.println("""a  : scala.collection.mutable.Map[String,Int] = """ + $show(a ));$skip(24); 
  val b = List("a","b");System.out.println("""b  : List[String] = """ + $show(b ));$skip(18); val res$6 = 
  b.contains("a");System.out.println("""res6: Boolean = """ + $show(res$6));$skip(18); val res$7 = 
  b.contains("c");System.out.println("""res7: Boolean = """ + $show(res$7));$skip(35); val res$8 = 
  a.retain((k,v) => b.contains(k))
	
	class Query(val id : Int, val query : String){};System.out.println("""res8: ch.ethz.ir.retrieval.temp.a.type = """ + $show(res$8));$skip(105); 
	val querypath = "/Users/LU/Downloads/tipster/topics";System.out.println("""querypath  : String = """ + $show(querypath ));$skip(171); 
	val queries = for {line <- Source.fromFile(querypath).getLines().filter(_ contains "<title>")
		val query = line.substring(line.indexOf(": ") + 1).trim()
  } yield query;System.out.println("""queries  : Iterator[String] = """ + $show(queries ));$skip(94); 
  val query = ((51 to 90) zip queries.toList) map { case (id, query) => new Query(id, query)};System.out.println("""query  : scala.collection.immutable.IndexedSeq[ch.ethz.ir.retrieval.temp.Query] = """ + $show(query ));$skip(72); 
  
  val text = "\"Downstream\" Investments <doc>by OPEC Member States";System.out.println("""text  : String = """ + $show(text ));$skip(7); val res$9 = 
  text;System.out.println("""res9: String = """ + $show(res$9));$skip(42); val res$10 = 
  text.toLowerCase().split("\\W+").toList;System.out.println("""res10: List[String] = """ + $show(res$10));$skip(43); val res$11 = 
  
  text.split("\\<[^>]+>").mkString(" ");System.out.println("""res11: String = """ + $show(res$11));$skip(205); 

	val df = Source.fromFile("/Users/LU/Documents/Eclipse/RetrievalSystem/df").getLines()
							.map(_.split(" ")).toVector.groupBy(_(0))
							.map(v => (v._1.toInt -> v._2.map(arr => arr(1) -> arr(2))));System.out.println("""df  : scala.collection.immutable.Map[Int,scala.collection.immutable.Vector[(String, String)]] = """ + $show(df ));$skip(123); 
							
	val line = "81 AP880212-0001 0.44862585333181576 0.44811077724331555 4.9967180449932225E-36 8.64766079265891E-35";System.out.println("""line  : String = """ + $show(line ));$skip(34); 
	val queryid = line.split(" ")(0);System.out.println("""queryid  : String = """ + $show(queryid ));$skip(34); 
	val docname = line.split(" ")(1);System.out.println("""docname  : String = """ + $show(docname ));$skip(69); 
	val feature = line.split(" ").drop(2).toVector.map(t => t.toDouble);System.out.println("""feature  : scala.collection.immutable.Vector[Double] = """ + $show(feature ));$skip(767); 
	
	val AP = Vector(0.1647444396947229, 0.13328902385940286, 0.147562270119366, 0.06017413054142767, 0.25943848163499417, 0.6947766618617696, 0.09462220837640085, 0.317655643575889, 0.09995439020326265, 0.0, 0.18467072954412855, 0.014773328606527913, 0.017175141242937852, 0.02719939473715717, 0.0, 0.0, 1.639344262295082E-4, 0.02737971342533726, 0.04099617654802119, 0.22533960189590624, 0.19438290500796115, 3.846153846153846E-4, 3.225806451612903E-4, 0.0, 0.004928571428571429, 0.0366246656280304, 0.18693788983427256, 0.42266448593078154, 4.185643027542832E-4, 0.0, 0.07857706321472792, 0.21833666873642876, 0.030917975522600387, 0.010015801776531087, 0.5390986335179204, 0.03837092442789594, 0.0, 0.0036559139784946237, 0.005417983077041322, 0.08934831412752457);System.out.println("""AP  : scala.collection.immutable.Vector[Double] = """ + $show(AP ));$skip(36); 
	println(AP.dropRight(10).sum/40.0);$skip(834); 
	val F1 = Vector(0.6554621848739495, 0.1732283464566929, 0.10730253353204172, 0.16974169741697415, 0.18461538461538463, 0.12883435582822086, 0.1746880570409982, 0.3166023166023166, 0.026509572901325478, 0.0625, 0.4248366013071896, 0.03526448362720403, 0.11038961038961038, 0.07157894736842106, 0.06584362139917695, 0.09427609427609429, 0.003154574132492114, 0.013559322033898305, 0.17105263157894737, 0.2967741935483871, 0.12916666666666668, 0.0365296803652968, Double.NaN, 0.02003338898163606, 0.05591397849462365, 0.07106598984771574, 0.3445378151260504, 0.41984732824427484, 0.006024096385542169, 0.05907172995780591, 0.18518518518518517, 0.10872675250357654, 0.054644808743169404, 0.00808080808080808, 0.14285714285714288, 0.12140575079872203, 0.006944444444444445, 0.01509433962264151, 0.043795620437956206, 0.09289617486338798);System.out.println("""F1  : scala.collection.immutable.Vector[Double] = """ + $show(F1 ));$skip(31); 
	val F = F1.filterNot(_.isNaN);System.out.println("""F  : scala.collection.immutable.Vector[Double] = """ + $show(F ));$skip(18); val res$12 = 
	F.sum / F.length;System.out.println("""res12: Double = """ + $show(res$12))}
}
