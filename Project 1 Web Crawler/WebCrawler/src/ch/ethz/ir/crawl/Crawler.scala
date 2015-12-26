package ch.ethz.ir.crawl

import scala.collection.mutable.Queue
import scala.collection.mutable.HashSet

class Crawler(startUrl: String) {
  val urlPool: HashSet[String] = HashSet()
  val queue: Queue[String] = Queue()
  var docPool: Vector[Document] = Vector()
  
  queue += startUrl
  urlPool += startUrl
  while (!queue.isEmpty) {
    val curUrl = queue.dequeue()
    urlPool += curUrl
    //println(curUrl)
    parseUrl(curUrl)
  }
  
  // Reference: http://alvinalexander.com/scala/how-to-write-scala-http-get-request-client-source-fromurl
  @throws(classOf[java.io.IOException])
  def get(url: String) = io.Source.fromURL(url).mkString
  
  @throws(classOf[java.io.IOException])
  @throws(classOf[java.net.SocketTimeoutException])
  def get(url: String,
        connectTimeout: Int = 5000,
        readTimeout: Int = 5000,
        requestMethod: String = "GET") = {  
    import java.net.{URL, HttpURLConnection}
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    val inputStream = connection.getInputStream
    val responseCode = connection.getResponseCode()
    var content = ""
    if (inputStream != null) {     
      content = io.Source.fromInputStream(inputStream).mkString
      inputStream.close
    }    
    connection.disconnect
    (responseCode, content)
  }  
  
  def parseUrl(url: String) {
    import java.net.{URL, HttpURLConnection}
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(20000)
    connection.setReadTimeout(20000)
    connection.setRequestMethod("GET")
    try {
      //val (responseCode, content) = get(url, 20000, 20000)          
      val inputStream = connection.getInputStream
      val responseCode = connection.getResponseCode()
      var content = ""
      if (inputStream != null) {     
        content = io.Source.fromInputStream(inputStream).mkString
        inputStream.close
      }    
      connection.disconnect
        
      responseCode match {
        case 200 => {
          Statistic.NUM_OF_DISTINCT_URLS += 1
          if (content == "" || url.contains("login"))
            return
          for(outUrl <- extractOutUrls(url, content) if !urlPool.contains(outUrl)) {
            queue += outUrl
            urlPool += outUrl
          }          
          
          val contentMain = extractContentMain(content)
          val doc = new Document(url, contentMain, content)
          if (isExactDuplicates(doc))         
            Statistic.NUM_OF_EXACT_DUPLICATES += 1            
          else {
            if (isNearDuplicates(doc))
              Statistic.NUM_OF_NEAR_DUPLICATES += 1
            if (judgeLanguage(doc) == "English")
              Statistic.NUM_OF_EN_PAGES += 1
            Statistic.FREQUENCY_OF_STUDENT += ("student".r findAllIn content).length
          }
          docPool = docPool :+  doc
          
        }
      }    
    } catch {
      case ioe: java.io.IOException => { 
        //println("java.io.IOException")   // handle this
        connection.disconnect
      }
      case ste: java.net.SocketTimeoutException => {
        //println("java.net.SocketTimeoutException")  // handle this
        connection.disconnect
      }
    }
  }
  
  def extractOutUrls(url: String, content: String): Iterator[String] = {
    import scala.util.matching.Regex
    //val HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>"
  	val HTML_A_HREF_TAG_PATTERN =
  		"\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))"
  	//val a_tag = (HTML_A_TAG_PATTERN.r findAllIn content).mkString("\n")
  	//val href_tag = (HTML_A_HREF_TAG_PATTERN.r findAllIn a_tag).mkString("\n")
  	val href_tag = (HTML_A_HREF_TAG_PATTERN.r findAllIn content).mkString("\n")
  	
  	val IN_DOMAIN_LINK_PATTERN = "\"[^\"]*\\.html\""
  	val in_domain_links = (IN_DOMAIN_LINK_PATTERN.r findAllIn href_tag)
  	val prefix = url take (url.lastIndexOf("/") + 1)
    // deal with "../"
    def removeDotsFromUrl(url: String): String = {
  		var url_withoutdots = url
      while (("/[^/]+/\\.\\./".r findFirstIn url_withoutdots) != None) {
        url_withoutdots = ("/[^/]+/\\.\\./".r findFirstIn url_withoutdots) match {
            case Some(dot_part) => url_withoutdots.replace(dot_part, "/")
            case None => url_withoutdots
          }
      }
      url_withoutdots
  	}
  	val in_domain_urls = in_domain_links
  	                        .filter(!_.startsWith("?"))
  	                        .filter(!_.startsWith("http"))
  	                        .map(prefix + _.replaceAll("\"", ""))
  	                        .map(removeDotsFromUrl(_))   
    in_domain_urls
  }
  
  def extractContentMain(html: String): String = {
    val start_label = "<!-- START main content -->"
    val end_label = "<!-- END main content -->"
  	val index1 = html.indexOf(start_label)
  	val index2 = html.indexOf(end_label, index1 + 1)
  	if (index1 >= 0 && index2 > 0) 
  	  html.substring(index1 + start_label.size, index2)
	  else
	    (html.replaceAll("\\<[^\\>]*\\>", " ")).split("\\W+") mkString " "	    
  }
  
  def isExactDuplicates(doc: Document): Boolean = {
    val identicalUrls = (docPool filter (_.simHash == doc.simHash)).
                        filter (_.contentMain == doc.contentMain)
//    if (identicalUrls.size > 0) {
//      println(doc.simHash)
//      println(((identicalUrls :+ doc) map (_.url)) mkString "\n")
//      println()
//    }
    if (identicalUrls.size > 0) true else false
  }
  
  def isNearDuplicates(doc: Document): Boolean = {
    val max_different_bits = 1
    def countDifferentBits(y: Int): Int = {
      var x = y  
      var different_bits = 0
      while (x != 0) {
        x = x & (x - 1)
        different_bits += 1
      }
      different_bits
    }
    val nearUrls = docPool filter (olddoc => countDifferentBits(olddoc.simHash ^ doc.simHash) <= max_different_bits)
    if (nearUrls.size > 0) true else false
  }
  
  def judgeLanguage(doc: Document): String = {
    if (LanguageRecognizer.isMostInEn(doc.contentMain)) "English"
    else "Germany"
  }
  
  def printResult {
    println("Distinct URLs found: " + Statistic.NUM_OF_DISTINCT_URLS)
    println("Exact duplicates found: " + Statistic.NUM_OF_EXACT_DUPLICATES)
    println("Unique English pages found: " + Statistic.NUM_OF_EN_PAGES)
    println("Near duplicats found: " + Statistic.NUM_OF_NEAR_DUPLICATES)
    println("Term frequency of \"student\": " + Statistic.FREQUENCY_OF_STUDENT)
  }
  
}

object testCrawler {
  def main(args: Array[String]) {
    require(args.length == 1)
    new Crawler(args(0)).printResult
  }
}