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
  def index = Action {
    val artist = spotifyClient.getArtist("0OdUWJ0sBjDrqHygGUXeCF")
    val artist2 = spotifyClient.getArtist("0LcJLqbBmaGUft1e9Mm8HV")
    val artistAlbumsPaging = spotifyClient.getArtistAlbums("0LcJLqbBmaGUft1e9Mm8HV")

    println(s"spotifyApi: ${spotifyClient.spotifyApi}")
    println(s"artist: ${artist.getName}")
    println(s"Artist: ${artist2.getName} has released the following albums #${artistAlbumsPaging.getTotal} albums: ")
    val artistAlbums = artistAlbumsPaging.getItems.toSeq

    artistAlbums.foreach { artistAlbum => println(s"Album: ${artistAlbum.getName}") }

    Ok(views.html.index("Your new application is ready."))
  }
}