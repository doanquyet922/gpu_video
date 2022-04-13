package com.example.gpu_video_plugin.widget

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.example.gpu_video_plugin.gpuv.camerarecorder.GPUCameraRecorder
import com.example.gpu_video_plugin.widget.SampleCameraGLView.TouchListener
import io.flutter.plugin.platform.PlatformView

internal class CameraWidget(context: Context, frameLayout1: FrameLayout, id: Int,creationParams: Map<String?, Any?>?) : PlatformView {
    private val frameLayout: FrameLayout = frameLayout1
//    private var sampleGLView: SampleCameraGLView = sampleGLView1
//    private var GPUCameraRecorder: GPUCameraRecorder? = GPUCameraRecorder1
    override fun getView(): View {
        return frameLayout
    }

    override fun dispose() {
//        frameLayout.removeView(sampleGLView)
    }

    init {

////        val path: String = creationParams?.get(key = "path") as String
//        activity.runOnUiThread(Runnable {
//            Log.d("BBB","CameraWidget")
////            val frameLayout: FrameLayout = findViewById<FrameLayout>(R.id.wrap_view)
//
//            frameLayout.removeAllViews()
////            sampleGLView = null
////            sampleGLView = SampleCameraGLView(getApplicationContext())
//            sampleGLView.setTouchListener(TouchListener { event: MotionEvent, width: Int, height: Int ->
//                GPUCameraRecorder?.changeManualFocusPoint(event.x, event.y, width, height)
//            })
//            frameLayout.addView(sampleGLView)
//        })
    }
}