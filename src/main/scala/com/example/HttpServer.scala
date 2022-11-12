package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.javadsl.server.Route
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, path, post}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spotifyFormats._

import scala.io.StdIn
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

class HttpServer {
  def start(): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "system")
    implicit val executionContext = system.executionContext

    val spotify = new SpotifyClient("BQD29qKEoiKmmdrzbwB0RYAA8oMjGanPXsXOBupNmazSgDN87cg0x9sBHttuodCBZ5B9qjN3WARU5MJb-qvD-K6znRH9gRGxzJHHZ_DS2-57ENwXYVRrbm85bznLcYwiUuHVezh2IfnxcnbzL5FQ0IfawNySRP6q2uhAJB3NMfi0NCnjSw")
    val recommender = new trackRecommender()

    val route = concat (
      path("getUsersPlaylists") {
        post {
          println("POST getUsersPlaylists")

          entity(as[HttpServerModels.userName]) { body =>
            println(body.username)
            cors() {
              complete {
                val usersPlaylists = spotify.playlist.getUsersPlaylists(body.username)
                usersPlaylists
              }
            }
          }

          //complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }

      ,

      path("getPlaylist") {
        post {
          println("POST getPlaylist")

          entity(as[HttpServerModels.playlistID]) { body =>

            cors() {
              complete {
                var t = List[String]()

                val playlist = spotify.playlist.getPlaylist(body.playlistID)
                playlist.tracks.items.filter(item => item.track != None).foreach(item => t = item.track.get.id.get :: t)
                var tracksAudioFeatures = spotify.track.getTracksAudioFeatures(t)
                val meanAudioFeatures = recommender.getMeanAudioFeatures(tracksAudioFeatures)

                (playlist, meanAudioFeatures)


              }
            }
          }

        }
      }

      ,

      path("getRecommendations") {
        post {
          println("POST getRecommendations")

          entity(as[HttpServerModels.recommendationRequest]) { body =>
            cors() {
              complete {
                var tSource = List[String]()
                var tTarget = List[String]()

                val playlistSource = spotify.playlist.getPlaylist(body.sPlaylistID)

                playlistSource.tracks.items.filter(item => item.track != None).foreach(item => tSource = item.track.get.id.get :: tSource)
                val sourceTracksAudioFeatures = spotify.track.getTracksAudioFeatures(tSource)

                val playlistTarget = spotify.playlist.getPlaylist(body.tPlaylistID)
                playlistTarget.tracks.items.filter(item => item.track != None).foreach(item => tTarget = item.track.get.id.get :: tTarget)
                val targetTracksAudioFeatures = spotify.track.getTracksAudioFeatures(tTarget)

                val recommendation = recommender.recommend(sourceTracksAudioFeatures, targetTracksAudioFeatures)

                //recommendation
                //targetTracksAudioFeatures.audio_features.filter(item => recommendation.contains(item.id.get))
                playlistTarget.tracks.items.filter(item => recommendation.contains(item.track.get.id.get)) //.map( item => (item.track.get.external_urls(Option("spotify"))))

              }
            }
          }

        }
      }

      ,

      path("getTrackAudioFeatures") {
        post {
          println("POST getTrackAudioFeatures")

          entity(as[HttpServerModels.userName]) { body =>
            println(body.username)
            cors() {
              complete {
                val trackAudioFeatures = spotify.track.getTrackAudioFeatures(body.username)
                trackAudioFeatures
              }
            }
          }

        }
      }

    )

    val bindingFuture = Http().newServerAt("localhost", 8080). bind(route)
    println(s"Server now online. Please navigate to http://localhost:8080/ \nPress RETURN to stop...")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}