package com.example.pockplayer.home

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.pockplayer.R
import com.example.pockplayer.model.Camera
import com.example.pockplayer.player.Exoplayer

class PlaylistFragment : Fragment() {

    private var mContext: Context? = null
//    private var mPositionTimeline: Long = C.TIME_UNSET
    private var mUrlCustomized: String? = null
    private var mPlayerAutoPlay: Boolean = true
    private var mPlayerWindowIndex: Int = 0
 //   private var mSavedPositionTimeline: Long = C.TIME_UNSET
 //   private var mDisplayTime: Long = C.TIME_UNSET

    private lateinit var mCamera: Camera
    private lateinit var mExoplayer: Exoplayer
  //  private lateinit var mPlayerView: PlayerView

    companion object {
        @JvmStatic
        fun newInstance(): PlaylistFragment = PlaylistFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = activity?.applicationContext

        savedInstanceState?.let {
            mCamera = it.getParcelable(getString(R.string.key_camera_selected))!!

            mPositionTimeline = it.getLong(getString(R.string.key_timeline_position), C.TIME_UNSET)
            mPlayerAutoPlay = it.getBoolean(getString(R.string.key_player_state))
            mUrlCustomized = it.getString(getString(R.string.key_timeline_url_custom))
            mPlayerWindowIndex = it.getInt(getString(R.string.key_timeline_start_index))
            mSavedPositionTimeline = it.getLong(getString(R.string.key_timeline_position_timeline))
            mDisplayTime = it.getLong(getString(R.string.key_timeline_display_time_timeline))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return initializeView(inflater, container)
    }

    /** Initialize the fragment with base on the device orientation */
    private fun initializeView(inflater: LayoutInflater, container: ViewGroup?): View? {

        val isFullScreenMode =
            (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)

        val root: View

        if (isFullScreenMode) {
            root = inflater.inflate(R.layout.fragment_fullscreen, container, false)
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        } else {
            root = inflater.inflate(R.layout.fragment_playlist, container, false)
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        }

        setViewsPlayer(isFullScreenMode, root)
        setViewsControllerPlayer(root)

        return root
    }

    /** Add the listeners to buttons of the controller view */
    private fun setViewsControllerPlayer(root: View) {
        root.findViewById<ImageButton>(R.id.exo_player_full_screen_button).setOnClickListener {
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                Configuration.ORIENTATION_PORTRAIT -> {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                Configuration.ORIENTATION_SQUARE -> {
                    System.out.println("ORIENTATION_SQUARE")
                }
                Configuration.ORIENTATION_UNDEFINED -> {
                    System.out.println("ORIENTATION_UNDEFINED")
                }
            }
        }
    }

    /** Set the Views reference based on the device orientation */
    private fun setViewsPlayer(isFullScreenMode: Boolean, root: View) {

        val idProgress: Int =
            if (isFullScreenMode) R.id.exo_buffering_fragment_full
            else R.id.exo_buffering_playlist

        val idPlayer: Int =
            if (isFullScreenMode) R.id.exo_player_fragment_full
            else R.id.view_exoplayer_playlist

        mPlayerView = root.findViewById(idPlayer)

        buildExoPlayer(root.findViewById(idProgress))
    }

    /** Get new instance exo_playback_control_portrait */
    private fun buildExoPlayer(progress: ProgressBar) {
        progress.visibility = View.VISIBLE;
        mExoplayer = Exoplayer(
            requireContext(),
            mCamera,
            progress, mPlayerView,
            mUrlCustomized,
            mPositionTimeline,
            mDisplayTime,
            mPlayerAutoPlay,
            mPlayerWindowIndex,
            mSavedPositionTimeline
        )
    }

    fun setData(camera: Camera) {
        mCamera = camera
    }


    /** Change the Camera in the fragment, using detach and attach */
    fun changeCamera(newCamera: Camera) {
        releasePlayer()
        mCamera = newCamera

        requireActivity().supportFragmentManager.beginTransaction().detach(this)
            .attach(this)
            .commit()

        mPositionTimeline = C.TIME_UNSET
        mUrlCustomized = null
    }

    /** Release the player pausing the Camera and clear the player */
    private fun releasePlayer() {
        mPlayerView.player?.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(getString(R.string.key_camera_selected), mCamera)
        mPlayerView.player?.let { outState.putBoolean(getString(R.string.key_player_state),
            it.playWhenReady
        ) }

        if (!mExoplayer.isLiveMode()) {
            mPlayerView.player?.playWhenReady = false

            var timeFirstFrameRecorded: Long = C.TIME_UNSET

            if (!mPlayerView.player?.currentTimeline?.isEmpty!!) {
                timeFirstFrameRecorded = mPlayerView.player!!.currentTimeline
                    .getWindow(0, Timeline.Window())
                    .positionInFirstPeriodMs
            }

            outState.putLong(
                getString(R.string.key_timeline_position),
                mExoplayer.getCurrentPosition()
            )

            outState.putLong(
                getString(R.string.key_timeline_start_time),
                timeFirstFrameRecorded
            )

            outState.putInt(
                getString(R.string.key_timeline_start_index),
                mPlayerView.player?.currentWindowIndex!!
            )

            outState.putString(
                getString(R.string.key_timeline_url_custom),
                mExoplayer.getUrlCustomized()
            )

            outState.putLong(
                getString(R.string.key_timeline_display_time_timeline),
                mExoplayer.getDisplayTime()
            )
        }


        outState.putLong(
            getString(R.string.key_timeline_position_timeline),
            mExoplayer.getPositionTimeline()
        )

        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        if (mPlayerView.player != null){
            mPlayerView.player?.stop()
            mPlayerView.player?.release()
        }
        super.onPause()
    }
    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }
}
