package services.spotify

import java.net.URI

import com.google.inject.Inject
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import com.wrapper.spotify.model_objects.specification.{AlbumSimplified, Artist, Paging}
import models.SpotifyApiKeys
import play.api.Configuration

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

  def getArtist(artistId: String): Artist = {
    val getArtistRequest = spotifyApi.getArtist(artistId).build()
    getArtistRequest.execute()
  }

  def getArtistAlbums(id: String): Paging[AlbumSimplified] = {
    val getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(id)
      .limit(10)
      .album_type("album")
      .build()
    getArtistsAlbumsRequest.execute()
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

  def authorizationCodeRefresh(refreshToken: String, spotifyApi: SpotifyApi): AuthorizationCodeCredentials = {
    val authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()

    authorizationCodeRefreshRequest.build().execute()
  }
}