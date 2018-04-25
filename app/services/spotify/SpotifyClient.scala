package services.spotify

import java.net.URI
import java.util.concurrent.{Future => JFuture}

import com.google.inject.Inject
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import com.wrapper.spotify.model_objects.specification.{AlbumSimplified, Artist, Paging}
import models.SpotifyApiKeys
import play.api.Configuration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.Try

class SpotifyClient @Inject()(config: Configuration) {
  val spotifyApiKeys: SpotifyApiKeys = models.SpotifyApiKeys(config)

  val spotifyApi: SpotifyApi = new SpotifyApi.Builder()
    .setClientId(spotifyApiKeys.clientId)
    .setClientSecret(spotifyApiKeys.clientSecret)
    .setRedirectUri(new URI(spotifyApiKeys.redirectUri))
    .build()

  def spotifyApiBuild(spotifyApiKeys: SpotifyApiKeys, redirectUri: String): SpotifyApi = {
    import com.wrapper.spotify.SpotifyApi

    new SpotifyApi.Builder()
      .setClientId(spotifyApiKeys.clientId)
      .setClientSecret(spotifyApiKeys.clientSecret)
      .setRedirectUri(new URI(spotifyApiKeys.redirectUri))
      .build
  }

  def getArtist(artistId: String): Future[Artist] = {
    val getArtistRequest = spotifyApi.getArtist(artistId).build()
    val artistJFut: JFuture[Artist] = getArtistRequest.executeAsync()
    val promise = Promise[Artist]()
    Future {
      promise.complete(Try(artistJFut.get))
    }
    val artistFut: Future[Artist] = promise.future
    artistFut
  }

  def getArtistAlbums(id: String): Future[Paging[AlbumSimplified]] = {
    val getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(id)
      .limit(10)
      .album_type("album")
      .build()

    val artistAlbumsJFut: JFuture[Paging[AlbumSimplified]] = getArtistsAlbumsRequest.executeAsync[Paging[AlbumSimplified]]()
    val promise = Promise[Paging[AlbumSimplified]]()
    Future {
      promise.complete(Try(artistAlbumsJFut.get))
    }
    promise.future
  }

  def authorizeCodeUri(): URI = {
    val spotifyApi: SpotifyApi = spotifyApiBuild(spotifyApiKeys, "")
    val authorizationCodeUriRequest = spotifyApi.authorizationCodeUri
      .state("x4xkmn9pu3j6ukrs8n")
      .scope("user-read-birthdate,user-read-email,user-read-birthdate")
      .show_dialog(true)
      .build

    authorizationCodeUriRequest.execute()
  }

  def setTokens(authorizationCodeCredentials: AuthorizationCodeCredentials): Unit = {
    spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken)
    spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken)
  }

  def authorizeCode(code: String, uri: URI): AuthorizationCodeCredentials = {
    val authorizationCodeRequest = spotifyApi.authorizationCode(code).build

    authorizationCodeRequest.execute()
  }

  def authorizationCodeRefresh(): AuthorizationCodeCredentials = {
    val authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
    val authorizationCodeCredentials = authorizationCodeRefreshRequest.build().execute()
    setTokens(authorizationCodeCredentials)

    authorizationCodeCredentials
  }
}