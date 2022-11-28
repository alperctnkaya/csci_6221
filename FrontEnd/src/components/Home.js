import React, { useState, useEffect, useRef } from 'react'
import UserNameForm from './UserNameForm'
import PlaylistForm from './PlaylistIDForm'
import DefaultPlaylists from './DefaultPlaylists'
import defaultPlaylists from '../test.json'
import Recomendations from './Recomendations'
import axios from 'axios'
import {Bars} from 'react-loading-icons'
import Alert from 'react-bootstrap/Alert';


function Home () {
  const [playlistsData1, setPlaylists1] = React.useState([])
  const [playlistsData2, setPlaylists2] = React.useState([])
  const [playlistssongs1, setPlaylistSongs1] = React.useState([])
  const [playlistssongs2, setPlaylistSongs2] = React.useState([])
  const [playlistid, setPlaylistId] = React.useState('')
  const [recommendSongsFromBE, setRecomendSongs] = React.useState([])
  const [sourcePlayListId, setSourcePlaylistID] = React.useState('')
  const [targetPlaylistId, setTargetPlaylistID] = React.useState('')
  const [searchField1, setSearchField1] = React.useState('')
  const [searchField2, setSearchField2] = React.useState('')
  const [loading1, setLoading1] = React.useState(false)
  const [loading2, setLoading2] = React.useState(false)
  const [error, setErrorStatus] = React.useState(false)
  const [recommendSongsLoading, setRecomendedSongsLoading] = React.useState(false)
  const [rowHeight, setRowHeight] = React.useState('')
  const sourceColumn = useRef(null)
  const targetColumn = useRef(null)
  const playlists = (data, searchfielddata, inputboxnumber) => {
    if (inputboxnumber == 1) {
      setPlaylists1(data)
      setPlaylistSongs1([])
    } else {
      setPlaylists2(data)
      setPlaylistSongs2([])
    }

    if (searchfielddata.length > 0 && inputboxnumber == 1)
      setSearchField1(searchfielddata)
    else if (searchfielddata.length > 0 && inputboxnumber == 2)
      setSearchField2(searchfielddata)
  }

  const playlistssongData = (id, songs) => {
    if (id === '1') {
      setPlaylists1([])
      setPlaylistSongs1(songs)
    } else {
      setPlaylists2([])
      setPlaylistSongs2(songs)
    }
  }

  const handlePlaylistID = (data, id, songs) => {
    // setPlaylistId(data)
    // setPlaylists1([])
    console.log(songs)
    if (id === '1') {
      setSourcePlaylistID(data)
      setPlaylists1([])
      setPlaylistSongs1(songs)
    } else {
      setTargetPlaylistID(data)
      setPlaylists2([])
      setPlaylistSongs2(songs)
    }
  }

  const clearData = () => {
      console.log('in')
        setPlaylists1(defaultPlaylists.playlists.items)
        setPlaylistSongs1([])
        setSourcePlaylistID("")
        setPlaylistSongs2([])
        setTargetPlaylistID("")
        setPlaylists2(defaultPlaylists.playlists.items)
        setRecomendSongs([])
  }

  const recommendSongs = () => {
    let requestBody = {}
    if(targetPlaylistId.length === 0){
        requestBody['playlistID'] = sourcePlayListId
        setRecomendedSongsLoading(true)
        axios
          .post('http://localhost:8080/getRecommendationsSpotify', requestBody)
          .then(response => {
            setRecomendSongs(response.data)
            setRecomendedSongsLoading(false)
          })
          .catch(error => {
            console.log(error)
            errorFunciton();
          })
    }
    if(sourcePlayListId.length !==0 && targetPlaylistId.length !== 0){
      requestBody['sPlaylistID'] = sourcePlayListId
      requestBody['tPlaylistID'] = targetPlaylistId
      setRecomendedSongsLoading(true)
      axios
        .post('http://localhost:8080/getRecommendations', requestBody)
        .then(response => {
          setRecomendSongs(response.data)
          setRecomendedSongsLoading(false)
        })
        .catch(error => {
          console.log(error)
          errorFunciton();
        })
    }
  }

  const loading = (id, status) => {
    if(id === "1" ) setLoading1(status)
    else setLoading2(status)
  }
  const errorFunciton = () => {
    setErrorStatus(true)
    setTimeout(() => {
      setErrorStatus(false)
   }, 3000)
  }

  React.useEffect(() => {
    if (playlistsData1.length === 0 && playlistssongs1.length == 0)
      setPlaylists1(defaultPlaylists.playlists.items)
    if (playlistsData2.length === 0 && playlistssongs2.length == 0)
      setPlaylists2(defaultPlaylists.playlists.items)
    // var height1 = document.getElementById('source-column').clientHeight;
    // var height2 = document.getElementById('target-column').clientHeight;
    //setRowHeight(Math.max(sourceColumn.current.clientHeight, targetColumn.current.clientHeight))
  }, [])

  return (
    <React.Fragment>
      {error && 
          <Alert key='danger' variant='danger'>
          Somethings seems to wrong. Please try after sometime.
        </Alert>
      }
      
      {recommendSongsFromBE.length === 0 && (
        <div className='container' style={{opacity: recommendSongsLoading ? '0.2' : '1', height: '100%'}}>
          <div className='row header_logo'>
            <img
              className='spotify_logo'
              src='/Spotify_logo.png'
              style={{ width: '50px', height: '50px' }}
              alt=''
            />
            <h1 className='spotify_title'> Spotify Recomendations </h1>
          </div>
          <div className='row input_body'>
            {/* <button
              type='button'
              className='btn btn-success'
              id='recommend'
              onClick={recommendSongs}
            >
              Recommend
            </button> */}
          </div>
          <div className='row input_boxes'>
            <div className='col-lg-5' ref={sourceColumn}>
              <UserNameForm playlists={playlists} id='1' loading={loading} errorFunciton={errorFunciton} handlePlaylistID={handlePlaylistID}/>
              {loading1 && <Bars/>}
              <div style={{ marginLeft: '-30px',opacity: loading1 ? '0.2' : '1'  }}>
                {playlistsData1.length > 0 && playlistssongs1.length === 0 && (
                  <DefaultPlaylists
                    playlistsData={playlistsData1}
                    tracks={false}
                    handlePlaylistID={handlePlaylistID}
                    recommendSongs={recommendSongs}
                    index='1'
                    loading={loading}
                    errorFunciton={errorFunciton}
                  />
                )}
                {playlistssongs1.length > 0 && (
                  <DefaultPlaylists
                    playlistsData={playlistssongs1}
                    tracks={true}
                    recommendSongs={recommendSongs}
                    index='1'
                    loading={loading}
                    errorFunciton={errorFunciton}
                  />
                )}
                 {playlistsData1.length === 0 && playlistssongs1[0] && playlistssongs1[0].tracks.items.length === 0 && <div style={{marginTop: '35px'}}>
                  <h4>No Items Present</h4>
                </div>}
              </div>
            </div>
            <div
              className='col-lg-2'
              style={{
                borderRight: '1px solid white',
                height: rowHeight,
                position: 'absolute',
                right: '50%'
              }}
            ></div>
            <div className='col-lg-5' ref={targetColumn}>
              <UserNameForm playlists={playlists} id='2' loading={loading} errorFunciton={errorFunciton} handlePlaylistID={handlePlaylistID}/>
              {loading2 && <Bars/>}
              <div style={{ marginLeft: '-30px',opacity: loading2 ? '0.2' : '1'   }}>
                {playlistsData2.length > 0 && (
                  <DefaultPlaylists
                    playlistsData={playlistsData2}
                    tracks={false}
                    handlePlaylistID={handlePlaylistID}
                    recommendSongs={recommendSongs}
                    index='2'
                    loading={loading}
                    errorFunciton={errorFunciton}
                  />
                )}
                {playlistssongs2.length > 0 && (
                  <DefaultPlaylists
                    playlistsData={playlistssongs2}
                    tracks={true}
                    recommendSongs={recommendSongs}
                    index='2'
                    loading={loading}
                    errorFunciton={errorFunciton}
                  />
                )}
              </div>
            </div>

            {/* <div>
        <h3>OR</h3>
      </div> */}
            {/* <div>
      <PlaylistForm playlistssongData={playlistssongData}  playlistid={playlistid} />
      </div> */}
      <div className='col-lg-1'>
          <button
              type='button'
              className='btn btn-success'
              id='recommend'
              onClick={recommendSongs}
              disabled= {!(sourcePlayListId.length > 0 && playlistssongs1[0] && playlistssongs1[0].tracks.items.length > 0)}
            >
              Recommend
            </button>
            </div>
          </div>
          <div className='row' style={{ marginLeft: '-30px' }}>
            <div className='col-lg-6'></div>

            <div className='col-lg-6'></div>
          </div>
        </div>
      )}
      {recommendSongsLoading && <Bars style={{position: 'absolute',left: '50%', right: '50%'}}/>}
      {recommendSongsFromBE.length > 0 && 
        <Recomendations  clearData={clearData} data={recommendSongsFromBE} sourcePlaylist={playlistssongs1} targetPlaylist={playlistssongs2}/>
      }
    </React.Fragment>
  )
}

export default Home
