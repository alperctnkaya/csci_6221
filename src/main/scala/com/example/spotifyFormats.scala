package com.example

import spray.json._


object spotifyFormats extends DefaultJsonProtocol {

  case class audioFeaturesFormatJsonFormat[T](format:RootJsonFormat[T], size : Int) extends RootJsonFormat[T]{

    def read(json: JsValue): T = json match {
      case JsNull => {
        var jsonDefault = """{ "danceability": -1.0 , "energy": 0.0,  "key": 0,  "loudness": -0.0,  "mode": 0,  "speechiness": 0.0,  "acousticness": 0.0,  "instrumentalness": 0.0,  "liveness": 0.0,  "valence": 0.0,  "tempo": 0.0,  "type": "",  "id": "",  "uri": "",  "track_href": "",  "analysis_url": "",  "duration_ms": 0,  "time_signature": 0}"""
        format.read(jsonDefault.parseJson)

      }

      case _ => {
        if (json.asJsObject.fields.size < size) {
          var jsonDefault = """{ "danceability": 0.0 , "energy": 0.0,  "key": 0,  "loudness": 0.0,  "mode": 0,  "speechiness": 0.0,  "acousticness": 0.0,  "instrumentalness": 0.0,  "liveness": 0.0,  "valence": 0.0,  "tempo": 0.0,  "type": "",  "id": "",  "uri": "",  "track_href": "",  "analysis_url": "",  "duration_ms": 0,  "time_signature": 0}"""
          format.read(jsonDefault.parseJson)
        }
        else {
          format.read(json)
        }
      }
    }

    def write(obj: T): JsValue = format.write(obj)
  }

  implicit val audioFeaturesFormat = audioFeaturesFormatJsonFormat(jsonFormat18(spotifyApiModels.AudioFeatures), 18)

  //implicit val audioFeaturesFormat = jsonFormat18(spotifyApiModels.AudioFeatures)
  implicit val usernameFormat = jsonFormat1(HttpServerModels.userName)
  implicit val playlistNameFormat = jsonFormat1(HttpServerModels.playlistID)
  implicit val recommendationRequestFormat = jsonFormat2(HttpServerModels.recommendationRequest)

  implicit val imageFormat = jsonFormat3(spotifyApiModels.Image)
  implicit val albumSimpleFormat = jsonFormat8(spotifyApiModels.AlbumSimple)
  implicit val artistSimpleFormat = jsonFormat6(spotifyApiModels.ArtistSimple)
  implicit val trackLinkFormat = jsonFormat5(spotifyApiModels.TrackLink)

  implicit val trackFormat = jsonFormat18(spotifyApiModels.Track)
  implicit val listAudioFeaturesFormat = jsonFormat1(spotifyApiModels.listAudioFeatures)
  implicit val followersFormat = jsonFormat2(spotifyApiModels.Followers)
  implicit val publicUserFormat = jsonFormat7(spotifyApiModels.PublicUser)
  implicit val playlistTrackFormat = jsonFormat4(spotifyApiModels.PlaylistTrack)
  implicit val playlistSimple = jsonFormat11(spotifyApiModels.PlaylistSimple)
  implicit val pagePlaylistTrackFormat = jsonFormat7(spotifyApiModels.Page[spotifyApiModels.PlaylistTrack])
  implicit val pagePlaylistSimpleFormat = jsonFormat7(spotifyApiModels.Page[spotifyApiModels.PlaylistSimple])
  implicit val playlistFormat = jsonFormat14(spotifyApiModels.Playlist)

}
