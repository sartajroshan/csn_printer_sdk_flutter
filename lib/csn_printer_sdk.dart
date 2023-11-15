import 'package:csn_printer_sdk/csn_printer_sdk_platform_interface.dart';
import 'package:csn_printer_sdk/src/CSNPrinterModel.g.dart';
import 'package:csn_printer_sdk/src/models/ContentSize.dart';
import 'package:csn_printer_sdk/src/models/PaperWidth.dart';

class CsnPrinterSdk {
  Future<bool?> makePrinterReady({bool beeper = true, bool drawer = false, bool cutter = true, int count = 1, PaperWidth width = PaperWidth.width58, ContentSize contentSize = ContentSize.small}) {
    int paperWidth;
    switch(width) {
      case PaperWidth.width58:
        paperWidth = 384;
            break;
      case PaperWidth.width80:
        paperWidth = 576;
        break;
    }

    int content;
    switch(contentSize) {
      case ContentSize.small:
        content = 1;
        break;
      case ContentSize.medium:
        content = 2;
        break;
      case ContentSize.large:
        content = 3;
        break;
    }

    return CsnPrinterSdkPlatform.instance.makePrinterReady(
      beeper, drawer, cutter, count, paperWidth, content
    );
  }

  Future<bool?> disconnect() {
    return CsnPrinterSdkPlatform.instance.disconnect();
  }

  Future<PrintResult?> print(List<PrintInputData> argData) {
    return CsnPrinterSdkPlatform.instance.print(argData);
  }

}