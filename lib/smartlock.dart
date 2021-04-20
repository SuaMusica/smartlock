import 'dart:async';

import 'package:flutter/services.dart';

class Smartlock {
  static const SHOW_HINTS = 'showHints';
  static const CHANNEL_NAME = 'br.com.suamusica.smartlock';
  static const MethodChannel _channel = const MethodChannel(CHANNEL_NAME);

  static Future<void> showHints() async {
    final bool response = await _channel.invokeMethod(SHOW_HINTS);
  }
}
