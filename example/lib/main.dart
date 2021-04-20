import 'package:flutter/material.dart';

import 'package:smartlock/smartlock.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String? id;
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: Column(
          children: [
            if (id != null) Text("Your id is $id"),
            ElevatedButton(
              child: Text('Click me'),
              onPressed: () {
                Smartlock.showHints().then((value) {
                  setState(() {
                    if (value != null) {
                      id = value;
                    }
                  });
                });
              },
            ),
          ],
        )),
      ),
    );
  }
}
