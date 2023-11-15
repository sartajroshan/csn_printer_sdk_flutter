import 'package:csn_printer_sdk/src/CSNPrinterModel.g.dart';

import 'csn_printer_sdk_platform_interface.dart';

class CsnPrinterSdkPigeon extends CsnPrinterSdkPlatform {
  final CSNPrinterApi _api = CSNPrinterApi();


  @override
  Future<bool?> makePrinterReady(bool beeper, bool drawer, bool cutter,
      int count, int width, int content) {
    return _api.makePrinterReady(beeper, drawer, cutter, count, width, content);
  }

  @override
  Future<bool?> disconnect() {
    return _api.disconnect();
  }

  @override
  Future<PrintResult?> print(List<PrintInputData?> argData) {
    return _api.print(argData);
  }

}
