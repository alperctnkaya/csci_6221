package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.duration.DurationInt
import scala.concurrent.Await
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spotifyFormats._

class SpotifyClient(val authToken: String ){

  PlaylistEndpoint.setBearerToken(authToken)
  TracksEndpoint.setBearerToken(authToken)

  implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
  implicit val executionContext = system.executionContext

  object playlist {

    def getUsersPlaylists(userId: String): spotifyApiModels.Page[spotifyApiModels.PlaylistSimple] = {
      val request = PlaylistEndpoint.getUsersPlaylists(userId)
      val response = Await.result(Http().singleRequest(request), 10.second)
      try {
        Await.result(Unmarshal(response.entity).to[spotifyApiModels.Page[spotifyApiModels.PlaylistSimple]], 10.second)
      }
    }

    def getPlaylist(playlistId: String): spotifyApiModels.Playlist ={
      val request = PlaylistEndpoint.getPlaylist(playlistId)
      val response = Await.result(Http().singleRequest(request), 10.second)
      try {
        Await.result(Unmarshal(response.entity).to[spotifyApiModels.Playlist], 10.second)
      }
    }
  }

  object track {

    def getTrack(trackId: String):spotifyApiModels.Track =  {
      val request = TracksEndpoint.getTrack(trackId)
      val response = Await.result(Http().singleRequest(request), 10.second)
      try {
        Await.result(Unmarshal(response.entity).to[spotifyApiModels.Track], 10.second)
      }
    }

    def getTracksAudioFeatures(tracksIds: List[String]): spotifyApiModels.listAudioFeatures = {
      val request = TracksEndpoint.getTracksAudioFeatures(tracksIds)
      val response = Await.result(Http().singleRequest(request), 10.second)
      try{
        Await.result(Unmarshal(response.entity).to[spotifyApiModels.listAudioFeatures], 10.second)
      }
    }

    def getTrackAudioFeatures(trackId: String): spotifyApiModels.AudioFeatures = {
      val request = TracksEndpoint.getTrackAudioFeatures(trackId)
      val response = Await.result(Http().singleRequest(request), 10.second)
      try{
        Await.result(Unmarshal(response.entity).to[spotifyApiModels.AudioFeatures], 10.second)
      }

      //.map(e => Await.result(Unmarshal(e).to[AudioFeatures], 1.seconds))

      /*
      Http().singleRequest(request).onComplete{
        case Success(res) => {
          val obj = Await.result(Unmarshal(res).to[AudioFeatures], 1000 millis)
        }
        case Failure(_) => sys.error("something wrong")
      }
      */

    }
  }

}
