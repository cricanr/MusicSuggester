package controllers

import java.net.URI

import actors.SpotifyTokenRefreshScheduler
import com.google.inject.Inject
import javax.inject.Singleton
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.mvc._
import services.spotify.SpotifyClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               spotifyClient: SpotifyClient,
                               ws: WSClient,
                               spotifyTokenRefreshScheduler: SpotifyTokenRefreshScheduler)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val authorizeCodeUri = spotifyClient.authorizeCodeUri()
  val fut = callRedirectUri(authorizeCodeUri)


  def index = Action.async {
    val authorizeCodeUri = spotifyClient.authorizeCodeUri()
    callRedirectUri(authorizeCodeUri)
  }

  def callRedirectUri(uri: URI)(implicit ec: ExecutionContext): Future[Result] = {
    val request: WSRequest = ws.url(uri.toURL.toString)
    val futureCall = request.withRequestTimeout(1000.millis).get()

    futureCall.map(response => Ok(views.html.spotifyAuthorize("Spotify Authorization page", response.body, authorizeCodeUri.toURL.toString)))
  }

  def authorize = Action.async { request =>
    val queryString = request.queryString.map { case (k, v) => k -> v.mkString }
    val code = queryString.getOrElse("code", "")

    val authorizeCodeCredentials = spotifyClient.authorizeCode(code, authorizeCodeUri)

    println(s"Spotify authorization credentials will expire in: ${authorizeCodeCredentials.getExpiresIn / 60} minutes!")

    spotifyClient.setTokens(authorizeCodeCredentials)

    val artistFut = spotifyClient.getArtist("0OdUWJ0sBjDrqHygGUXeCF")
    val artistAlbumsPagingFut = spotifyClient.getArtistAlbums("0OdUWJ0sBjDrqHygGUXeCF")

    val artistAlbumsFut = artistAlbumsPagingFut.map(artistAlbumsPaging => artistAlbumsPaging.getItems.toSeq)

    for {artist <- artistFut
         artistAlbumsPaging <- artistAlbumsPagingFut
         artistAlbums <- artistAlbumsFut
    } yield Ok(views.html.authorized(artist, artistAlbumsPaging, artistAlbums))
  }

  def authorized() = Action.async { _ =>
    Future.successful(Ok(views.html.index("cat")))
  }
}