import React from 'react'
import { useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as Yup from 'yup'
import playListtracks from '../playlisttracks.json'

function PlaylistForm ({playlistssongData, playlistid}) {
  const validationSchema = Yup.object().shape({
    playListID: Yup.string().required('')
  })

  const playListFormOptions = { resolver: yupResolver(validationSchema) }

  const { register, handleSubmit, formState } = useForm(playListFormOptions)
  const { errors } = formState

  const onSubmit = (data, e) => {
    // display form data on success
    e.preventDefault();
    console.log('3',playListtracks.tracks.items)
    playlistssongData(playListtracks.tracks.items)
  }

  React.useEffect(()=> {
    console.log(playlistid, playlistid.length)
    if(playlistid && playlistid.length > 0)  playlistssongData(playListtracks.tracks.items)
  },[playlistid])

  return (
    <React.Fragment>
      <form
        onSubmit={handleSubmit(onSubmit)}
        style={{ marginTop: '40px' }}
        className='form'
      >
        <input
          name='playListID'
          type='text'
          {...register('playListID')}
          placeholder='Enter PlayListID'
          className='form__field'
          //className={`form-control ${errors.playListID ? 'is-invalid' : ''}`}
        />
        <div className='invalid-feedback'>{errors.playListID?.message}</div>
        <button type='submit' className='btn btn--primary btn--inside'>
          SEND
        </button>
      </form>
    </React.Fragment>
  )
}

export default PlaylistForm
