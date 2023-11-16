import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(PigeonOptions(
  dartOut: 'lib/src/CSNPrinterModel.g.dart',
  dartOptions: DartOptions(),
  kotlinOut:
  'android/src/main/kotlin/csn/printer_sdk/csn_printer_sdk/CSNPrinterModel.g.kt',
  kotlinOptions: KotlinOptions(),
  //copyrightHeader: 'pigeon/copyright.txt',
  dartPackageName: 'csn_printer_sdk',
))
enum PrintState { success, error }
enum PrintInputDataType { text, qrCode, feedLine }

class PrintResult {
  final PrintState state;
  final String message;
  PrintResult(this.state, this.message);
}

class PrintInputData {
  PrintInputDataType dataType;
  PrintInputText? inputText;
  PrintInputQrCode? inputQrCode;

  PrintInputData(this.dataType, this.inputText, this.inputQrCode);
}

class PrintInputText  {
  String pszString;
  int nLan; int nOrgx; int  nWidthTimes; int nHeightTimes; int nFontType;  int nFontStyle;

  PrintInputText(
      {required this.pszString,
      this.nLan = 0,
      this.nOrgx = 0,
      this.nWidthTimes = 0,
      this.nHeightTimes = 0,
      this.nFontType = 0,
      this.nFontStyle = 0});
}


class PrintInputQrCode {
  String strCodedata;
  int nWidthX; int nVersion; int nErrorCorrectionLevel;

  PrintInputQrCode(
      {required this.strCodedata,
      this.nWidthX = 10,
      this.nVersion = 1,
      this.nErrorCorrectionLevel = 1});
}

@HostApi()
abstract class CSNPrinterApi {
  @async
  bool? makePrinterReady(bool beeper, bool drawer, bool cutter, int count, int width, int content);

  @async
  PrintResult? print(List<PrintInputData> data);

  @async
  bool? disconnect();
}