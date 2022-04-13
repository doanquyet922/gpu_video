import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class VideoLoader {
  static const MethodChannel _channel = MethodChannel('gpu_video_plugin');

  static void playMovie(String path) async {
    await _channel.invokeMethod("playMovie", path);
  }

  static Future<List<VideoItem>> get videosLoaders async {
    List<VideoItem> list = [];
    final maps = await _channel.invokeMethod("videoLoaders");
    if (maps != null) {
      maps.forEach((element) {
        String path = element["path"];
        int duration = element["duration"];
        int height = element["height"];
        int width = element["width"];
        list.add(VideoItem(
            path: path, duration: duration, width: width, height: height));
      });
    }
    // int tmp = -1;
    // bool check =await _channel
    //     .invokeMethod("check_permissions_WRITE_EXTERNAL_STORAGE") as bool;
    // if (check) {
    //
    // }
    // else{
    //   await compute();
    //   _channel.setMethodCallHandler((MethodCall call) async {
    //     if (call.method == "requested_WRITE_EXTERNAL_STORAGE" &&
    //         call.arguments as bool == true) {
    //       print("setMethodCallHandler true");
    //       final maps = await _channel.invokeMethod("videoLoaders");
    //       if (maps != null) {
    //         maps.forEach((element) {
    //           String path = element["path"];
    //           int duration = element["duration"];
    //           int height = element["height"];
    //           int width = element["width"];
    //           list.add(VideoItem(
    //               path: path, duration: duration, width: width, height: height));
    //         });
    //         tmp = 0;
    //       }
    //     } else {
    //       print("setMethodCallHandler true");
    //       tmp = 1;
    //     }
    //   });
     //  int x = 0;
     //  print("vao 1");
     // Future.
     //  print("vao 3");
    // }


    return list;
  }

  // Future<void> pau() async {
  //   _channel.setMethodCallHandler((MethodCall call) async {
  //     if (call.method == "requested_WRITE_EXTERNAL_STORAGE" &&
  //         call.arguments as bool == true) {
  //       print("setMethodCallHandler true");
  //       final maps = await _channel.invokeMethod("videoLoaders");
  //       if (maps != null) {
  //         maps.forEach((element) {
  //           String path = element["path"];
  //           int duration = element["duration"];
  //           int height = element["height"];
  //           int width = element["width"];
  //           list.add(VideoItem(
  //               path: path, duration: duration, width: width, height: height));
  //         });
  //       }
  //     } else {
  //       print("setMethodCallHandler true");
  //       tmp = 1;
  //     }
  //   });
  // }
}

class VideoItem {
  final String path;
  final int duration;
  final int width;
  final int height;

  VideoItem(
      {required this.path,
      required this.duration,
      required this.width,
      required this.height});

  @override
  String toString() {
    return 'VideoItem{path: $path}';
  }

//
//   String getPath() {
//     return path;
//   }
//
//   int getDuration() {
//     return duration;
//   }
//
//   int getWidth() {
//     return width;
//   }
//
//   int getHeight() {
//     return height;
//   }
//
//
//   @Override
//   int describeContents() {
//     return 0;
//   }
//
//   @Override
//   void writeToParcel(Parcel dest, int flags) {
//     dest.writeString(this.path);
//     dest.writeInt(this.duration);
//     dest.writeInt(this.width);
//     dest.writeInt(this.height);
//   }
//
//   protected VideoItem
//
//   (
//
//   Parcel
//
//   in) {
//   this.path = in.readString();
//   this.duration = in.readInt();
//   this.width = in.readInt();
//   this.height = in.readInt();
//   }
//
//   static final Creator<VideoItem> CREATOR = new Creator<VideoItem>()
//
//   {
//
//   @Override
//   VideoItem createFromParcel(Parcel source) {
//     return new VideoItem(source);
//   }
//
//   @Override
//   VideoItem
//
//   [
//
//   ]
//
//   newArray(int size) {
//     return new VideoItem[size];
//   }
// };
}
