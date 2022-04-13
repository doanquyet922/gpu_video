package com.example.gpu_video_plugin.widget

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.flutter.plugin.platform.PlatformView

internal class ImageVideoWidget(context: Context, id: Int, creationParams: Map<String?, Any?>?) : PlatformView {
    private val imageView: ImageView = ImageView(context)
    override fun getView(): View {
        return imageView
    }

    override fun dispose() {}

    init {
        val path: String = creationParams?.get(key = "path") as String
        Glide.with(context)
                .load(path)
                .into(imageView)
    }
}