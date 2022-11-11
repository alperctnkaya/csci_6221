package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, post, path}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spotifyFormats._
import scala.io.StdIn

class HttpServer {
  def start(): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "system")
    implicit val executionContext = system.executionContext

    val spotify = new SpotifyClient("BQD7uHScLsTP4rEtr_nS00HrGA0o7_JYar6CqAmN3LjquXj6N_fDP6Pkc_w1sf7zOLOvkQK4btaP3IU1gp3ALg76uWNkObQxm69OOwPQO8wX309jV2Dy9hUCFkO3oHJMZAvos1Fx98PgLS8alvH-lkaeqFiF069Kkio3Gkfs9XhqlnEfLA")
    val recommender = new trackRecommender()

    val route = concat (
      path("getUsersPlaylists") {
        post {
          println("POST getUsersPlaylists")

          entity(as[HttpServerModels.userName]) { body =>
            println(body.username)
            complete {
              val usersPlaylists = spotify.playlist.getUsersPlaylists(body.username)
              usersPlaylists
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

            complete {
              var t = List[String]()

              val playlist = spotify.playlist.getPlaylist(body.playlistID)
              playlist.tracks.items.filter(item => item.track != None).foreach(item =>  t = item.track.get.id.get::t)
              var tracksAudioFeatures = spotify.track.getTracksAudioFeatures(t)
              val meanAudioFeatures = recommender.getMeanAudioFeatures(tracksAudioFeatures)

              (playlist, meanAudioFeatures)


            }
          }

        }
      }

      ,

      path("getRecommendations") {
        post {
          println("POST getRecommendations")

          entity(as[HttpServerModels.recommendationRequest]) { body =>

            complete {
              var tSource = List[String]()
              var tTarget = List[String]()

              val playlistSource = spotify.playlist.getPlaylist(body.sPlaylistID)

              playlistSource.tracks.items.filter(item => item.track != None).foreach(item =>  tSource = item.track.get.id.get::tSource)
              val sourceTracksAudioFeatures = spotify.track.getTracksAudioFeatures(tSource)

              val playlistTarget = spotify.playlist.getPlaylist(body.tPlaylistID)
              playlistTarget.tracks.items.filter(item => item.track != None).foreach(item =>  tTarget = item.track.get.id.get::tTarget)
              val targetTracksAudioFeatures = spotify.track.getTracksAudioFeatures(tTarget)

              val recommendation = recommender.recommend(sourceTracksAudioFeatures, targetTracksAudioFeatures)

              //recommendation
              //targetTracksAudioFeatures.audio_features.filter(item => recommendation.contains(item.id.get))
              playlistTarget.tracks.items.filter( item => recommendation.contains(item.track.get.id.get))//.map( item => (item.track.get.external_urls(Option("spotify"))))

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
            complete {
              val trackAudioFeatures = spotify.track.getTrackAudioFeatures(body.username)
              trackAudioFeatures
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