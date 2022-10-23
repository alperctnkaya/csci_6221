package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.common.JsonEntityStreamingSupport
import akka.http.scaladsl.common.EntityStreamingSupport


object App {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    implicit val executionContext = system.executionContext

    implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
      EntityStreamingSupport.json()

    val spotify = new SpotifyClient("BQC49nbdSHDLsKxy6ZQkfUE-H9_EYdYPEfG0c59pXw9q6qzyVLDwNueeG3Q6_tFG9_KMQCK5ZPdEennKbUqN9wwfhhug2P8CUXK-KMZ2QPjwy7y83Czb5JQQAPn73KVgswKu_84T1PsLNhCaSss9PiZJjSAANMxqUpLPhOyGM6sV1AtRFg")

    val trackId = "7ouMYWpwJ422jRcDASZB7P"
    val playlistId = "37i9dQZF1DWVfS4Cdd3PNf"

    val trackObj = spotify.track.getTrack(trackId)
    val trackFeaturesObj = spotify.track.getTrackAudioFeatures(trackId)
    val playlistObj = spotify.playlist.getPlaylist(playlistId)

    println(trackObj)
    println(trackFeaturesObj)
    println(playlistObj.tracks.items.head)

  }
}