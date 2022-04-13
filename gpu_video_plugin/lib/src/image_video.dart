import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
class ImageVideo extends StatelessWidget {
  const ImageVideo({Key? key,this.width,this.height,required this.path}) : super(key: key);
  final double? height;
  final double? width;
  final String path;
  @override
  Widget build(BuildContext context) {
    const String viewType = 'image-video-file';
    Map<String, dynamic> creationParams = <String, dynamic>{};
    creationParams['path']=path;
    return SizedBox(
      height: height,
      width: width,
      child:  AndroidView(
      viewType: viewType,
      layoutDirection: TextDirection.ltr,
      creationParams: creationParams,
      creationParamsCodec: const StandardMessageCodec(),
    )
      // PlatformViewLink(
      //   viewType: viewType,
      //   surfaceFactory:
      //       (BuildContext context, PlatformViewController controller) {
      //     return AndroidViewSurface(
      //       controller: controller as AndroidViewController,
      //       gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
      //       hitTestBehavior: PlatformViewHitTestBehavior.opaque,
      //     );
      //   },
      //   onCreatePlatformView: (PlatformViewCreationParams params) {
      //     return PlatformViewsService.initSurfaceAndroidView(
      //       id: params.id,
      //       viewType: viewType,
      //       layoutDirection: TextDirection.ltr,
      //       creationParams: creationParams,
      //       creationParamsCodec: const StandardMessageCodec(),
      //       onFocus: () {
      //         params.onFocusChanged(true);
      //       },
      //     )
      //       ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
      //       ..create();
      //   },
      // ),
    );
  }
}
