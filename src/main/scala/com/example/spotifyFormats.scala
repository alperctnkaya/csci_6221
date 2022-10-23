package com.example
import spray.json.DefaultJsonProtocol

object spotifyFormats extends DefaultJsonProtocol {

  implicit val imageFormat = jsonFormat3(Image)
  implicit val albumSimpleFormat = jsonFormat8(AlbumSimple)
  implicit val artistSimpleFormat = jsonFormat6(ArtistSimple)
  implicit val trackLinkFormat = jsonFormat5(TrackLink)
  implicit val audioFeaturesFormat = jsonFormat18(AudioFeatures)
  implicit val trackFormat = jsonFormat18(Track)
  implicit val listAudioFeaturesFormat = jsonFormat1(listAudioFeatures)
  implicit val followersFormat = jsonFormat2(Followers)
  implicit val publicUserFormat = jsonFormat7(PublicUser)
  implicit val playlistTrackFormat = jsonFormat4(PlaylistTrack)
  implicit val playlistSimple = jsonFormat11(PlaylistSimple)
  implicit val pagePlaylistTrackFormat = jsonFormat7(Page[PlaylistTrack])
  implicit val pagePlaylistSimpleFormat = jsonFormat7(Page[PlaylistSimple])
  implicit val playlistFormat = jsonFormat14(Playlist)

}