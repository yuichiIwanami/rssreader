
package controllers

import play.api._
import play.api.mvc._

import logics.RssItems
import models.dao

object RssReader extends Controller {
	
	def display(rss: String, count: Int) = Action {
		val entries = RssItems.getEntries(rss, count)
		Ok(views.html.display(RssItems.getBlogs, entries))
	}
}