package com.example.pockplayer.player

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.seventh.dguardcloud.utils.getDateNow
import com.example.pockplayer.model.Camera
import com.kevalpatel2106.rulerpicker.RulerValuePicker
import java.util.*

class Exoplayer(
    private val context: Context,
    private val camera: Camera,
    private val progress: ProgressBar,
 //   private val playerView: PlayerView,
    private val urlCustomized: String?,
    private val startSavedPosition: Long,
    private val displayTime: Long,
    autoPlay: Boolean,
    startWindow: Int,
    savedPosition: Long?
): AppCompatActivity() {
    private var mSavedInstance: Bundle? = null

    private var mIsToday: Boolean = true
    private var mIsLiveMode: Boolean = urlCustomized == null
    private var mStartAutoPlay = autoPlay
    private var mStartWindow: Int = startWindow
    private var mUrlCustom: String? = urlCustomized
    private var mShowTimebar: Boolean = false
    private var mHourPosition: Int = -1
    private var mMinutesPosition: Int = -1
    private val mHandlerChangeTime = Handler()
  //  private val mRunnable: Runnable = getRunnableUpdateTime()
    private var mPathThumbnail: String? = null
    private var mLatestThumbnail: Long = 0
    private var mIsFullScreenMode: Boolean = false
  //  private var mDay: Int = getDateNow().get(Calendar.DAY_OF_MONTH)
    private var mMonth: Int = getDateNow().get(Calendar.MONTH)
    private var mYear: Int = getDateNow().get(Calendar.YEAR)

    private lateinit var mTimeCamera: Calendar
//    private lateinit var mLiveTimeBar: DefaultTimeBar
 //   private lateinit var mExoPlayer: ExoPlayer
    private lateinit var mRulerPicker: RulerValuePicker
 //   private lateinit var mDatePicker: DatePickerTimeline
    private lateinit var mButtonIndicatorLive: Button
    private lateinit var mLabelHour: TextView
    private lateinit var mLabelDay: TextView
    private lateinit var mLayoutTimebar: RelativeLayout
    private lateinit var mLayoutTimeline: RelativeLayout
 //   private lateinit var mediaSource: MediaSource

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            mSavedInstance = savedInstanceState
        }
    }


//    private var mPosition: Long = if (savedPosition != C.TIME_UNSET) savedPosition ?: 0
//    else getPositionNow()
//        set(value) {
//            field = value
//            mIsLiveMode = (value == getPositionNow())
//        }
//
//    init {
//        mIsFullScreenMode = (context.resources.configuration.orientation ==
//                Configuration.ORIENTATION_LANDSCAPE)
//
//        initializeExoPlayer()
//    }

    private fun initializeExoPlayer() {

    }

    private fun getPositionNow(): Long {
        val dateNow = Calendar.getInstance()
        val dateStartToday = Calendar.getInstance()

        dateStartToday.set(
            dateNow.get(Calendar.YEAR),
            dateNow.get(Calendar.MONTH),
            dateNow.get(Calendar.DAY_OF_MONTH), 0, 0, 0
        )

        val dayMinutes = ((dateNow.timeInMillis / 1000) - (dateStartToday.timeInMillis / 1000))

        return (dayMinutes / 60)
    }
}