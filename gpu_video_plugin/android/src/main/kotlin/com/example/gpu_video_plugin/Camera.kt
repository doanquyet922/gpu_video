package com.example.gpu_video_plugin

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.NonNull
import com.example.gpu_video_plugin.gpuv.camerarecorder.CameraRecordListener
import com.example.gpu_video_plugin.gpuv.camerarecorder.GPUCameraRecorder
import com.example.gpu_video_plugin.gpuv.camerarecorder.GPUCameraRecorderBuilder
import com.example.gpu_video_plugin.gpuv.camerarecorder.LensFacing
import com.example.gpu_video_plugin.widget.SampleCameraGLView
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.*

class Camera(context1: Context, activity1: Activity, sampleGLView1: SampleCameraGLView, GPUCameraRecorder1: GPUCameraRecorder?) {
    private var context: Context = context1
    private var activity: Activity = activity1
    private var sampleGLView: SampleCameraGLView = sampleGLView1
    private var GPUCameraRecorder: GPUCameraRecorder? = GPUCameraRecorder1
    private var lensFacing = LensFacing.BACK
     fun setUpCamera(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        val data: HashMap<String, Any?> = call.arguments as HashMap<String, Any?>
        val cameraHeight: Int = data["cameraHeight"] as Int
        val cameraWidth: Int = data["cameraWidth"] as Int
//        setUpCameraView()
        GPUCameraRecorder = GPUCameraRecorderBuilder(activity, sampleGLView) //.recordNoFilter(true)
//                .cameraRecordListener(object : CameraRecordListener {
//                    override fun onGetFlashSupport(flashSupport: Boolean) {
//                        activity.runOnUiThread(Runnable {
////                            findViewById<View>(R.id.btn_flash).setEnabled(flashSupport)
//                        })
//                    }
//
//                    override fun onRecordComplete() {
////                        BaseCameraActivity.exportMp4ToGallery(context, filepath)
//                    }
//
//                    override fun onRecordStart() {
//                        activity.runOnUiThread(Runnable {
////                            lv.setVisibility(View.GONE)
//                        })
//                    }
//
//                    override fun onError(exception: Exception) {
//                        Log.e("GPUCameraRecorder", exception.toString())
//                    }
//
//                    override fun onCameraThreadFinish() {
////                        if (toggleClick) {
////                            runOnUiThread(Runnable { setUpCamera() })
////                        }
////                        toggleClick = false
//                    }
//
//                    override fun onVideoFileReady() {}
//                })
                .videoSize(cameraWidth, cameraHeight)
                .cameraSize(cameraWidth, cameraHeight)
                .lensFacing(lensFacing)
                .build()
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 99999
    }
}