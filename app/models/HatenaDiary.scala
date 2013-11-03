package models

import play.api.libs.ws.WS
 
import scala.xml.{XML, Elem}
import scala.util.matching.Regex
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global
 
case class HatenaDiaryFeed(title: String, link: String, diaries: Seq[HatenaDiary])
case class HatenaDiary(date: String, title: String, link: String, description: String)
 
object HatenaDiaryUtil {
  val hatenaBaseUrl = "http://d.hatena.ne.jp/"
 
  def getFeeds(userId: String): Future[HatenaDiaryFeed] = {
	val rssUrl = hatenaBaseUrl + userId + "/rss"
	val future = WS.url(rssUrl).get()
    future.map { response =>
      parseFeed(XML.loadString(response.body))
    }
  }
 
  private def parseFeed(feed: Elem) = {
	val title = (feed \ "channel" \ "title").text
	val link = (feed \ "channel" \ "link").text
	val items = feed \\ "item"
	val diaries = items.map { item => {
	  val date = (item \ "date").text
	  val title = (item \ "title").text
	  val link = (item \ "link").text
	  val description = (item \ "description").text
	  HatenaDiary(parseDate(date), title, link, description)
	}
			 }
	HatenaDiaryFeed(title, link, diaries)
  }
 
  private def parseDate(date: String) = {
	val dateRegex = new Regex("""(\d{4})-(\d{2})-(\d{2}).*""", "year", "month", "day")
	dateRegex.replaceAllIn(date, m => m.group("year")+"/"+m.group("month")+"/"+m.group("day"))
  }
}