import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:csn_printer_sdk/csn_printer_sdk_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelCsnPrinterSdk platform = MethodChannelCsnPrinterSdk();
  const MethodChannel channel = MethodChannel('csn_printer_sdk');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
