package actors

import actors.SpotifyTokenRefreshActor.RefreshToken
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import javax.inject.{Inject, Named}
import services.spotify.SpotifyClient

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class SpotifyTokenRefreshScheduler @Inject()(actorSystem: ActorSystem, @Named("spotify-token-refresh-actor") someActor: ActorRef)(implicit executionContext: ExecutionContext) {

  def schedule(spotifyClient: SpotifyClient) = {
    actorSystem.scheduler.schedule(
      initialDelay = 0.microseconds,
      interval = 30.seconds,
      receiver = someActor,
      message = RefreshToken(spotifyClient)
    )
  }
}

object SpotifyTokenRefreshActor {
  case class RefreshToken(spotifyClient: SpotifyClient)
}

class SpotifyTokenRefreshActor extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case RefreshToken(spotifyClient: SpotifyClient) =>
      spotifyClient.authorizationCodeRefresh()
  }
}