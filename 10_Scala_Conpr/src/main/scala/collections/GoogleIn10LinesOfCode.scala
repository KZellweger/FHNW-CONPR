package collections

object GoogleIn10LinesOfCode {
  val htmlWord = Set(
     "class", "div", "http", "span", "href", "www", "html", "img",
     "title", "src", "alt", "width", "height", "strong", "clearfix",
     "target", "style", "script", "type", "_blank", "item", "last",
     "text", "javascript", "middot", "point", "display", "none", "data", 
     "amp", "https", "name", "incoming", "imwidth", "ratio", "imdensity",
     "path", "jpg", "1_1", "media", "max", "min", "_self", "kind", "link")

  // Früher reichte die obige Menge um uninteressante Wörter zu entfernen.
  // Heute braucht es etwas mehr:

  def keepWord(w: String): Boolean = 
    w.size > 2 && w.charAt(0).isUpper && w.charAt(1).isLower && !w.toList.exists(_.isDigit)
  
  
  def main(args: Array[String]): Unit = {
    println("Hey Google")
  
    import scala.io._
    val content = Source.fromURL("https://www.blick.ch")(Codec.UTF8).getLines().mkString
    println(content)
  
    val words = content.split("\\W").toList
    println(words)
//    
    val newords = words.filter(w => keepWord(w))
    println(newords)
//    
    val grouped = newords.groupBy(x => x)
    println(grouped)
//  
    val counts = grouped.view.mapValues(l => l.size)
    println(counts)
//  
    val top10 = counts.toList.sortBy(t => t._2).reverse.take(10)
    println(top10)
  }
}