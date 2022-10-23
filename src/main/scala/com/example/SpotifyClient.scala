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

    def getPlaylist(playlistId: String): Playlist ={
      val request = PlaylistEndpoint.getPlaylist(playlistId)

      //prints result
      //Await.result(Http().singleRequest(request), 2.second).entity.
      // toStrict(2 second).map(_.data.utf8String).foreach(println)

      val response = Await.result(Http().singleRequest(request), 2.second)
      Await.result(Unmarshal(response.entity).to[Playlist], 2.second)
    }
  }

  object track {

    def getTrack(trackId: String):Track =  {
      val request = TracksEndpoint.getTrack(trackId)
      val response = Await.result(Http().singleRequest(request), 2.second)
      Await.result(Unmarshal(response.entity).to[Track], 2.second)
    }

    def getTracksAudioFeatures(tracksIds: List[String]): listAudioFeatures = {
      val request = TracksEndpoint.getTracksAudioFeatures(tracksIds)
      val response = Await.result(Http().singleRequest(request), 2.second)
      Await.result(Unmarshal(response.entity).to[listAudioFeatures], 2.second)
    }

    def getTrackAudioFeatures(trackId: String): AudioFeatures = {
      val request = TracksEndpoint.getTrackAudioFeatures(trackId)
      val response = Await.result(Http().singleRequest(request), 2.second)
      Await.result(Unmarshal(response.entity).to[AudioFeatures], 2.second)

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
