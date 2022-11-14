package com.example

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken, `Set-Cookie`}
import akka.http.scaladsl.model._
import com.example.spotifyApiModels.AudioFeatures


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

    def getPlaylist(playlistID:String): HttpRequest ={
      createRequest(playlistEndpoint  + playlistID)
    }

    def getUsersPlaylists(userID: String): HttpRequest ={
      createRequest(baseAPIUrl + "/v1/users/" + userID + "/playlists")
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
      val ids = tracksIds.mkString(",")
      createRequest(tracksEndpoint + "/audio-features?ids=" + ids)
    }

    def getArtist(artistID: String): HttpRequest = {
      createRequest(baseAPIUrl + "/v1/artists/" + artistID)
    }

    def getArtists(artistsIDs: List[String]): HttpRequest = {
      val ids= artistsIDs.mkString(",")
      createRequest(baseAPIUrl + "/v1/artists?ids=" + ids)
    }

    def getRecommendations(seedArtists: List[String], seedGenres: List[String], seedTracks: List[String]): HttpRequest ={

      val seed_artists = "seed_artists=" + seedArtists.mkString(",")
      var seed_genres = "seed_genres=" + seedGenres.mkString(",")
      val seed_tracks = "seed_tracks=" + seedTracks.mkString(",")

      seed_genres = seed_genres.replace(" ", "%20")

      createRequest(tracksEndpoint + "/recommendations?limit=10" + "&" + seed_artists + "&" + seed_genres + "&" + seed_tracks)
    }
}