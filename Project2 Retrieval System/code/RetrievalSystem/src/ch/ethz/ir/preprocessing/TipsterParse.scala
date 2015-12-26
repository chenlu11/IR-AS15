package ch.ethz.ir.preprocessing

import util.Try
import javax.xml.parsers._
import org.w3c.dom.{ Document => XMLDoc }
import java.io.InputStream
import org.w3c.dom.NodeList
import org.w3c.dom.{Document => XMLDoc}

class TipsterParse(filename: String, is: InputStream){
  val doc : XMLDoc = {   
    val dbFactory = DocumentBuilderFactory.newInstance
    val dBuilder  = dbFactory.newDocumentBuilder
    val d = dBuilder.parse(is)
    is.close()
    d
  }
  
  //def name : String = read(doc.getElementsByTagName("DOCNO")).filter(_.isLetterOrDigit)
  def name : String = read(doc.getElementsByTagName("DOCNO")).filter((_ != ' '))
  def content : String = {
    filename.charAt(0) match {
      case 'A' => read(doc.getElementsByTagName("HEAD")) + " " + read(doc.getElementsByTagName("TEXT"))
      case 'D' => read(doc.getElementsByTagName("TEXT"))
      // 'F' and 'P' contains many "<...>" in text
      case 'F' => read(doc.getElementsByTagName("TEXT")).split("\\<[^>]+>").mkString(" ")
      case 'P' => read(doc.getElementsByTagName("TTL")) + " " + read(doc.getElementsByTagName("TEXT")).split("\\<[^>]+>").mkString(" ") 
      case 'S' => read(doc.getElementsByTagName("DESCRIPT")) + " " + read(doc.getElementsByTagName("LEADPARA")) + " " +
                  read(doc.getElementsByTagName("HEADLINE")) + " " + read(doc.getElementsByTagName("MEMO")) + " " +
                  read(doc.getElementsByTagName("TEXT"))
      case 'W' => filename.charAt(3) match {
        case '8' => read(doc.getElementsByTagName("HL")) + " " + read(doc.getElementsByTagName("TEXT"))
        case  _  => read(doc.getElementsByTagName("HL")) + " " + read(doc.getElementsByTagName("TEXT")) + " " + 
                    read(doc.getElementsByTagName("LP"))
      }
      // <TEXT><DESCRIPT>...</DESCRIPT></TEXT> or <TEXT>...</TEXT>...<DESCRIPT>...</DESCRIPT>
      case 'Z' => {
        val text = read(doc.getElementsByTagName("TEXT"))
        if (text.contains("<DESCRIPT>"))
          text + " " + read(doc.getElementsByTagName("TITLE"))
        else 
          read(doc.getElementsByTagName("DESCRIPT")) + " " + read(doc.getElementsByTagName("TITLE")) + " " + text
      }
      case _   => read(doc.getElementsByTagName("TEXT"))
    }
  }
//  def tokens : List[String] = Tokenizer.tokenize(content)  
  def tokens = Tokenizer.tokenizerToHashCode(content) 
  
  // helper method that reads from specific nodelist 
  protected def read(nlist: NodeList) : String = {
   	if (nlist==null) ""
   	else if (nlist.getLength==0) ""
   	else {
 	    val length = nlist.getLength
 	    val text  = for (i <- 0 until length) yield nlist.item(i).getTextContent
	    text.mkString(System.getProperty("line.separator"))
 	  }
 	}
   
  // helper method to combine two content fields
  protected def append(a: Option[String], b: Option[String]) = (a, b) match {
    case (None, None)      => ""
    case (Some(a), None)   => a
    case (None, Some(b))   => b
    case (Some(a),Some(b)) => a + System.getProperty("line.separator") + b
  }

  // get attribute value from first node of given tag
  protected def getAttrFromFirstNode(attr: String, tag: String) : Option[String] = { 
    val lst = getAttrFromAllNodes(attr,tag)
    if (lst.length==0) None else Some(lst.head)
  }  

  // get attribute values from all nodes of given tag
  protected def getAttrFromAllNodes(attr: String, tag: String) : List[String] = { 
    val nodelist = doc.getElementsByTagName(tag)
    if (nodelist == null) List()
    else if (nodelist.getLength==0) List()
    else {
      val result = new collection.mutable.ListBuffer[String]
      for (i <- 0 until nodelist.getLength) {
        val attributes = nodelist.item(i).getAttributes;
        val text = Try(attributes.getNamedItem(attr).getTextContent);
        if (text.isSuccess) result += text.get 
      }
      result.toList
    }
  }  

}

