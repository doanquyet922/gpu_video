import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Camera extends StatefulWidget {
  const Camera({Key? key, this.width, this.height}) : super(key: key);
  final double? height;
  final double? width;
  static const MethodChannel _channel = MethodChannel('gpu_video_plugin');

  static setFilter(String nameGlFilter) async {
    await _channel.invokeMethod("setFilter", nameGlFilter);
  }

  static switchCamera() async {
    await _channel.invokeMethod("switchCamera");
  }

  static flash() async {
    await _channel.invokeMethod("flash");
  }

  static imageCapture() async {
    await _channel.invokeMethod("imageCapture");
  }

  static startRecord() async {
    await _channel.invokeMethod("startRecord");
  }

  static stopRecord() async {
    await _channel.invokeMethod("stopRecord");
  }

  @override
  State<Camera> createState() => _CameraState();
}

class _CameraState extends State<Camera> {
  // final MethodChannel _channel = const MethodChannel('gpu_video_plugin');

  @override
  void initState() {
    // setUp();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    String viewType = 'gpu_video_plugin_camera';
    setUp();
    Map<String, dynamic> creationParams = <String, dynamic>{};
    return SizedBox(
      width: widget.width,
      height: widget.height,
      child: AndroidView(
        viewType: viewType,
        layoutDirection: TextDirection.ltr,
        creationParams: creationParams,
        creationParamsCodec: const StandardMessageCodec(),
      ),
    );
  }

  setUp() {
    // int _id=0;
    final Map<String, dynamic> map = <String, dynamic>{};
    map["cameraHeight"] =
        (widget.height ?? MediaQuery.of(context).size.height).toInt();
    map["cameraWidth"] =
        (widget.width ?? MediaQuery.of(context).size.width).toInt();
    Camera._channel.invokeMethod("setUpCamera", map);
    // int.tryParse(await _channel.invokeMethod("setUpCamera", map)) ?? 0;
    // if (!mounted) return;
    // setState(() {
    //   id=_id;
    // });
  }

  @override
  void dispose() async {
    super.dispose();
    await Camera._channel.invokeMethod("releaseCamera");
  }
}
