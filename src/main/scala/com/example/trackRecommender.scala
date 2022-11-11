package com.example

class trackRecommender {
  var numTracksToBeRecommended = 2

  def recommend(sourcePlaylist: spotifyApiModels.listAudioFeatures, targetPlaylist: spotifyApiModels.listAudioFeatures) : List[String]= {

    var recommendations = List[String]()
    val meanAudioFeatures = getMeanAudioFeatures(sourcePlaylist)
    findClosestTracks(meanAudioFeatures, targetPlaylist, numTracksToBeRecommended)

  }

  def getMeanAudioFeatures(sourcePlaylist: spotifyApiModels.listAudioFeatures) : spotifyApiModels.AudioFeatures = {

    val sourcePlaylistLen = sourcePlaylist.audio_features.size

    val audioFeatures = sourcePlaylist.audio_features.filter(item => item.danceability != -1.0)

    val danceability = audioFeatures.map(_.danceability).sum / sourcePlaylistLen
    val energy: Float = audioFeatures.map(_.energy).sum / sourcePlaylistLen
    val key: Int = audioFeatures.map(_.key).sum / sourcePlaylistLen
    val loudness: Float = audioFeatures.map(_.loudness).sum / sourcePlaylistLen
    val mode: Int = audioFeatures.map(_.mode).sum / sourcePlaylistLen
    val speechiness: Float = audioFeatures.map(_.speechiness).sum / sourcePlaylistLen
    val acousticness: Float = audioFeatures.map(_.acousticness).sum / sourcePlaylistLen
    val instrumentalness: Float = audioFeatures.map(_.instrumentalness).sum / sourcePlaylistLen
    val liveness: Float = audioFeatures.map(_.liveness).sum / sourcePlaylistLen
    val valence: Float = audioFeatures.map(_.valence).sum / sourcePlaylistLen
    val tempo: Float = audioFeatures.map(_.tempo).sum / sourcePlaylistLen

    spotifyApiModels.AudioFeatures.apply(danceability = danceability, energy = energy,
      key = key, loudness = loudness, mode = mode, speechiness = speechiness, acousticness = acousticness,
      instrumentalness = instrumentalness, liveness = liveness, valence = valence, tempo = tempo, `type` = None,
      id = None, uri = None, track_href = None, analysis_url = None, duration_ms = 0, time_signature = 1f )
  }

  def findClosestTracks(meanSourceAudioFeatures: spotifyApiModels.AudioFeatures, targetPlaylist: spotifyApiModels.listAudioFeatures, limit: Int): List[String]  ={
    targetPlaylist.audio_features.map(item => (item.id.get,distanceTracks(meanSourceAudioFeatures,item)))
      .sortBy(_._2)
      .map(item => item._1)
      .take(limit)
  }

  def distanceTracks(item1 : spotifyApiModels.AudioFeatures, item2 : spotifyApiModels.AudioFeatures): Double ={
    //euclidian Distance between two tracks

    scala.math.sqrt(
      scala.math.pow(item1.danceability - item2.danceability ,2) +
      scala.math.pow(item1.energy - item2.energy ,2) +
      //scala.math.pow(item1.key - item2.key ,2) +
      //scala.math.pow(item1.loudness - item2.loudness ,2) +
      //scala.math.pow(item1.mode - item2.mode ,2) +
      scala.math.pow(item1.speechiness - item2.speechiness ,2) +
      scala.math.pow(item1.acousticness - item2.acousticness ,2) +
      scala.math.pow(item1.instrumentalness - item2.instrumentalness ,2) +
      scala.math.pow(item1.liveness - item2.liveness ,2) +
      scala.math.pow(item1.valence - item2.valence ,2)
      //scala.math.pow(item1.tempo - item2.tempo ,2)
    )
  }
}
