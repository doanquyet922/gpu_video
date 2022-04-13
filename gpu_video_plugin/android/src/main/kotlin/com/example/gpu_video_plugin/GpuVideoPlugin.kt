package com.example.gpu_video_plugin

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.GLException
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gpu_video_plugin.gpuv.camerarecorder.CameraRecordListener
import com.example.gpu_video_plugin.gpuv.camerarecorder.GPUCameraRecorder
import com.example.gpu_video_plugin.gpuv.camerarecorder.GPUCameraRecorderBuilder
import com.example.gpu_video_plugin.gpuv.camerarecorder.LensFacing
import com.example.gpu_video_plugin.widget.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.IntBuffer
import java.text.SimpleDateFormat
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.opengles.GL10


/** GpuVideoPlugin */
class GpuVideoPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var mp4Compose: Mp4Compose
    private lateinit var call1: MethodCall
    private lateinit var result1: Result
    private lateinit var camera: Camera
    private lateinit var frameLayout: FrameLayout
    private lateinit var filepath: String
    private var sampleGLView: SampleCameraGLView? = null
    private var GPUCameraRecorder: GPUCameraRecorder? = null
    private lateinit var flutterBinding: FlutterPlugin.FlutterPluginBinding
    private var lensFacing = LensFacing.BACK
    private var toggleClick = false

    //    private var movieWrapperView: MovieWrapperView? = null
    private var moviePreview: MoviePreview? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        flutterBinding = flutterPluginBinding
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "gpu_video_plugin")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        flutterPluginBinding
                .platformViewRegistry
                .registerViewFactory("image-video-file", ImageVideoFactory())

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "check_permissions_WRITE_EXTERNAL_STORAGE") {
            if (checkPermission_WRITE_EXTERNAL_STORAGE()) result.success(true)
            else result.success(false)
        } else if (call.method == "videoLoaders") {
            if (checkPermission_WRITE_EXTERNAL_STORAGE()) mp4Compose.videoLoaders(call, result)
            else {
                call1 = call
                result1 = result
            }
        } else if (call.method == "filters") {
            filters(call, result)
        } else if (call.method == "startCodec") {
            mp4Compose.startCodec(call, result, channel)
        } else if (call.method == "playMovie") {
            mp4Compose.playMovie(call, result)
        } else if (call.method == "setUpCamera") {
            if (checkPermission_CAMERA()) setUpCamera(call, result)
            else {
                call1 = call
                result1 = result
            }
        } else if (call.method == "releaseCamera") {
            releaseCamera()
            result.success("ok")
        } else if (call.method == "setFilter") {
            setFilter(call, result)
        } else if (call.method == "switchCamera") {
            switchCamera()
            result.success("ok")
        } else if (call.method == "flash") {
            flash()
            result.success("ok")
        } else if (call.method == "imageCapture") {
            imageCapture()
            result.success("ok")
        } else if (call.method == "startRecord") {
            startRecord()
            result.success("ok")
        } else if (call.method == "stopRecord") {
            stopRecord()
            result.success("ok")
        } else if (call.method == "set_up_movie_view") {
            moviePreview = null
            moviePreview = MoviePreview(context)
            val movieWrapperView = moviePreview?.setUpMovieView(call, result)
            if (movieWrapperView != null) {
                flutterBinding
                        .platformViewRegistry
                        .registerViewFactory("movie_view", MovieViewFactory(movieWrapperView))
            }
            result.success(1)

        } else if (call.method == "remove_view_movie") {
            moviePreview?.removeView()
            result.success("ok")
        }
        else if (call.method == "set_filter_movie") {
            moviePreview?.setFilterMovie(call, result)
        }
        else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        mp4Compose = Mp4Compose(context, activity)
        binding.addRequestPermissionsResultListener(object : PluginRegistry.RequestPermissionsResultListener {
            override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {
                Log.d("BBB", "addRequestPermissionsResultListener")
                when (requestCode) {
                    Mp4Compose.PERMISSION_REQUEST_CODE -> if (grantResults!!.size > 0 && grantResults!![0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "permission has been grunted.", Toast.LENGTH_SHORT).show()
                        if (call1.method == "videoLoaders") {
                            mp4Compose.videoLoaders(call1, result1)
                        }
//                        channel.invokeMethod("requested_WRITE_EXTERNAL_STORAGE", true)

                    } else {
                        Toast.makeText(context, "[WARN] permission is not grunted.", Toast.LENGTH_SHORT).show()
//                        channel.invokeMethod("requested_WRITE_EXTERNAL_STORAGE", false)
                        result1.notImplemented()
                    }
                    Camera.PERMISSION_REQUEST_CODE -> if (grantResults!!.size > 0 && grantResults!![0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "permission has been grunted.", Toast.LENGTH_SHORT).show()
                        if (call1.method == "setUpCamera") {
                            setUpCamera(call1, result1)
                        }
//                        channel.invokeMethod("requested_WRITE_EXTERNAL_STORAGE", true)

                    } else {
                        Toast.makeText(context, "[WARN] permission is not grunted.", Toast.LENGTH_SHORT).show()
//                        channel.invokeMethod("requested_WRITE_EXTERNAL_STORAGE", false)
                        result1.notImplemented()
                    }
                }
                return false;
            }

        })

    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }

    private fun startRecord() {
        filepath = getVideoFilePath()
        GPUCameraRecorder!!.start(filepath)
    }

    private fun stopRecord() {
        GPUCameraRecorder!!.stop()
    }

    private fun createBitmapFromGLSurface(w: Int, h: Int, gl: GL10): Bitmap? {
        val bitmapBuffer = IntArray(w * h)
        val bitmapSource = IntArray(w * h)
        val intBuffer = IntBuffer.wrap(bitmapBuffer)
        intBuffer.position(0)
        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer)
            var offset1: Int
            var offset2: Int
            var texturePixel: Int
            var blue: Int
            var red: Int
            var pixel: Int
            for (i in 0 until h) {
                offset1 = i * w
                offset2 = (h - i - 1) * w
                for (j in 0 until w) {
                    texturePixel = bitmapBuffer[offset1 + j]
                    blue = texturePixel shr 16 and 0xff
                    red = texturePixel shl 16 and 0x00ff0000
                    pixel = texturePixel and -0xff0100 or red or blue
                    bitmapSource[offset2 + j] = pixel
                }
            }
        } catch (e: GLException) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.message, e)
            return null
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)
    }

    private interface BitmapReadyCallbacks {
        fun onBitmapReady(bitmap: Bitmap?)
    }

    private fun captureBitmap(bitmapReadyCallbacks: BitmapReadyCallbacks) {
        sampleGLView!!.queueEvent {
            val egl = EGLContext.getEGL() as EGL10
            val gl = egl.eglGetCurrentContext().gl as GL10
            val snapshotBitmap: Bitmap? = createBitmapFromGLSurface(sampleGLView!!.measuredWidth, sampleGLView!!.measuredHeight, gl)
            activity.runOnUiThread(Runnable { bitmapReadyCallbacks.onBitmapReady(snapshotBitmap) })
        }
    }

    fun saveAsPngImage(bitmap: Bitmap, filePath: String?) {
        try {
            val file = File(filePath)
            val outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
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

    private fun getVideoFilePath(): String {
        return getAndroidMoviesFolder().absolutePath + "/" + SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date()) + "GPUCameraRecorder.mp4"
    }

    private fun getAndroidMoviesFolder(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
    }

    private fun exportPngToGallery(context: Context, filePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(filePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    fun getImageFilePath(): String {
        return getAndroidImageFolder().absolutePath + "/" + SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date()) + "GPUCameraRecorder.png"
    }

    private fun getAndroidImageFolder(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    }

    private fun imageCapture() {
        captureBitmap(object : BitmapReadyCallbacks {
            override fun onBitmapReady(bitmap: Bitmap?) {
                Handler().post {
                    val imagePath = getImageFilePath()
                    saveAsPngImage(bitmap!!, imagePath)
                    exportPngToGallery(context, imagePath)
                }
            }
        })
    }

    private fun flash() {
        if (GPUCameraRecorder != null && GPUCameraRecorder!!.isFlashSupport) {
            GPUCameraRecorder!!.switchFlashMode()
            GPUCameraRecorder!!.changeAutoFocus()
        }
    }

    //    private void changeFilter(Filters filters) {
    //        GPUCameraRecorder.setFilter(Filters.getFilterInstance(filters, getApplicationContext()));
    //    }

    private fun switchCamera() {
        releaseCamera()
        lensFacing = if (lensFacing == LensFacing.BACK) {
            LensFacing.FRONT
        } else {
            LensFacing.BACK
        }
        toggleClick = true
    }

    private fun setFilter(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        val nameGlFilter: String = call.arguments as String
        val filterType: FilterType = getFilter(nameGlFilter)
        if (GPUCameraRecorder != null) {
            GPUCameraRecorder!!.setFilter(FilterType.createGlFilter(filterType, context))
        }
        result.success("ok")
    }

    private fun getFilter(nameFilter: String): FilterType {
        val filters = FilterType.values()
        var filterType: FilterType = filters[2]
        for (i in filters) {
            if (i.name == nameFilter) {
                filterType = i
            }
        }
        return filterType
    }

    private fun releaseCamera() {
        Log.d("BBB", "releaseCamera")
        flutterBinding
                .platformViewRegistry
                .registerViewFactory("gpu_video_plugin_camera", null)
        if (sampleGLView != null) {
            sampleGLView!!.onPause()
        }
        if (GPUCameraRecorder != null) {
            GPUCameraRecorder!!.stop()
            GPUCameraRecorder!!.release()
            GPUCameraRecorder = null
        }
        if (sampleGLView != null) {
            frameLayout.removeView(sampleGLView)
            sampleGLView = null
        }
    }

    private fun filters(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        val filters = FilterType.values()
        val list: ArrayList<String> = ArrayList()
        for (i in filters) {
            list.add(i.name)
        }
        result.success(list)
    }


    private fun setUpCameraView() {
        activity.runOnUiThread(Runnable {
            frameLayout = FrameLayout(context)
            frameLayout.removeAllViews()
            sampleGLView = null
            sampleGLView = SampleCameraGLView(context)
            sampleGLView!!.setTouchListener { event: MotionEvent, width: Int, height: Int ->
                if (GPUCameraRecorder == null) return@setTouchListener
                GPUCameraRecorder!!.changeManualFocusPoint(event.x, event.y, width, height)
            }
            frameLayout.addView(sampleGLView)
        })
    }

    private fun setUpCamera(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        call1 = call
        result1 = result
        val data: HashMap<String, Any?> = call.arguments as HashMap<String, Any?>
        val cameraHeight: Int = data["cameraHeight"] as Int
        val cameraWidth: Int = data["cameraWidth"] as Int
        sampleGLView = null
        sampleGLView = SampleCameraGLView(context)
//        camera = Camera(context, activity, sampleGLView!!, GPUCameraRecorder)
        setUpCameraView()
        flutterBinding
                .platformViewRegistry
                .registerViewFactory("gpu_video_plugin_camera", CameraFactory(frameLayout))
////        result.success(idCamera)
//        Log.d("BBB","idCamera:"+idCamera)
//        idCamera++
        GPUCameraRecorder = GPUCameraRecorderBuilder(activity, sampleGLView) //.recordNoFilter(true)
                .cameraRecordListener(object : CameraRecordListener {
                    override fun onGetFlashSupport(flashSupport: Boolean) {
                        activity.runOnUiThread(Runnable {
//                            findViewById<View>(R.id.btn_flash).setEnabled(flashSupport)
                        })
                    }

                    override fun onRecordComplete() {
                        exportMp4ToGallery(context, filepath)
                    }

                    override fun onRecordStart() {
                        activity.runOnUiThread(Runnable {
//                            lv.setVisibility(View.GONE)
                        })
                    }

                    override fun onError(exception: Exception) {
                        Log.e("GPUCameraRecorder", exception.toString())
                    }

                    override fun onCameraThreadFinish() {
                        if (toggleClick) {
                            activity.runOnUiThread(Runnable {
                                setUpCamera(call1, result1)
                            })
                        }
                        toggleClick = false;
                    }

                    override fun onVideoFileReady() {}
                })
                .videoSize(cameraWidth, cameraHeight)
                .cameraSize(cameraWidth, cameraHeight)
                .lensFacing(lensFacing)
                .build()

    }

    private fun checkPermission_WRITE_EXTERNAL_STORAGE(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        // request permission if it has not been grunted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Mp4Compose.PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    private fun checkPermission_CAMERA(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        // request permission if it has not been grunted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), Camera.PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }
}
