package com.example

  case class Image(height: Option[Int], url: Option[String], width: Option[Int])

  case class listAudioFeatures(
                                audio_features: List[AudioFeatures]
                              )

  case class AudioFeatures(
                          danceability: Float,
                          energy: Float,
                          key: Int,
                          loudness: Float,
                          mode: Int,
                          speechiness: Float,
                          acousticness: Float,
                          instrumentalness: Float,
                          liveness: Float,
                          valence: Float,
                          tempo: Float,
                          `type`: Option[String],
                          id: Option[String],
                          uri: Option[String],
                          track_href: Option[String],
                          analysis_url: Option[String],
                          duration_ms: Int,
                          time_signature: Float,
                        )

  case class Followers(
                        href: Option[Option[String]],
                        total: Int
                      )

  case class PublicUser(
                           display_name: Option[Option[String]],
                           external_urls: Map[Option[String], Option[String]],
                           followers: Option[Followers],
                           href: Option[String],
                           id: Option[String],
                           `type`: Option[String],
                           uri: Option[String]
                         )

  case class PlaylistTrack(
                              //added_at: java.util.Date, //spray json does not have java.util.Date formatter by default
                              added_at : Option[String],
                              added_by: Option[PublicUser],
                              is_local: Boolean,
                              track: Option[Track]
                            )

  case class PlaylistSimple(
                             collaborative: Boolean,
                             external_urls: Map[Option[String], Option[String]],
                             href: Option[String],
                             id: Option[String],
                             images: List[Image],
                             name: Option[String],
                             owner: PublicUser,
                             public: Option[Boolean],
                             snapshot_id: Option[String],
                             tracks: Map[Option[String], Option[String]],
                             `type`: Option[String],
                             uri: Option[String]
                           )
  case class Page[T](
                      href: Option[String],
                      items: List[T],
                      limit: Int,
                      next: Option[Option[String]],
                      offset: Int,
                      previous: Option[Option[String]],
                      total: Int
                    )

  case class Playlist(
                       collaborative: Boolean,
                       description: Option[String],
                       external_urls: Map[Option[String], Option[String]],
                       followers: Followers,
                       href: Option[String],
                       id: Option[String],
                       images: List[Image],
                       name: Option[String],
                       owner: PublicUser,
                       public: Option[Boolean],
                       snapshot_id: Option[String],
                       tracks: Page[PlaylistTrack],
                       `type`: Option[String],
                       uri: Option[String]
                     )

  case class TrackLink(
                        external_urls: Map[Option[String], Option[String]],
                        href: Option[String],
                        id: Option[String],
                        `type`: Option[String],
                        uri: Option[String]
                      )

  case class ArtistSimple(
                           external_urls: Map[Option[String], Option[String]],
                           href: Option[String],
                           id: Option[String],
                           name: Option[String],
                           `type`: Option[String],
                           uri: Option[String]
                         )

  case class AlbumSimple(
                          album_type: Option[String],
                          available_markets: List[Option[String]],
                          external_urls: Map[Option[String], Option[String]],
                          href: Option[String],
                          id: Option[String],
                          images: List[Image],
                          name: Option[String],
                          `type`: Option[String],
                          uri: Option[String]
                        )

  case class Track(
                    album: Option[AlbumSimple],
                    artists: List[ArtistSimple],
                    disc_number: Int,
                    duration_ms: Int,
                    explicit: Boolean,
                    external_ids: Map[Option[String], Option[String]],
                    external_urls: Map[Option[String], Option[String]],
                    href: Option[String],
                    id: Option[String],
                    is_local: Option[Boolean],
                    is_playable: Option[Boolean],
                    linked_from: Option[TrackLink],
                    name: Option[String],
                    popularity: Int,
                    preview_url: Option[String],
                    track_number: Int,
                    `type`: Option[String],
                    uri: Option[String],
                    available_markets: List[Option[String]],
                  )


