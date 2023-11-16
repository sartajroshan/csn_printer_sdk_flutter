package csn.printer_sdk.csn_printer_sdk

import PrintInputData
import PrintResult
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.widget.Toast
import com.csnprintersdk.csnio.CSNPOS
import com.csnprintersdk.csnio.CSNUSBPrinting
import com.csnprintersdk.csnio.csnbase.CSNIOCallBack
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ConnectUSB :
    CSNIOCallBack {
    private val es: ExecutorService = Executors.newScheduledThreadPool(30)
    private val mPos = CSNPOS()
    private val mUsb = CSNUSBPrinting()
    var mActivity: Activity? = null

    var nPrintWidth: Int = 384
    var bCutter: Boolean = true
    var bDrawer: Boolean = true
    var bBeeper: Boolean = true
    var nPrintCount: Int = 1
    var nPrintContent: Int = 1
    var nCompressMethod: Int = 0
    private var readyCallback: ((Boolean) -> Unit)? = null
    private var printCallback: ((PrintResult) -> Unit)? = null
    private var disconnectCallback: ((Boolean) -> Unit)? = null
    private  var  printData: List<PrintInputData>? = null


    fun setActivity(activity: Activity?) {
        this.mActivity = activity
    }

    fun setPrintData(data:List<PrintInputData>?) {
        this.printData = data
    }


    fun readyForPrint(
        nPrintWidth: Int,
        bCutter: Boolean,
        bDrawer: Boolean,
        bBeeper: Boolean,
        nPrintCount: Int,
        nPrintContent: Int,
        nCompressMethod: Int,
        callback: (Boolean) -> Unit) {
        this.nPrintWidth = nPrintWidth
        this.bCutter = bCutter
        this.bDrawer = bDrawer
        this.bBeeper = bBeeper
        this.nPrintCount = nPrintCount
        this.nPrintContent = nPrintContent
        this.nCompressMethod = nCompressMethod
        mPos.Set(mUsb)
        mUsb.SetCallBack(this)
        readyCallback = callback;
        probe()
    }


    fun print(callback: (PrintResult) -> Unit) {
        printCallback = callback
        if (printData == null)
            return
        es.submit(TaskPrint(mPos))
    }

    fun disconnect(callback: (Boolean) -> Unit) {
        disconnectCallback = callback
        es.submit(TaskClose(mUsb))
    }

    fun resetCallbacks() {
        disconnectCallback = null
        printCallback = null
        readyCallback = null
        printData = null
    }

    private fun probe() {
        val mUsbManager = mActivity!!.getSystemService(USB_SERVICE) as UsbManager
        val deviceList = mUsbManager.deviceList
        val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
        if (deviceList.size > 0) {
            // 初始化选择对话框布局，并添加按钮和事件
            if (deviceIterator.hasNext()) { // 这里是if不是while，说明我只想支持一种device
                val device = deviceIterator.next()
                //Toast.makeText( this, device.toString(), Toast.LENGTH_SHORT).show();

                val mPermissionIntent = PendingIntent
                    .getBroadcast(
                        mActivity!!.applicationContext,
                        0,
                        Intent(
                            mActivity!!
                                .applicationInfo.packageName
                        ),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                if (!mUsbManager.hasPermission(device)) {
                    mUsbManager.requestPermission(
                        device,
                        mPermissionIntent
                    )
                    Toast.makeText(
                        mActivity!!,
                        "permission denied", Toast.LENGTH_LONG
                    ).show()
                    if(readyCallback != null) {
                        readyCallback!!(false)
                    }
                } else {
                    Toast.makeText(mActivity!!, "Connecting...", Toast.LENGTH_SHORT).show()
                    es.submit(
                        TaskOpen(
                            mUsb,
                            mUsbManager,
                            device,
                            mActivity!!
                        )
                    )
                    //es.submit(new TaskTest(mPos,mUsb,mUsbManager,device,mActivity));
                }

            } else {
                Toast.makeText(mActivity!!, "No printer found", Toast.LENGTH_SHORT).show()
                if(readyCallback != null) {
                    readyCallback!!(false)
                }
            }
        } else {
            Toast.makeText(mActivity!!, "No usb devices found", Toast.LENGTH_SHORT).show()
            if(readyCallback != null) {
                readyCallback!!(false)
            }
        }
    }

    inner class TaskTest(
        pos: CSNPOS,
        usb: CSNUSBPrinting?,
        usbManager: UsbManager?,
        usbDevice: UsbDevice?,
        context: Context?
    ) :
        Runnable {
        var pos: CSNPOS? = null
        var usb: CSNUSBPrinting? = null
        var usbManager: UsbManager? = null
        var usbDevice: UsbDevice? = null
        var context: Context? = null

        init {
            this.pos = pos
            this.usb = usb
            this.usbManager = usbManager
            this.usbDevice = usbDevice
            this.context = context
            pos.Set(usb)
        }

        override fun run() {
            // TODO Auto-generated method stub
            for (i in 0..999) {
                var beginTime = System.currentTimeMillis()
                if (usb!!.Open(usbManager, usbDevice, context)) {
                    var endTime = System.currentTimeMillis()
                    pos!!.POS_S_Align(0)
                    pos!!.POS_S_TextOut(
                        i.toString() + "\t" + "Open\tUsedTime:" + (endTime - beginTime) + "\r\n",
                        0,
                        0,
                        0,
                        0,
                        0
                    )
                    beginTime = System.currentTimeMillis()
                    val ticketResult = pos!!.POS_TicketSucceed(i, 30000)
                    endTime = System.currentTimeMillis()
                    pos!!.POS_S_TextOut(
                        i.toString() + "\t" + "Ticket\tUsedTime:" + (endTime - beginTime) + "\t" + (if (ticketResult == 0) "Succeed" else "Failed") + "\r\n",
                        0,
                        0,
                        0,
                        0,
                        0
                    )
                    pos!!.POS_FullCutPaper()
                    usb!!.Close()
                }
            }
        }
    }

    inner class TaskOpen(
        usb: CSNUSBPrinting,
        usbManager: UsbManager,
        usbDevice: UsbDevice,
        context: Context
    ) :
        Runnable {
        private var usb: CSNUSBPrinting
        private var usbManager: UsbManager
        private var usbDevice: UsbDevice
        private var context: Context

        init {
            this.usb = usb
            this.usbManager = usbManager
            this.usbDevice = usbDevice
            this.context = context
        }

        override fun run() {
            usb.Open(usbManager, usbDevice, context)
        }
    }

    inner class TaskPrint(pos: CSNPOS) : Runnable {
        private var pos: CSNPOS

        init {
            this.pos = pos
        }

        override fun run() {
            val bPrintResult: Int = Prints.PrintTicket(
                mActivity!!,
                pos,
                nPrintWidth,
                bCutter,
                bDrawer,
                bBeeper,
                nPrintCount,
                nPrintContent,
                nCompressMethod,
                printData!!
            )
            val bIsOpened = pos.GetIO().IsOpened()
            mActivity!!.runOnUiThread {
                Toast.makeText(
                    mActivity!!,
                    if (bPrintResult >= 0) "print success" + " " + Prints.resultCodeToString(
                        bPrintResult
                    ) else "print failed" + " " + Prints.resultCodeToString(
                        bPrintResult
                    ),
                    Toast.LENGTH_SHORT
                ).show()
                if(bPrintResult >= 0) {
                    if (printCallback != null) {
                        printCallback!!(PrintResult(PrintState.SUCCESS, Prints.resultCodeToString(bPrintResult)  ))
                    }
                } else {
                    if (printCallback != null) {
                        printCallback!!(PrintResult(PrintState.ERROR, Prints.resultCodeToString(bPrintResult)  ))
                    }
                }
                //mActivity!!.btnPrint!!.isEnabled = bIsOpened
            }
        }
    }

    inner class TaskClose(usb: CSNUSBPrinting) : Runnable {
        private var usb: CSNUSBPrinting

        init {
            this.usb = usb
        }

        override fun run() {
            usb.Close()
        }
    }

    override fun OnOpen() {
        mActivity!!.runOnUiThread {
            Toast.makeText(mActivity, "Connected", Toast.LENGTH_SHORT).show()
            if(readyCallback != null) {
                readyCallback!!(true)
            }
        }
    }

    override fun OnOpenFailed() {
        mActivity!!.runOnUiThread {
            Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show()
            if(readyCallback != null) {
                readyCallback!!(false)
            }
        }
    }

    override fun OnClose() {
        if(disconnectCallback != null) {
            disconnectCallback!!(true)
        }
        resetCallbacks()
       /* mActivity!!.runOnUiThread {
            probe() // 如果因为打印机关机导致Close。那么这里需要重新枚举一下。
        }*/
    }

    companion object {
        var dwWriteIndex = 1
    }
}