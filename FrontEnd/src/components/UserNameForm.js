import React from 'react'
import { useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as Yup from 'yup'
import axios from 'axios'

function UserNameForm ({ playlists, id, loading, errorFunciton , handlePlaylistID}) {
  const [usernameOrPlaylistID1, setUsernameOrPlaylistID1] = React.useState('')
  const [usernameOrPlaylistID2, setUsernameOrPlaylistID2] = React.useState('')
  const validationSchema = Yup.object().shape({
    searchField: Yup.string().required('')
  })

  const formOptions = { resolver: yupResolver(validationSchema) }

  const { register, handleSubmit, reset, formState } = useForm(formOptions)
  const { errors } = formState

  function onSubmit (data, e) {
    // display form data on success
    e.preventDefault()
    let inputboxnumber = 0
    if (id === '1') {
      inputboxnumber = 1
      setUsernameOrPlaylistID1(data.searchField)
    } else {
      inputboxnumber = 2
      setUsernameOrPlaylistID2(data.searchField)
    }
    let requestBody = {}
    if (data.searchField.startsWith('https')) {
      requestBody['playlistID'] = data.searchField.substring(
        data.searchField.indexOf('playlist/') + 9,
        data.searchField.lastIndexOf('?')
      )
      loading(id, true)
      axios
        .post('http://localhost:8080/getPlaylist', requestBody)
        .then(response => {
          console.log(response.data[0].tracks.items)
          handlePlaylistID(requestBody['playlistID'], id, response.data)
          //playlists(response.data.items, data.searchField, inputboxnumber)
          loading(id, false)
        })
        .catch(error => {
          errorFunciton()
          loading(id, false)
        })
    } else {
      requestBody['username'] = data.searchField
      loading(id, true)
      axios
        .post('http://localhost:8080/getUsersPlaylists', requestBody)
        .then(response => {
          playlists(response.data.items, data.searchField, inputboxnumber)
          loading(id, false)
        })
        .catch(error => {
          errorFunciton()
          loading(id, false)
        })
    }
  }

  return (
    <React.Fragment>
      <form onSubmit={handleSubmit(onSubmit)} className='form' id={'form' + id}>
        <input
          className='form__field'
          name='searchField'
          type='text'
          {...register('searchField')}
          placeholder='Enter Username or Playlist Url'
          //className={` form__field form-control ${errors.searchField ? 'is-invalid' : ''}`}
        />
        <div className='invalid-feedback'>{errors.searchField?.message}</div>
        <button
          type='submit'
          className='btn btn--primary btn--inside'
          id={'button' + id}
        >
          SEND
        </button>
      </form>
    </React.Fragment>
  )
}

export default UserNameForm
