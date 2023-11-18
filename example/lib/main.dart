import 'package:csn_printer_sdk/csn_printer_sdk.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String status = 'Unknown';
  final _csnPrinterSdkPlugin = CsnPrinterSdkPlugin();

  @override
  void initState() {
    super.initState();
  }

  Future<void> startPrinting() async {
    try {
         final  isReady = await _csnPrinterSdkPlugin.makePrinterReady();
         setState(() {
           status = isReady == true ? 'Printer Ready' : 'Printer Not ready';
         });

         if(isReady ?? false) {
          final result = await _csnPrinterSdkPlugin.print([
            PrintInputData(
                dataType: PrintInputDataType.alignment,
              align: 1
            ),
             PrintInputData(
               dataType: PrintInputDataType.text,
               inputText: PrintInputText(pszString: "Invoice Copy\r\n", nLan: 0, nOrgx: 96, nWidthTimes: 1, nHeightTimes: 1, nFontType: 0, nFontStyle: 0)
             ),
             PrintInputData(
                 dataType: PrintInputDataType.feedLine,
             ),
            PrintInputData(
                dataType: PrintInputDataType.alignment,
                align: 0
            ),
             PrintInputData(
                 dataType: PrintInputDataType.text,
                 inputText: PrintInputText(pszString: "Receipt: 270500027719 Cashier: 010121212122121", nLan: 0, nOrgx: 0, nWidthTimes: 0, nHeightTimes: 0, nFontType: 0, nFontStyle: 0)
             ),
            PrintInputData(
              dataType: PrintInputDataType.feedLine,
            ),
             PrintInputData(
                 dataType: PrintInputDataType.text,
                 inputText: PrintInputText(pszString: "----------------------------------------------", nLan: 0, nOrgx: 0, nWidthTimes: 0, nHeightTimes: 0, nFontType: 0, nFontStyle: 0)
             ),
            PrintInputData(
              dataType: PrintInputDataType.feedLine,
            ),
             PrintInputData(
                 dataType: PrintInputDataType.qrCode,
                 inputQrCode: PrintInputQrCode(strCodedata: "https://google.com", nWidthX: 10, nVersion: 1, nErrorCorrectionLevel: 1),
             ),
            PrintInputData(
              dataType: PrintInputDataType.feedLine,
            ),
           ]);

          setState(() {
            status = result?.state == PrintState.success ? 'Successfully printed' : '${result?.message}';
          });
         }

    } on PlatformException {
      setState(() {
        status = 'platform exc';
      });
    }

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
              Text(status),
              TextButton(onPressed: (){
                startPrinting();
              }, child: const Text("Print")),
            ],
          ),
        ),
      ),
    );
  }
}
