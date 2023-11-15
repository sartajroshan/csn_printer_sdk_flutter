import 'package:csn_printer_sdk/src/CSNPrinterModel.g.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'csn_printer_sdk_pigeon.dart';

abstract class CsnPrinterSdkPlatform extends PlatformInterface {
  /// Constructs a CsnPrinterSdkPlatform.
  CsnPrinterSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static CsnPrinterSdkPlatform _instance = CsnPrinterSdkPigeon();

  /// The default instance of [CsnPrinterSdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelCsnPrinterSdk].
  static CsnPrinterSdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [CsnPrinterSdkPlatform] when
  /// they register themselves.
  static set instance(CsnPrinterSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool?> makePrinterReady(bool beeper, bool drawer, bool cutter, int count, int width, int content) async {
    throw UnimplementedError('makePrinterReady() has not been implemented.');
  }

  Future<PrintResult?> print(List<PrintInputData?> argData) async {
    throw UnimplementedError('print() has not been implemented.');
  }
  Future<bool?> disconnect() async {
    throw UnimplementedError('disconnect() has not been implemented.');
  }
}
