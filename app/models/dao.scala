package models

import play.api.db.DB
import play.api.Play.current
import anorm._

object dao {
	
		def getRss: List[String] = {		
				DB.withConnection { implicit conn =>	
					val selectRSSes = SQL("Select * from rss").apply()
					selectRSSes.map(row => row[String]("rss")).toList	
				}					
		}			
		
		def addRSS(url: String) {			
			DB.withConnection { implicit conn =>
				SQL("insert into rss(rss) values({url})")
					.on('url -> url).executeUpdate()				
			}
		}
}