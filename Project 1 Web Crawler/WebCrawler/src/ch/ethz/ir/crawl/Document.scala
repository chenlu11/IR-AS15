package ch.ethz.ir.crawl

class Document(val url: String, val contentMain: String, content: String) {
  val simHash: Int = computeSimHash
  
  def computeSimHash: Int = {
    val tokens = content.split("[ .,;:?!\t\n\r\f]+").toList
  	val shingles = tokens.sliding(3).toSet    	
  	val hashes = shingles.map(_.hashCode)
//  	def binary(value: Int): String =
//  	  String.format("%16s", Integer.toBinaryString(value)).replace(' ','0')
//  	hashes.map(h => binary(h))
  	var simhash: Int = 0
  	for (i <- 0 until 32) {
  	  val sum_of_bit_i: Int = (hashes.toList map (bit => 2 * ((bit >> i) & 1) - 1)).sum
  	  simhash = if (sum_of_bit_i > 0) simhash | (1 << i)
  	            else simhash & ~(1 << i)
  	}
  	simhash
  }
}