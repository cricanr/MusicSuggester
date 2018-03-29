package controllers

import javax.inject._

import com.google.inject.Inject
import play.api.mvc._
import services.spotify.SpotifyClient


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               spotifyClient: SpotifyClient) extends AbstractController(cc) {


  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = Action {
    val artist = spotifyClient.getArtist("0OdUWJ0sBjDrqHygGUXeCF")

    println(s"spotifyApi: ${spotifyClient.spotifyApi}")
    println(s"artist: ${artist.getName}")

    Ok(views.html.index("Your new application is ready."))
  }
}