package com.example.gpu_video_plugin;

import com.example.gpu_video_plugin.gpuv.egl.filter.GlFilter;

public interface FilterAdjuster {
    public void adjust(GlFilter filter, int percentage);
}
