package ch.ethz.ir.crawl

object Statistic {
  var NUM_OF_DISTINCT_URLS: Int = 0  // successfully retrieved URL number
  var NUM_OF_EXACT_DUPLICATES: Int = 0
  var NUM_OF_NEAR_DUPLICATES: Int = 0
  var NUM_OF_EN_PAGES: Int = 0
  var FREQUENCY_OF_STUDENT: Int = 0  // the frequency of the term “student”
}