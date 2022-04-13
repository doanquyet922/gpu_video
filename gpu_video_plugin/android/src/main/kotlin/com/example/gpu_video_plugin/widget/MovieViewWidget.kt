package com.example.gpu_video_plugin.widget

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.flutter.plugin.platform.PlatformView

internal class MovieViewWidget(context: Context, movieWrapperView1: MovieWrapperView, id: Int, creationParams: Map<String?, Any?>?) : PlatformView {
    private var movieWrapperView: MovieWrapperView = movieWrapperView1
    override fun getView(): View {
        return movieWrapperView
    }

    override fun dispose() {
        Log.d("BBB","removeView: MovieViewWidget")
    }

    init {
    }
}