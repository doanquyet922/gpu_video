import 'package:flutter/material.dart';
import 'package:gpu_video_plugin/gpu_video.dart';
import 'package:gpu_video_plugin_example/cameraaaa.dart';

import 'movie.dart';

void main() {
  runApp(const MaterialApp(
    home: MyApp(),
  ));
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with TickerProviderStateMixin {
  List<VideoItem> _list = [];
  List<String> _listFilter = [];
  String dropdownValue = "";
  int indexVideo = -1;
  double valueProcessFilter = 0;
  var isMute = false;
  var isFlipHorizontal = false;
  var isFlipVertical = false;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    loadListVideo();
  }

  Future<void> initPlatformState() async {
    List<String> listFilter = [];
    listFilter = await Filters.filters;

    setState(() {
      _listFilter = listFilter;
      dropdownValue = _listFilter[0];
    });
  }

  void loadListVideo() async {
    List<VideoItem> list = [];
    list = await VideoLoader.videosLoaders;
    if (!mounted) return;
    setState(() {
      _list = list;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Column(
        children: [
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Row(
              children: [
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    primary: indexVideo == -1 ? Colors.grey : Colors.blue,
                  ),
                  onPressed: () {
                    if (indexVideo == -1) return;
                    Filters.startCodec(indexVideo, dropdownValue, isMute,
                        isFlipHorizontal, isFlipVertical);
                    indexVideo = -1;
                  },
                  child: Text(
                    "startCodec",
                    style: TextStyle(
                      color: indexVideo == -1 ? Colors.black : Colors.white,
                    ),
                  ),
                ),
                const SizedBox(
                  width: 20,
                ),
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    primary: indexVideo == -1 ? Colors.grey : Colors.blue,
                  ),
                  onPressed: () {
                    if (indexVideo == -1) return;
                    VideoLoader.playMovie(_list[indexVideo].path);
                    indexVideo = -1;
                  },
                  child: Text(
                    "playMovie",
                    style: TextStyle(
                      color: indexVideo == -1 ? Colors.black : Colors.white,
                    ),
                  ),
                ),
                const SizedBox(
                  width: 20,
                ),
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    primary: Colors.blue,
                  ),
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => const Cameraaaa()),
                    );
                  },
                  child: const Text(
                    "Camera",
                    style: TextStyle(
                      color: Colors.white,
                    ),
                  ),
                ),
                const SizedBox(
                  width: 20,
                ),
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    primary: Colors.blue,
                  ),
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => const Movie()),
                    );
                  },
                  child: const Text(
                    "MOVIE PREVIEW",
                    style: TextStyle(
                      color: Colors.white,
                    ),
                  ),
                ),
              ],
            ),
          ),
          DropdownButtonHideUnderline(
              child: DropdownButton<String>(
            value: dropdownValue,
            icon: const Icon(Icons.arrow_downward),
            elevation: 16,
            style: const TextStyle(color: Colors.deepPurple),
            underline: Container(
              height: 2,
              color: Colors.deepPurpleAccent,
            ),
            onChanged: (String? newValue) {
              setState(() {
                dropdownValue = newValue!;
              });
            },
            items: _listFilter.map<DropdownMenuItem<String>>((String value) {
              return DropdownMenuItem<String>(
                value: value,
                child: Text(value),
              );
            }).toList(),
          )),
          Row(
            children: [
              Checkbox(
                  value: isMute,
                  onChanged: (val) {
                    setState(() {
                      isMute = val!;
                    });
                  }),
              const Text("Mute")
            ],
          ),
          Row(
            children: [
              Checkbox(
                  value: isFlipHorizontal,
                  onChanged: (val) {
                    setState(() {
                      isFlipHorizontal = val!;
                    });
                  }),
              const Text("FlipHorizontal")
            ],
          ),
          Row(
            children: [
              Checkbox(
                  value: isFlipVertical,
                  onChanged: (val) {
                    setState(() {
                      isFlipVertical = val!;
                    });
                  }),
              const Text("FlipVertical")
            ],
          ),
          Container(
            margin: const EdgeInsets.all(10),
            child: ProcessFilter(
              onProgress: (val) {
                setState(() {
                  valueProcessFilter = val;
                });
              },
              onCompleted: (val) {
                if (val == true) {
                  valueProcessFilter = 0.0;
                  loadListVideo();
                }
              },
              child: LinearProgressIndicator(
                value: valueProcessFilter,
                semanticsLabel: 'Linear progress indicator',
              ),
            ),
          ),
          Expanded(
            child: ListView.builder(
                itemCount: _list.length,
                itemBuilder: (context, index) => InkWell(
                      onTap: () {
                        setState(() {
                          indexVideo = index;
                        });
                      },
                      child: ColoredBox(
                        color:
                            (index == indexVideo) ? Colors.blue : Colors.white,
                        child: Row(
                          children: [
                            ImageVideo(
                              path: _list[index].path,
                              width: 50,
                              height: 50,
                            ),
                            Expanded(child: Text(_list[index].path))
                          ],
                        ),
                      ),
                    )),
          )
        ],
      ),
    );
  }
}
