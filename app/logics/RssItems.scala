package logics

import java.net.{ URL, URLEncoder }
import java.text.SimpleDateFormat
import java.util.{ Date, Locale }
import scala.beans.BeanProperty
import scala.io.Source
import scala.util.parsing.json._
import scala.xml._
import play.api.libs.json._
import models._

object RssItems {
	
	def getBlogs(): List[blog] = {
		for (urlStr <- dao.getRss) yield {new blog(urlStr, 1)}
	}
	
	def getEntries(rss: String, count: Int = 20): List[entry] = {		
		rss match {
			case "all"=> val blogs = for (urlStr <- dao.getRss) yield {new blog(urlStr, count)}
									   sortEntry(blogs)
			case _ => new blog("http://" + rss, count).entrylist
		}
	}
	
	def sortEntry(blogs: List[blog]): List[entry] = {		
		val list = for {blog <- blogs
								item <- blog.entrylist} yield {
								(item)
							}
		sort(list)				
	}

	def sort(list: List[entry]): List[entry] = {
		if (list.size == 1) { return list }
		val mid = list.size / 2
		val left = sort(list.take(mid))
		val right = sort(list.drop(mid))
		merge(left, right)
	}

	def merge(left: List[entry], right: List[entry]): List[entry] = {
		if (left.isEmpty || right.isEmpty) {
			left ::: right
		} else if (left.head.date > right.head.date) {
			List(left.head) ::: merge(left.tail, right)
		} else {
			List(right.head) ::: merge(left, right.tail)
		}
	}
	
	class blog(urlStr: String, count: Int) {
		val connection = new URL("https://ajax.googleapis.com/ajax/services/feed/load?v=1.0"
											+ "&num=" + count + "&q=" + urlStr).openConnection
		connection.addRequestProperty("Referer", "119.240.254.139")
		val source = Source.fromInputStream(connection.getInputStream, "UTF-8").mkString
		val feed: JsValue = Json.parse(source) \ "responseData" \ "feed"
		val mainTitle: String = (feed \ "title").as[String]
		val mainLink: String = (feed \ "link").as[String]
		val feedUrl: String = (feed \ "feedUrl").as[String]
		val encUrl: String = URLEncoder.encode(feedUrl.replace("http://", ""), "UTF-8")
		val entries: List[JsValue] = (feed \ "entries").as[List[JsValue]]
		val entrylist: List[entry] = for (entry <- entries) yield { new entry(entry)(mainTitle, mainLink, encUrl) }	
	}
	
	val sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z")
	val displayformat = new SimpleDateFormat("yyyy/MM/dd HH:mm")
	
	class entry(ent: JsValue)(maintitle: String, mainlink: String, encurl: String) {
		val mainTitle = maintitle
		val mainLink = mainlink
		val encUrl =encurl
		val title = (ent \"title").as[String]
		val link = (ent \ "link").as[String]
		val snippet = (ent \ "contentSnippet").as[String]
		val content = (ent \ "content").as[String]
		val rawdate = sdf.parse((ent \ "publishedDate").as[String])
		val date = displayformat.format(rawdate)		
	}
}