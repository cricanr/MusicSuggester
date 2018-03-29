package models

import play.api.Configuration

case class SpotifyApiKeys(clientId: String, clientSecret: String, redirectUri: String, oauthToken: String)

object SpotifyApiKeys {
  def apply(config: Configuration): SpotifyApiKeys =
    new SpotifyApiKeys(
      config.get[String]("CLIENT_ID"),
      config.get[String]("CLIENT_SECRET"),
      config.get[String]("REDIRECT_URI"),
      config.get[String]("OAUTH_TOKEN")
    )
}