package com.example.gpu_video_plugin

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gpu_video_plugin.compose.VideoItem
import com.example.gpu_video_plugin.compose.VideoLoadListener
import com.example.gpu_video_plugin.compose.VideoLoader
import com.example.gpu_video_plugin.gpuv.composer.FillMode
import com.example.gpu_video_plugin.gpuv.composer.GPUMp4Composer
import com.example.gpu_video_plugin.gpuv.egl.filter.GlFilter
import com.example.gpu_video_plugin.gpuv.egl.filter.GlFilterGroup
import com.example.gpu_video_plugin.gpuv.egl.filter.GlMonochromeFilter
import com.example.gpu_video_plugin.gpuv.egl.filter.GlVignetteFilter
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Mp4Compose(context1: Context, activity1: Activity) {

    private var context: Context = context1
    private var activity: Activity = activity1

    private var GPUMp4Composer: GPUMp4Composer? = null
    private var videoItems: List<VideoItem>? = null

    private var videoPath: String? = null
    private var glFilter: GlFilter = GlFilterGroup(GlMonochromeFilter(), GlVignetteFilter())
    fun playMovie(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        val path: String = call.arguments as String
        val uri = Uri.parse(path)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.setDataAndType(uri, "video/mp4")
        context.startActivity(intent)
    }

    fun startCodec(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result, methodChannel: MethodChannel) {
        val data: HashMap<String, Any?> = call.arguments as HashMap<String, Any?>
        val videoItemIndex: Int = data["videoItemIndex"] as Int
        val nameGlFilter: String = data["nameGlFilter"] as String
        val mute: Boolean = data["mute"] as Boolean
        val flipHorizontal: Boolean = data["flipHorizontal"] as Boolean
        val flipVertical: Boolean = data["flipVertical"] as Boolean
        videoPath = getVideoFilePath()
        glFilter = getFilter(nameGlFilter)
        GPUMp4Composer = null
        if (videoItems != null) {
            GPUMp4Composer = GPUMp4Composer(videoItems!![videoItemIndex].path, videoPath)
                    // .rotation(Rotation.ROTATION_270)
                    //.size(720, 720)
                    .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                    .filter(glFilter)
                    .mute(mute)
                    .flipHorizontal(flipHorizontal)
                    .flipVertical(flipVertical)
                    .listener(object : GPUMp4Composer.Listener {
                        override fun onProgress(progress: Double) {
                            Log.d(TAG, "onProgress = $progress")
                            activity.runOnUiThread(Runnable { methodChannel.invokeMethod("processFilter", progress) })
                        }

                        override fun onCompleted() {
                            Log.d(TAG, "onCompleted()")
                            exportMp4ToGallery(context, videoPath!!)
                            activity.runOnUiThread(Runnable { methodChannel.invokeMethod("processFilter", 1.0) })
                        }

                        override fun onCanceled() {}
                        override fun onFailed(exception: Exception) {
                            Log.d(Mp4Compose.TAG, "onFailed()")
                        }
                    })
                    .start()
        }

    }

    private fun getFilter(nameFilter: String): GlFilter {
        val filters = FilterType.values()
        var filterType: FilterType = filters[2]
        for (i in filters) {
            if (i.name == nameFilter) {
                filterType = i
            }
        }
        return FilterType.createGlFilter(filterType, context)
    }

    private fun getAndroidMoviesFolder(): File? {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
    }

    private fun getVideoFilePath(): String {
        return getAndroidMoviesFolder()?.absolutePath + "/" + SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date()) + "filter_apply.mp4"
    }

    fun exportMp4ToGallery(context: Context, filePath: String) {
        val values = ContentValues(2)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATA, filePath)
        context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values)
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://$filePath")))
    }

    fun videoLoaders(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        Log.d("BBB", "videoLoaders")
        var videoLoader: VideoLoader? = null
        if (checkPermission()) {
            videoLoader = VideoLoader(context)
            videoLoader.loadDeviceVideos(object : VideoLoadListener {
                override fun onVideoLoaded(items: List<VideoItem>) {
                    videoItems = items
                    val maps: ArrayList<MutableMap<String, Any>> = ArrayList()
                    for (i in items) {
                        val mutableMap: MutableMap<String, Any> = mutableMapOf<String, Any>()
                        mutableMap["path"] = i.path
                        mutableMap["duration"] = i.duration
                        mutableMap["height"] = i.height
                        mutableMap["width"] = i.width
                        maps.add(mutableMap)
                    }
                    result.success(maps)
                }

                override fun onFailed(e: Exception) {
                    e.printStackTrace()
                }
            })
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        // request permission if it has not been grunted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }


    //    private fun checkPermission2() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return
//        }
//        // request camera permission if it has not been grunted.
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), CAMERA_PERMISSION_REQUEST_CODE)
//        }
//    }
    companion object {
        private val TAG = "Mp4Compose"
        const val PERMISSION_REQUEST_CODE = 88888
    }
}