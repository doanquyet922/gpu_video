import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class ProcessFilter extends StatefulWidget {
  const ProcessFilter(
      {Key? key,
      required this.onProgress,
      required this.child,
      this.onCompleted})
      : super(key: key);
  final ValueSetter<double> onProgress;
  final ValueSetter<bool>? onCompleted;
  final Widget child;

  @override
  _ProcessFilterState createState() => _ProcessFilterState();
}

class _ProcessFilterState extends State<ProcessFilter> {
  final MethodChannel _channel = const MethodChannel('gpu_video_plugin');
  double _processFilter = 0.0;

  Future<void> _fromNative(MethodCall call) async {
    if (call.method == 'processFilter') {
      _processFilter = double.tryParse(call.arguments.toString()) ?? 0;
      widget.onProgress(_processFilter);
      if (_processFilter == 1.0) {
        widget.onCompleted!(true);
      } else {
        widget.onCompleted!(false);
      }
      setState(() {});
      print('callTest result = ${call.arguments}');
    }
  }

  @override
  void initState() {
    super.initState();
    _channel.setMethodCallHandler(_fromNative);
  }

  @override
  Widget build(BuildContext context) {
    return widget.child;
  }
}

class Filters {
  final double _valueMaxProcessFilter = 1;

  double get valueMaxProcessFilter => _valueMaxProcessFilter;

  static const MethodChannel _channel = MethodChannel('gpu_video_plugin');

  static Future<List<String>> get filters async {
    final List<String> filters = [];
    var data = await _channel.invokeMethod('filters');
    for (var i in data) {
      filters.add(i.toString());
    }
    return filters;
  }

  static Future<void> startCodec(int videoItemIndex, String nameGlFilter,
      bool mute, bool flipHorizontal, bool flipVertical) async {
    print("startCodec dart");
    // _channel.setMethodCallHandler(null);

    Map<String, dynamic> map = <String, dynamic>{};
    map["videoItemIndex"] = videoItemIndex;
    map["nameGlFilter"] = nameGlFilter;
    map["mute"] = mute;
    map["flipHorizontal"] = flipHorizontal;
    map["flipVertical"] = flipVertical;
    await _channel.invokeMethod("startCodec", map);
    // final List<String> filters=[];
    // var data = await _channel.invokeMethod('filters');
    // for(var i in data){
    //   filters.add(i.toString());
    // }
    // return filters;
  }

// static Future<List<double>> get processFilter async {
//   // Map<String,dynamic> map=<String,dynamic>{};
//   // map["videoItemIndex"]=1;
//   // map["nameGlFilter"]="GAMMA";
//   // map["mute"]=false;
//   // map["flipHorizontal"]=true;
//   // map["flipVertical"]=false;
//   //
//   // await _channel.invokeMethod("startCodec",map) ;
//   final List<String> filters=[];
//   var data = await _channel.invokeMethod('filters');
//   for(var i in data){
//     filters.add(i.toString());
//   }
//   return filters;
// }
}
