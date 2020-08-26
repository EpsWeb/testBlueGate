import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:permission_handler/permission_handler.dart';
import 'package:http/http.dart' as http;

import 'dart:io';

void main() {
  runApp(MyApp(Permission.location));
  String str = 'Hi';
  str.selfPrint();
  print(5.increase());
}

extension on String {
  selfPrint() {
    print('Self' + this);
  }
}

extension<T extends num> on T {
  T increase() => this + 1;
}

class MyApp extends StatelessWidget {
  const MyApp(this._permission);

  final Permission _permission;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState(Permission.location);
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform_battery =
      const MethodChannel('samples.flutter.dev/battery');
  static const platform_scanner =
      const MethodChannel('samples.flutter.dev/scanner');

  _MyHomePageState(this._permission);

  final Permission _permission;
  PermissionStatus _permissionStatus = PermissionStatus.undetermined;

  @override
  void initState() {
    super.initState();

    _listenForPermissionStatus();
  }

  void _listenForPermissionStatus() async {
    final status = await _permission.status;
    setState(() => _permissionStatus = status);
  }

  // Get battery level.
  String _batteryLevel = 'Unknown battery level.';
  bool isStartSucceeded = false;
  bool isStopSucceeded = false;
  bool isStarted = false;
  bool isStopped = false;

  PermissionStatus _status;

  Future<PermissionStatus> requestPermission(Permission permission) async {
    final PermissionStatus status = await permission.request();

    setState(() {
//      print(status);
      _permissionStatus = status;
//      print(_permissionStatus);
    });
    return status;
  }

  Future<void> _startScanning() async {
    final PermissionStatus status =
        await requestPermission(Permission.location);
//    print('status permission: ' + status.toString());
    if (status == PermissionStatus.granted) {
      try {
        await platform_scanner.invokeMethod('startScanning');
        setState(() {
          isStarted = true;
          isStartSucceeded = true;
        });
      } on PlatformException catch (e) {
        setState(() {
          isStarted = true;
          isStartSucceeded = false;
        });
      }
    } else {
//      print('Permission status is not granted');
    }
  }

  Future<void> _stopScanning() async {
    try {
      await platform_scanner.invokeMethod('stopScanning');
      setState(() {
        isStopped = true;
        isStopSucceeded = true;
      });
    } on PlatformException catch (e) {
      setState(() {
        isStopped = true;
        isStopSucceeded = false;
      });
    }
  }

//  extension on String {
//
//}

  Future<void> _getBatteryLevel() async {
    loadData();
    String batteryLevel;
    try {
      final int result = await platform_battery.invokeMethod('getBatteryLevel');
      batteryLevel = 'Battery level at $result % .';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }

    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

  loadData() async {
    String dataURL =
        "https://dev.pal-es.com/api/v1/bt/address/autocomplete?keywords=israel";
    http.Response response = await http.get(dataURL,
        headers: {"x-bt-user-token": "GDN5-F8KG5-GNYSD45-KGBXRW843-SDFN4"});
    final String body = json.decode(response.body).toString();
    print('Body: ' + body);
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            IconButton(
              icon: const Icon(Icons.settings),
              onPressed: () async {
                var hasOpened = openAppSettings();
                debugPrint('App Settings opened: ' + hasOpened.toString());
              },
            ),
            RaisedButton(
              child: Text('Get Battery Level'),
              onPressed: _getBatteryLevel,
            ),
            Text(_batteryLevel),
            RaisedButton(
              child: Text('Start scanning'),
              onPressed: _startScanning,
            ),
            isStarted
                ? (isStartSucceeded
                    ? Text('Started successfully')
                    : Text('Start failed'))
                : Container(),
            RaisedButton(
              child: Text('Stop scanning'),
              onPressed: _stopScanning,
            ),
            isStopped
                ? (isStopSucceeded
                    ? Text('Stopped successfully')
                    : Text('Stop failed'))
                : Container(),
            ListTile(
              title: Text(_permission.toString()),
              subtitle: Text(
                _permissionStatus.toString(),
                style: TextStyle(color: getPermissionColor()),
              ),
//              onTap: () {
//                requestPermission(_permission);
//              },
            )
          ],
        ),
      ),
    );
  }

  Color getPermissionColor() {
    switch (_permissionStatus) {
      case PermissionStatus.denied:
        return Colors.red;
      case PermissionStatus.granted:
        return Colors.green;
      default:
        return Colors.grey;
    }
  }
}
