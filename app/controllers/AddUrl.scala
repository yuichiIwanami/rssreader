package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._
import models._

import logics.RssItems

object AddUrl extends Controller {
	
	val addForm = Form("url" -> text)
	
	def display = Action {
		Ok(views.html.add(addForm))
	}
	
	def add = Action { implicit request =>
		addForm.bindFromRequest.fold(
			formWithErrors => BadRequest(views.html.add(addForm)),
			url => {
				dao.addRSS(url)
				Redirect(routes.AddUrl.display)
			}			
		)
	}
}