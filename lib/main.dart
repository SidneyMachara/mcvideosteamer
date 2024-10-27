import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: StreamingScreen(),
    );
  }
}

class StreamingScreen extends StatefulWidget {
  @override
  _StreamingScreenState createState() => _StreamingScreenState();
}

class _StreamingScreenState extends State<StreamingScreen> {
  Future<void> requestPermissions() async {
    // Request camera permission
    var cameraStatus = await Permission.camera.request();
    if (cameraStatus.isGranted) {
      print("Camera permission granted");
    } else {
      print("Camera permission denied");
    }

    // Request microphone permission
    var microphoneStatus = await Permission.microphone.request();
    if (microphoneStatus.isGranted) {
      print("Microphone permission granted");
    } else {
      print("Microphone permission denied");
    }

    // Alternatively, you can request both at once
    // Map<Permission, PermissionStatus> statuses = await [
    //   Permission.camera,
    //   Permission.microphone,
    // ].request();

    // if (cameraStatus.isGranted && microphoneStatus.isGranted) {
    //   print("Both permissions granted");
    // } else {
    //   print("One or both permissions denied");
    // }
  }

  @override
  void initState() {
    super.initState();
    requestPermissions();
  }

  static const platform = MethodChannel('srt_streaming_channel');
  bool isStreaming = false;

  Future<void> startStream() async {
    try {
      var res = await platform.invokeMethod(
        'startStream',
        {
          "ip": "5.161.66.194",
          "port": 9101,
        },
      );

      setState(() {
        isStreaming = true;
      });
    } on PlatformException catch (e) {
      print("-------> '${e.message}'");
    }
  }

  Future<void> stopStream() async {
    try {
      await platform.invokeMethod('stopStream');
      setState(() {
        isStreaming = false;
      });
    } on PlatformException catch (e) {
      print("Failed to stop stream: '${e.message}'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('SRT Camera Streaming'),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          ElevatedButton(
            onPressed: isStreaming ? stopStream : startStream,
            child: Text(isStreaming ? 'Stop Streaming' : 'Start Streaming'),
          ),
        ],
      ),
    );
  }
}
