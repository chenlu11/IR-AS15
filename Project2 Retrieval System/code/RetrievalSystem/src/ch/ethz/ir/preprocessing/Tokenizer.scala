package ch.ethz.ir.preprocessing

object Tokenizer {
  final val usingStem : Boolean = true
  final val stopWords: Set[String] = "the,of,a,to,and,in,i,for,,on,that,it,andp,with,said,be,by,at,andm,ar,from,or,wa,an,1,thi,he,have,will,ha,2,not,but,which".split(',').toSet

  def tokenize(text: String) : Vector[String] = {
    //text.toLowerCase().split("[ .,;:?!\t\n\r\f]+").toList
    if (usingStem)
      text.toLowerCase().split("\\W+").par.filterNot(s => s == "" || stopWords.contains(s) || s.endsWith("_")).map(word => PorterStemmer.stem(word)).toVector
    else
      text.toLowerCase().split("\\W+").par.filterNot(_ == "").toVector
  }
  
  def tokenizerToHashCode(text: String) : Vector[Int] = tokenize(text).map(term => term.hashCode())
  
  def main(args: Array[String]) {
    val s = "<?xml version='1.0' encoding='UTF-8'?><DOC><DOCNO> AP880212-0165 </DOCNO><FILEID>AP-NR-02-12-88 2046EST</FILEID><FIRST>r f AM-JaguarRecall     02-12 0132</FIRST><SECOND>AM-Jaguar Recall,0137</SECOND><HEAD>Jaguar Orders Recall Of 16,000 Vehicles For Suspension Problem</HEAD><DATELINE>LEONIA, N.J. (AP) </DATELINE><TEXT>   Jaguar Cars Inc. announced Friday a recallof 16,000 1988 Jaguar XJ6 and Vanden Plas models in the UnitedStates because of possible problems in the front suspension system.   Jaguar said stress corrosion may cause certain lower-spring panfixings in the front suspension system to fail. Such failures couldcause the vehicle's front sspension to settle onto the bump stop onone side of the car, Jaguar said.   The company said the car would not lose steering or braking, andthat no accidents or injuries have been reported from the problem.   Owners will be told by mail when to make appointments withJaguar dealers to have new fixings installed at no cost, thecompany said.</TEXT></DOC>"
    println(Tokenizer.tokenize(s).mkString(", "))
  }
}