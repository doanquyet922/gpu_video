import 'package:flutter/material.dart';
import 'package:gpu_video_plugin/gpu_video.dart';

class Movie extends StatefulWidget {
  const Movie({Key? key}) : super(key: key);

  @override
  State<Movie> createState() => _MovieState();
}

class _MovieState extends State<Movie> {
  List<String> _listFilter = [];

  Future<void> initListFilter() async {
    List<String> listFilter = [];
    listFilter = await Filters.filters;

    setState(() {
      _listFilter = listFilter;
    });
  }

  @override
  void initState() {
    super.initState();
    initListFilter();
  }

  @override
  Widget build(BuildContext context) {
    String url =
        "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4";
    return Scaffold(
      body: Column(
        children: [
          SizedBox(
              height: 300,
              // width: 100,
              child: MovieView(url: url)),
          const Text("MOVIE"),
          Expanded(
              child: ListView.separated(
                  itemBuilder: (_, index) => InkWell(
                      onTap: () {
                        MovieView.setFilterMovie(_listFilter[index]);
                      },
                      child: Text(_listFilter[index])),
                  separatorBuilder: (_, index) => const SizedBox(
                        height: 20,
                      ),
                  itemCount: _listFilter.length))
        ],
      ),
    );
  }
}
