package com.example

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken, `Set-Cookie`}
import akka.http.scaladsl.model._


abstract class SpotifyEndpoint {

  protected var bearerToken : OAuth2BearerToken = _

  protected val baseAPIUrl = "https://api.spotify.com"

  def setBearerToken(token: String): Unit ={
    this.bearerToken = headers.OAuth2BearerToken(token)
  }

  protected def createRequest(endpoint: String): HttpRequest = {
    HttpRequest(uri = endpoint).withHeaders(Authorization(bearerToken))
  }

}

object PlaylistEndpoint extends SpotifyEndpoint{
  private val playlistEndpoint = baseAPIUrl + "/v1/playlists/"

  def getPlaylist(playlistId:String): HttpRequest ={
    createRequest(playlistEndpoint  + playlistId)
  }

  def getUsersPlaylists(user_id: String): HttpRequest ={
    createRequest(baseAPIUrl + "/v1/users/" + user_id + "/playlists")
  }

}

object TracksEndpoint extends SpotifyEndpoint {

  private val tracksEndpoint = baseAPIUrl + "/v1"

  def getTrack(trackId: String): HttpRequest ={
    createRequest(tracksEndpoint + "/tracks/" + trackId)
  }

  def getTrackAudioFeatures(trackId: String): HttpRequest =
    createRequest(tracksEndpoint + "/audio-features/" + trackId)

  def getTracksAudioFeatures(tracksIds: List[String]): HttpRequest = {
    val ids = tracksIds.mkString("%")
    createRequest(tracksEndpoint + "/audio-features?ids=" + ids)
  }
}