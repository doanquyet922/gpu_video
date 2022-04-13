package com.example.gpu_video_plugin.widget

import android.content.Context
import android.widget.FrameLayout
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class MovieViewFactory(movieWrapperView1: MovieWrapperView) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private val movieWrapperView: MovieWrapperView = movieWrapperView1
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?
        return MovieViewWidget(context, movieWrapperView, viewId, creationParams)
    }
}