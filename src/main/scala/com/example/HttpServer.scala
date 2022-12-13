package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, options, path, post, respondWithHeaders}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpMethods.{DELETE, GET, OPTIONS, POST, PUT}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import spotifyFormats._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import akka.http.scaladsl.server.{Directive0, Route}


trait CORSHandler {

  private val corsResponseHeaders                          = List(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With", "Origin")
  )
  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders: Directive0 = {
    respondWithHeaders(corsResponseHeaders)
  }
  //this handles preflight OPTIONS requests.
  private def preflightRequestHandler: Route               = options {
    complete(
      HttpResponse(StatusCodes.OK).withHeaders(
        `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)
      )
    )
  }
  // Wrap the Route with this method to enable adding of CORS headers
  def corsHandler(r: Route): Route                         = addAccessControlHeaders {
    concat(preflightRequestHandler, r)
  }
  // Helper method to add CORS headers to HttpResponse
  // preventing duplication of CORS headers across code
  def addCORSHeaders(response: HttpResponse): HttpResponse =
    response.withHeaders(corsResponseHeaders)

}

class HttpServer {
  def start(): Unit = {
    implicit val system = ActorSystem("Server")

    val spotify = new SpotifyClient("BQCRxSFBcE88En-7LgdVnfQS8tEBNxEJo2J0jMMdDoFSpGr078-MvhLyn8Qbs-YCIH7HLejmt77D6nUMr776XyG2wuAGYrzJ_y-ZCc5gytFzaERPBtV7P96FJzcYHlhb0Yv5s_EFSso686aLe9lg66Ua-ybfZjEZ5i93UBMqNQ7h6wLKzA")
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
                playlist.tracks.items.filter(item => item.track != None).foreach(item => t = item.track.get.id.get :: t)
                val tracksAudioFeatures = spotify.track.getTracksAudioFeatures(t)
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

                playlistSource.tracks.items.filter(item => item.track != None).foreach(item =>  tSource = item.track.get.id.get :: tSource)
                val sourceTracksAudioFeatures = spotify.track.getTracksAudioFeatures(tSource)

                val playlistTarget = spotify.playlist.getPlaylist(body.tPlaylistID)
                playlistTarget.tracks.items.filter(item => item.track != None).foreach(item =>   tTarget = item.track.get.id.get :: tTarget)
                val targetTracksAudioFeatures = spotify.track.getTracksAudioFeatures(tTarget)

                val recommendation = recommender.recommend(sourceTracksAudioFeatures, targetTracksAudioFeatures)
                val recommendAudioFeatures = spotify.track.getTracksAudioFeatures(recommendation)
                val meanRecommendAudioFeatures = recommender.getMeanAudioFeatures(recommendAudioFeatures)

                //recommendation
                //targetTracksAudioFeatures.audio_features.filter(item => recommendation.contains(item.id.get))

                //(playlistTarget.tracks.items.filter(item => recommendation.contains(item.track.get.id.get)) , //.map( item => (item.track.get.external_urls(Option("spotify"))))

                (Map("tracks" -> playlistTarget.tracks.items.filter(item => recommendation.contains(item.track.get.id.get)).map(item => item.track)),
                meanRecommendAudioFeatures)

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

      ,

      path("getRecommendationsSpotify") {
        post {
          println("POST getRecommendationsSpotify")

          entity(as[HttpServerModels.playlistID]) { body =>
            println(body.playlistID)

            complete {
              var artistIDs = List[String]()
              var genres = List[String]()
              var trackIDs = List[String]()
              var selectedTrackIDs = List[String]()
              var recommendationIDs = List[String]()

              val playlistSource = spotify.playlist.getPlaylist(body.playlistID)

              playlistSource.tracks.items.filter(item => item.track != None).foreach(item => artistIDs = item.track.get.artists.head.id.get :: artistIDs)
              playlistSource.tracks.items.filter(item => item.track != None).foreach(item =>  trackIDs = item.track.get.id.get :: trackIDs)

              artistIDs = artistIDs.take(50) // 50 is the limit for the API
              val artists = spotify.artist.getArtists(artistIDs)

              artists.artists.filter(item => item.genres.size != 0).foreach(item => genres = item.genres.head :: genres)

              genres = genres.groupBy(identity).mapValues(_.length).toSeq.sortBy(_._2).reverse.map(item => item._1).toList
              artistIDs = artistIDs.groupBy(identity).mapValues(_.length).toSeq.sortBy(_._2).reverse.map(item => item._1).toList


              playlistSource.tracks.items.filter(item => item.track != None).filter(item => artistIDs.contains(item.track.get.artists.head.id.get))
                .map( item => selectedTrackIDs = item.track.get.id.get :: selectedTrackIDs)

              genres = genres.take(2)
              artistIDs = artistIDs.take(2)
              selectedTrackIDs = selectedTrackIDs.distinct.take(1)

              val recommendations = spotify.browse.getRecommendations(artistIDs,genres,selectedTrackIDs)
              recommendations.tracks.foreach(item => recommendationIDs = item.id.get::recommendationIDs)

              val recommendAudioFeatures = spotify.track.getTracksAudioFeatures(recommendationIDs)
              val meanRecommendAudioFeatures = recommender.getMeanAudioFeatures(recommendAudioFeatures)


              (recommendations, meanRecommendAudioFeatures)
              //(genres,artistIDs, selectedTrackIDs)
            }
          }
        }
      }

    )
    val cors  = new CORSHandler {}

    val host = "0.0.0.0"
    val port: Int = sys.env.getOrElse("PORT", "8080").toInt
    Http().newServerAt(host, port).bind(cors.corsHandler(route))
    //Http().bindAndHandle(route, host, port)
    println(s"Server now online.  \nPress RETURN to stop...")

  }
}
