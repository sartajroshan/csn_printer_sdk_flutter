package csn.printer_sdk.csn_printer_sdk

import CSNPrinterApi
import PrintInputData
import PrintResult
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


/** CsnPrinterSdkPlugin */
class CsnPrinterSdkPlugin: FlutterPlugin, CSNPrinterApi, ActivityAware  {

  private var connectUSB: ConnectUSB? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    connectUSB = ConnectUSB()
    CSNPrinterApi.setUp(flutterPluginBinding.binaryMessenger, this)

  }


  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    if (connectUSB == null) {
      return
    }
    CSNPrinterApi.setUp(binding.binaryMessenger, null)
    connectUSB = null
  }

  override fun makePrinterReady(
    beeper: Boolean,
    drawer: Boolean,
    cutter: Boolean,
    count: Long,
    width: Long,
    content: Long,
    callback: (Result<Boolean?>) -> Unit
  ) {
    connectUSB!!.readyForPrint(width.toInt(), cutter, drawer, beeper, count.toInt(), content.toInt(), 0) {
      callback(Result.success(it))
    }

  }

  override fun print(data: List<PrintInputData>, callback: (Result<PrintResult?>) -> Unit) {
    if(connectUSB != null) {
      connectUSB!!.setPrintData(data)
      connectUSB!!.print {
        callback(Result.success(it))
      }
    }
  }

  override fun disconnect(callback: (Result<Boolean?>) -> Unit) {
    if(connectUSB != null) {
      connectUSB!!.disconnect {
        callback(Result.success(it))
      }
    }
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    if (connectUSB == null) {
      return
    }
    connectUSB!!.setActivity(binding.getActivity())
  }

  override fun onDetachedFromActivity() {
    if (connectUSB == null) {
      return
    }
    connectUSB!!.setActivity(null)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }


}
