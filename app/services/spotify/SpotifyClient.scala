package services.spotify

import java.net.URI

import com.google.inject.Inject
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Artist
import models.SpotifyApiKeys
import play.api.Configuration

class SpotifyClient @Inject()(config: Configuration) {
  val spotifyApiKeys: SpotifyApiKeys = models.SpotifyApiKeys(config)

  val spotifyApi: SpotifyApi = spotifyApiBuild(spotifyApiKeys)

  def spotifyApiBuild(spotifyApiKeys: SpotifyApiKeys): SpotifyApi = {
    import com.wrapper.spotify.SpotifyApi

    new SpotifyApi.Builder()
      .setClientId(spotifyApiKeys.clientId)
      .setClientSecret(spotifyApiKeys.clientSecret)
      .setRedirectUri(new URI(spotifyApiKeys.redirectUri)) 
      .setAccessToken(spotifyApiKeys.oauthToken)
      .build
  }

  def getArtist(artistId: String)(): Artist = {
    val getArtistRequest = spotifyApi.getArtist(artistId).build()
    getArtistRequest.execute()
  }
}
