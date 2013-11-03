package rss

import scala.xml._
import java.net.URL

object RssReaderSample {
  def main(args : Array[String]) : Unit = { 
    val rssFeeds = combineFeeds(getRssFeeds(args(0)))
    printFeeds(rssFeeds)
    //writeFeeds(rssFeeds)
  }

  def getRssFeeds(fileName : String) : List[(String, Seq[String])] = {
    // Given a config file containing a list of rss feed URL's,
    // returns a list with ('channel name', ['article 1', 'article 2'])
    var baseList : List[(String, Seq[String])] = List()
    getFileLines(fileName).map(extractRss).foldLeft(baseList)
        { (l, r) => l ::: r.toList }
  }

  /**
   * 
   */
  def combineFeeds(feeds : List[(String, Seq[String])]) 
      : List[(String, Seq[String])] = {
     // Cleanup function, given a list of feeds it combines duplicate channels.
    def combinedFeed(feedName : String) : Seq[String] = {
      feeds.filter(_._1 == feedName).map(_._2).flatten
    }

    val feedNames = feeds.map(_._1).distinct

    feedNames.map(x => (x, combinedFeed(x)))
  }


  def getFileLines(fileName : String) : Array[String] = 
    // Extracts the URL's from a file and breaks them up into individual strings.
    scala.io.Source.fromFile(fileName).mkString.split("\n") 

  def extractRss(urlStr : String) : Seq[(String, Seq[String])] = {
    // Given a URL, returns a Seq of RSS data in the form:
    // ("channel", ["article 1", "article 2"])
    val url = new URL(urlStr)
    val conn = url.openConnection
    val xml = XML.load(conn.getInputStream)

    for (channel <- xml \\ "channel") 
      yield {
        val channelTitle = (channel \ "title").text
        val titles = extractRssTitles(channel)
        (channelTitle, titles)
    }
  }

  def extractRssTitles(channel : Node) : Seq[String] = {
    // Given a channel node from an RSS feed, returns all of the article names
    for (title <- (channel \\ "item") \\ "title") yield title.text
  }

  def printFeeds(feeds : List[(String, Seq[String])]) : List[Seq[Unit]] = { 
    // Given a list of ("channel", ["article 1", "article 2"]), prints
    //  them each to screen.
    for (feed <- feeds) yield {
          println("*** " + feed._1 + " ***")
          for (title <- feed._2) yield println("\t" + title)
    }
  }

  def writeFeeds(feeds : List[(String, Seq[String])]) = {
    // Given a list of ("channel", ["article 1", "article 2"]), generates
    //  and writes an HTML document listing the articles with channel names
    //  as headers.
    val html = 
      <html><title>Rss Feeds</title><body>
        {for (feed <- feeds) yield
          <h2>{feed._1}</h2>
          <ul>
            {for (title <- feed._2) yield <li>{title}</li>}
          </ul>}
        </body> </html>
    
    //XML.saveFull("rss.html", html, "UTF-8", true, null)
  }
}