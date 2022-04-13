package com.example.gpu_video_plugin.compose;

import java.util.List;

public interface VideoLoadListener {

    void onVideoLoaded(List<VideoItem> videoItems);

    void onFailed(Exception e);
}
