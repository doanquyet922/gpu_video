package com.example.gpu_video_plugin.widget

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import com.example.gpu_video_plugin.gpuv.camerarecorder.GPUCameraRecorder
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class CameraFactory(frameLayout1: FrameLayout) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private val frameLayout: FrameLayout = frameLayout1
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?
        return CameraWidget(context, frameLayout, viewId, creationParams)
    }
}