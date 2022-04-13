package com.example.gpu_video_plugin.gpuv.camerarecorder;

/**
 * Created by sudamasayuki on 2018/03/13.
 */

public interface CameraRecordListener {

    void onGetFlashSupport(boolean flashSupport);

    void onRecordComplete();

    void onRecordStart();

    void onError(Exception exception);

    void onCameraThreadFinish();

    /**
     * Is called when native codecs finish to write file.
     */
    void onVideoFileReady();
}
