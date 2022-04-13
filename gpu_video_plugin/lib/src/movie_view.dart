import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MovieView extends StatefulWidget {
  const MovieView({Key? key, required this.url, this.width, this.height})
      : super(key: key);
  static const MethodChannel channel = MethodChannel('gpu_video_plugin');
  final String url;
  final double? height;
  final double? width;
  static setFilterMovie(String nameFilter)async{
    await channel.invokeMethod("set_filter_movie",nameFilter);
  }

  @override
  _MovieViewState createState() => _MovieViewState();
}

class _MovieViewState extends State<MovieView> {

  setUpView() async {
    await MovieView.channel.invokeMethod("set_up_movie_view", widget.url);
  }

  removeView() async {
    await MovieView.channel.invokeMethod("remove_view_movie");
  }

  @override
  void initState() {
    super.initState();
    setUpView();
  }

  @override
  void dispose() {
    super.dispose();
    removeView();
  }

  @override
  Widget build(BuildContext context) {
    String viewType = 'movie_view';
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
}
