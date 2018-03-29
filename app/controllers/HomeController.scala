package controllers

import java.net.URI

import com.google.inject.Inject
import play.api.mvc._
import services.spotify.SpotifyClient

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               spotifyClient: SpotifyClient,
                               ws: WSClient) extends AbstractController(cc) {
  def index = Action {
    val authorizeCodeUri = spotifyClient.authorizeCodeUri()
    println(s"authorizeCodeUri: $authorizeCodeUri")
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

  def callRedirectUri(uri: URI)(implicit ec: ExecutionContext): Unit = {
    println("uri as string: " + uri.toURL.toString)
    val request: WSRequest = ws.url(uri.toURL.toString)
    val futResponse: Future[WSResponse] = request.withRequestTimeout(1000.millis).get()

    // TODO: check future if success or failure
    futResponse.map {

      response => Ok(views.html.spotifyAuthorize("Spotify Authorization page", response.body))
    }
  }

  def authorize = Action {
    println("spotify auth page")
    Ok(views.html.spotifyAuthorize("Spotify Authorization page", ""))
  }
}