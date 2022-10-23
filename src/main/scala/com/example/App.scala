package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}


object App {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    implicit val executionContext = system.executionContext

    implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
      EntityStreamingSupport.json()


        val spotify = new SpotifyClient("BQDdfNYEzZYjX7WQ6cwGt0UHVbdTvAgZmk3CK8JnWRmOUByBsT3CywoDNCC5XxH_RRFUiMlQ13RSAB8TKE0wvnSKMBzm1sGS1xNBzCm0fFbx2ZIf_k1Gs8_NkP7qBmoV1hGReCztg2bUVUTmDXyZjFb-oe1Dd_9SplPhTweqBkS1p-V2qA")

        val trackId = "7ouMYWpwJ422jRcDASZB7P"
        val playlistId = "37i9dQZF1DWVfS4Cdd3PNf"
        val userName = ""

        val trackObj = spotify.track.getTrack(trackId)
        val trackFeaturesObj = spotify.track.getTrackAudioFeatures(trackId)
        val playlistObj = spotify.playlist.getPlaylist(playlistId)
        //val userNameObj = spotify.playlist.getUsersPlaylists(userName)

        println(trackObj)
        println(trackFeaturesObj)
        println(playlistObj.tracks.items.head)
        //println(userNameObj.items.head)


  }
}