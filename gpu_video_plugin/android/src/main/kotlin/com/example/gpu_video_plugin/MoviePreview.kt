package com.example.gpu_video_plugin

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import com.example.gpu_video_plugin.gpuv.egl.filter.GlFilter
import com.example.gpu_video_plugin.gpuv.player.GPUPlayerView
import com.example.gpu_video_plugin.widget.MovieWrapperView
import com.example.gpu_video_plugin.widget.PlayerTimer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.*

class MoviePreview(context1: Context) {
    private var context: Context = context1
    private var player: SimpleExoPlayer? = null
    private var gpuPlayerView: GPUPlayerView? = null
    private var playerTimer: PlayerTimer? = null
    private lateinit var movieWrapperView: MovieWrapperView
    private var filter: GlFilter? = null
    private var adjuster: FilterAdjuster? = null

    fun setFilterMovie(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
//        val data: HashMap<String, Any?> = call.arguments as HashMap<String, Any?>
        val nameGlFilter: String = call.arguments as String
        val filters = FilterType.values()
        var filterType: FilterType = filters[2]
        for (i in filters) {
            if (i.name == nameGlFilter) {
                filterType = i
            }
        }
        filter = FilterType.createGlFilter(filterType, context)
        adjuster = FilterType.createFilterAdjuster(filterType)
//        findViewById<View>(R.id.filterSeekBarLayout).setVisibility(if (adjuster != null) View.VISIBLE else View.GONE)
        gpuPlayerView!!.setGlFilter(filter)
        result.success("ok")
    }


    fun setUpMovieView(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result): MovieWrapperView {
        val url: String = call.arguments as String
        setUpSimpleExoPlayer(url)
        setUoGlPlayerView()
        return movieWrapperView
    }

    private fun setUpSimpleExoPlayer(url: String) {
        // SimpleExoPlayer
        player = SimpleExoPlayer.Builder(context)
                .setTrackSelector(DefaultTrackSelector(context))
                .build()
        player?.addMediaItem(MediaItem.fromUri(Uri.parse(url)))
        player?.prepare()
        player?.playWhenReady = true
    }

    private fun setUoGlPlayerView() {
        gpuPlayerView = GPUPlayerView(context)
        gpuPlayerView?.setSimpleExoPlayer(player)
        gpuPlayerView?.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        movieWrapperView = MovieWrapperView(context)
        movieWrapperView.addView(gpuPlayerView)
        gpuPlayerView?.onResume()
    }

    private fun setUpTimer() {
        playerTimer = PlayerTimer()
        playerTimer?.setCallback(PlayerTimer.Callback {
            val position = player?.currentPosition
            val duration = player?.duration
            if (duration != null) {
                if (duration <= 0) return@Callback
            }
//            timeSeekBar.setMax(duration.toInt() / 1000)
//            timeSeekBar.setProgress(position.toInt() / 1000)
        })
        playerTimer?.start()
    }

    fun removeView() {
        Log.d("BBB", "removeView: MoviePreview")
        releasePlayer()
        if (playerTimer != null) {
            playerTimer?.stop()
            playerTimer?.removeMessages(0)
        }
    }

    private fun releasePlayer() {
        gpuPlayerView?.onPause()
        movieWrapperView.removeAllViews()
        gpuPlayerView = null
        player?.stop()
        player?.release()
        player = null
    }
}