import 'package:flutter_test/flutter_test.dart';
import 'package:csn_printer_sdk/csn_printer_sdk.dart';
import 'package:csn_printer_sdk/csn_printer_sdk_platform_interface.dart';
import 'package:csn_printer_sdk/csn_printer_sdk_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockCsnPrinterSdkPlatform
    with MockPlatformInterfaceMixin
    implements CsnPrinterSdkPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final CsnPrinterSdkPlatform initialPlatform = CsnPrinterSdkPlatform.instance;

  test('$MethodChannelCsnPrinterSdk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelCsnPrinterSdk>());
  });

  test('getPlatformVersion', () async {
    CsnPrinterSdk csnPrinterSdkPlugin = CsnPrinterSdk();
    MockCsnPrinterSdkPlatform fakePlatform = MockCsnPrinterSdkPlatform();
    CsnPrinterSdkPlatform.instance = fakePlatform;

    expect(await csnPrinterSdkPlugin.getPlatformVersion(), '42');
  });
}
