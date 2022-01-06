package com.ravi.kutukidemo

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.ravi.kutukidemo.adapter.VideoTitleAdapter
import com.ravi.kutukidemo.model.Videos
import com.ravi.kutukidemo.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity(), View.OnClickListener, VideoTabClickListner {
    private lateinit var rvTitle: RecyclerView
    private lateinit var playerView: PlayerView
    private lateinit var ibFullScreen: ImageButton
    private lateinit var ibFullScreenExit: ImageButton
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBack: Button
    private var mPlayer: ExoPlayer? = null
    private lateinit var mainViewModel: MainViewModel
    var vidIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initViews()
        initListeners()
        initPlayer()
        getVideos()
    }

    private fun initViews() {
        playerView = findViewById(R.id.playerView)
        rvTitle = findViewById(R.id.rvVideosTitles)
        ibFullScreen = findViewById(R.id.exo_fullscreen)
        ibFullScreenExit = findViewById(R.id.exo_zoom_out)
        btnBack = findViewById(R.id.btnBack)
        constraintLayout = findViewById(R.id.constraintLayout)
        progressBar = findViewById(R.id.pbPlayer)

        // initialize viewModel
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun initListeners() {
        btnBack.setOnClickListener(this)
        ibFullScreen.setOnClickListener(this)
        ibFullScreenExit.setOnClickListener(this)
        vidIndex = intent?.getIntExtra("vidIndex", 0) ?: 0
    }

    private fun setTitleRecyclerView(videoList: List<Videos>) {
        val adapter = VideoTitleAdapter(videoList, applicationContext, this, vidIndex)
        rvTitle.setHasFixedSize(true)
        rvTitle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTitle.adapter = adapter
    }

    private fun playVideo(videoList: List<Videos>) {
        mPlayer?.setMediaSource(buildMediaSource(videoList[vidIndex].videoURL))
        mPlayer?.prepare()
    }

    private fun getVideos() {
        mainViewModel.getVideos()
        mainViewModel.videoList.observe(this, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        showLoader(false)
                        // update recyclerview and start video player
                        setTitleRecyclerView(it)
                        playVideo(it)
                    }
                }
                is NetworkResult.Error -> {
                    showLoader(false)
                    Toast.makeText(this, response.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    showLoader(true)
                }
            }
        })
    }

    private fun initPlayer() {
        // initialised video player
        mPlayer = ExoPlayer.Builder(this).build()
        playerView.player = mPlayer
        mPlayer?.playWhenReady = true
    }

    /** Build media source with video url
     * @param vidUrl: video Url
     */
    private fun buildMediaSource(vidUrl: String): MediaSource {
        var videoUrl = vidUrl
        if (vidUrl.contains("http:")) {
            videoUrl = "https" + vidUrl.drop(4)
        }
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUrl))
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.exo_fullscreen -> {
                    setFullScreenMode()
                    hideSystemUI()
                }
                R.id.exo_zoom_out -> {
                    setNormalMode()
                    showSystemUI()
                }
                R.id.btnBack -> {
                    finish()
                }
            }
        }
    }

    private fun setFullScreenMode() {
        btnBack.visibility = View.GONE
        rvTitle.visibility = View.GONE
        ibFullScreen.visibility = View.GONE
        ibFullScreenExit.visibility = View.VISIBLE
        val mConstraintSet = ConstraintSet()
        mConstraintSet.clone(constraintLayout)
        mConstraintSet.constrainPercentWidth(R.id.playerView, 1f)
        mConstraintSet.applyTo(constraintLayout)
    }

    private fun setNormalMode() {
        btnBack.visibility = View.VISIBLE
        rvTitle.visibility = View.VISIBLE
        ibFullScreen.visibility = View.VISIBLE
        ibFullScreenExit.visibility = View.GONE

        val mConstraintSet = ConstraintSet()
        mConstraintSet.clone(constraintLayout)
        mConstraintSet.constrainPercentWidth(R.id.playerView, 0.7f)
        mConstraintSet.applyTo(constraintLayout)
    }

    private fun showLoader(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun getVideo(index: String?) {
        mPlayer?.setMediaSource(buildMediaSource(index!!))
        mPlayer?.prepare()
    }

    override fun onPause() {
        super.onPause()
        mPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
        mPlayer = null
    }

    /** show status bar and bottom buttons in normal mode*/
    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            constraintLayout
        ).show(WindowInsetsCompat.Type.systemBars())
    }

    /** hide status bar and bottom buttons in full screen*/
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, constraintLayout)
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}