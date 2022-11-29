import React from 'react'
import '../css/recomendation.css'
import {
  MdArrowBack,
  MdPlayCircleFilled,
  MdPauseCircleFilled,
  MdPlayDisabled,
} from 'react-icons/md'
import { FcComboChart } from "react-icons/fc";

import axios from 'axios'
import AudioPlayer from 'react-h5-audio-player'
import 'primeicons/primeicons.css'
import 'primereact/resources/themes/lara-light-indigo/theme.css'
import 'primereact/resources/primereact.css'
import 'primeflex/primeflex.css'
import { Chart } from 'primereact/chart'
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';


function Recomendations ({ clearData, data, sourcePlaylist, targetPlaylist }) {
  const [tract, setTrack] = React.useState(data)
  const [playingSong, setPlayningSong] = React.useState('')
  const [sourceDataPoints, setSourceDataPoints] = React.useState([])
  const [targetDataPoints, setTargetDataPoints] = React.useState([])

  const [show, setShow] = React.useState(false);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);
  const labels = [
    'acousticness',
    'danceability',
    'energy',
    'instrumentalness',
    'liveness',
    'speechiness',
    'valence'
  ]
  
  const dataPoints = () => {
    let sourcepoints = []
    let targetpoints = []
    let recommendsongspoints = []
    let datasetArray = [];
    console.log(sourcePlaylist[1],targetPlaylist[1],data[1])
    labels.map(label => {
      if (sourcePlaylist[1][label] != undefined) sourcepoints.push(sourcePlaylist[1][label])
      else sourcepoints.push(0)
    })

    if(targetPlaylist[1] != undefined){
      labels.map(label => {
        if (targetPlaylist[1][label] != undefined) targetpoints.push(targetPlaylist[1][label])
        else targetpoints.push(0)
      })
    }

    labels.map(label => {
      console.log(label, data[1])
      if (data[1][label] != undefined) recommendsongspoints.push(data[1][label])
      else recommendsongspoints.push(0)
    })

    datasetArray.push(  {
      label: 'Source Playlost Features',
      backgroundColor: 'rgba(179,181,198,0.2)',
      borderColor: 'rgba(179,181,198,1)',
      pointBackgroundColor: 'rgba(179,181,198,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(179,181,198,1)',
      data: sourcepoints
    });
    if(targetPlaylist[1] != undefined){
      datasetArray.push(        {
        label: 'Target Playlist Features',
        backgroundColor: 'rgba(255,99,132,0.2)',
        borderColor: 'rgba(255,99,132,1)',
        pointBackgroundColor: 'rgba(255,99,132,1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(255,99,132,1)',
        data: targetpoints
      })
    }
    datasetArray.push(        {
      label: 'Recommended Playlist Features',
      backgroundColor: 'rgba(160,99,132,0.2)',
      borderColor: 'rgba(1405,99,132,1)',
      pointBackgroundColor: 'rgba(215,99,132,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(129,99,132,1)',
      data: recommendsongspoints
    })
    setChartData({
      labels: [
        'Acousticness',
        'Danceability',
        'Energy',
        'Instrumentalness',
        'Liveness',
        'Speechiness',
        'Valence'
      ],
      datasets: datasetArray
    })

  }

  function millisToMinutesAndSeconds (millis) {
    let minutes = Math.floor(millis / 60000)
    let seconds = ((millis % 60000) / 1000).toFixed(0)
    return minutes + ':' + (seconds < 10 ? '0' : '') + seconds
  }
  const playSong = url => {
    if (url !== playingSong) setPlayningSong(url)
    else setPlayningSong('')
  }

  const hoverControl = () => {
  }
  React.useEffect(() => {
    dataPoints()
    console.log('1',data);
  },[])

  const [chartData, setChartData] = React.useState({})

  const [lightOptions] = React.useState({
    plugins: {
      legend: {
        labels: {
          color: '#495057'
        }
      }
    },
    scales: {
      r: {
        pointLabels: {
          color: '#495057'
        },
        grid: {
          color: '#ebedef'
        },
        angleLines: {
          color: '#ebedef'
        }
      }
    }
  })

  return (
    <React.Fragment>
      <div class='container' style={{ height: '100vh' }}>
        <div className='row header_logo'>
          <img
            className='spotify_logo'
            src='/Spotify_logo.png'
            style={{ width: '50px', height: '50px' }}
            alt=''
          />
          <h1 className='spotify_title'> Spotify Recomendations</h1>
        </div>
        <div className='row previous_page' onClick={clearData}>
          <MdArrowBack style={{ width: '50px', height: '30px' }} /> Previous
          Page
        </div>
        <div
          style={{
            marginTop: '30px',
            marginBottom: '30px',
            marginLeft: '-9px',
            display: 'flex',
            alignItems: 'end'
          }}
          className='row'
        >
          <strong>RECOMENDATIONS</strong>
          <FcComboChart style={{ width: '50px', height: '30px', cursor: 'pointer' }} onClick={handleShow} />
        </div>
      <Modal show={show} onHide={handleClose}>
        <Modal.Header>
          <Modal.Title>Feature Comparison</Modal.Title>
        </Modal.Header>
        <Modal.Body>
        <Chart
          type='radar'
          data={chartData}
          options={lightOptions}
          style={{ position: 'relative', width: '100%' }}
        />
        </Modal.Body>
      </Modal>
        {data[0].tracks.length > 0 ? 
        data[0].tracks.map(song => (
          <div
            className='each_row'
            style={{ marginLeft: '-18px' }}
            onClick={() => playSong(song.preview_url)}
            onMouseEnter={hoverControl()}
          >
            <div className='musicSet'>
              {song.preview_url &&
                playingSong !== song.preview_url && (
                  <MdPlayCircleFilled
                    className='play-icon'
                    style={{
                      width: '50px',
                      height: '30px',
                      marginRight: '20px'
                    }}
                  />
                )}
              {song.preview_url &&
                playingSong === song.preview_url && (
                  <MdPauseCircleFilled
                    className='play-icon'
                    style={{
                      width: '50px',
                      height: '30px',
                      marginRight: '20px'
                    }}
                  />
                )}
              {!song.preview_url && (
                <MdPlayDisabled
                  className='play-icon'
                  style={{
                    width: '50px',
                    height: '20px',
                    opacity: '0.2',
                    marginRight: '20px'
                  }}
                />
              )}

              <div style={{ position: 'relative' }}>
                <img
                  className='music_image'
                  src={song.album.images[0].url}
                  alt=''
                ></img>
              </div>
              <div className='music_name'>
                <h6>{song.name}</h6>
                <p className='music_description'>
                  {song.artists.map(artist => artist.name).join(', ')} -{' '}
                  {song.album.name}
                </p>
              </div>
              <div className='duration_display'>
                <p>{millisToMinutesAndSeconds(song.duration_ms)}</p>
              </div>
            </div>
          </div>
        ))
        : <div>
          <h4>No Items Found</h4>
        </div>
        }
        
        <AudioPlayer
          // style={{ width: "300px" }}
          autoPlay
          // layout="horizontal"
          src={playingSong}
          onPlay={e =>
            setTimeout(() => {
              setPlayningSong('')
            }, 29000)
          }
          // other props here
        />
      </div>
    </React.Fragment>
  )
}

export default Recomendations
