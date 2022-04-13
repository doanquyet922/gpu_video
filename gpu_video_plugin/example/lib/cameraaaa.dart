import 'package:flutter/material.dart';
import 'package:gpu_video_plugin/gpu_video.dart';

class Cameraaaa extends StatefulWidget {
  const Cameraaaa({Key? key}) : super(key: key);

  @override
  State<Cameraaaa> createState() => _CameraaaaState();
}

class _CameraaaaState extends State<Cameraaaa> {
  List<String> _listFilter = [];
  var isRecord=false;
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
    return Scaffold(
      body: Column(
        children: [
          Expanded(
            child: Stack(
              children: [
                const Camera(
                  // height: 500,
                  // width: 300,
                ),
                Positioned.fill(
                    left: 10,
                    top: 50,
                    child: Visibility(
                      visible: !isRecord,
                      child: ListView.separated(
                          separatorBuilder: (_, index) =>
                          const SizedBox(
                            height: 5,
                          ),
                          itemCount: _listFilter.length,
                          itemBuilder: (context, index) =>
                              InkWell(
                                onTap: () {
                                  print("setFilter");
                                  Camera.setFilter(_listFilter[index].toString());
                                },
                                child: Text(
                                  _listFilter[index].toString(),
                                  style: const TextStyle(color: Colors.red),
                                ),
                              )),
                    )),
                Positioned(
                  right: 10,
                  top: 50,
                  child: Column(
                    children: [
                      ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          primary: Colors.blue,
                        ),
                        onPressed: () {
                          Camera.switchCamera();
                        },
                        child: const Text(
                          "SWITCH",
                          style: TextStyle(
                            color: Colors.white,
                          ),
                        ),
                      ),
                      ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          primary: Colors.blue,
                        ),
                        onPressed: () {
                          Camera.flash();
                        },
                        child: const Text(
                          "FLASH",
                          style: TextStyle(
                            color: Colors.white,
                          ),
                        ),
                      ),
                      ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          primary: Colors.blue,
                        ),
                        onPressed: () {
                          Camera.imageCapture();
                        },
                        child: const Text(
                          "IMAGE CAPTURE",
                          style: TextStyle(
                            color: Colors.white,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              primary: Colors.blue,
            ),
            onPressed: () {
              if(!isRecord){
                Camera.startRecord();
              }
              else{
                Camera.stopRecord();
              }
              setState(() {
                isRecord=!isRecord;
              });
            },
            child: Text(
              isRecord?"Stop Record":"Start Record",
              style: const TextStyle(
                color: Colors.white,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
