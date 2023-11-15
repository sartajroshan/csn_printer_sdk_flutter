package csn.printer_sdk.csn_printer_sdk

import PrintInputData
import android.content.Context
import com.csnprintersdk.csnio.CSNPOS

object Prints {
    fun PrintTicket(
        ctx: Context,
        pos: CSNPOS,
        nPrintWidth: Int,
        bCutter: Boolean,
        bDrawer: Boolean,
        bBeeper: Boolean,
        nCount: Int,
        nPrintContent: Int,
        nCompressMethod: Int,
        data: List<PrintInputData>
    ): Int {
        var bPrintResult = 0
        val status = ByteArray(1)
        if (pos.POS_RTQueryStatus(status, 3, 1000, 2)) {
            if (status[0].toInt() and 0x08 == 0x08) //Determine whether the cutter is abnormal
                return (-2).also { bPrintResult = it }
            if (status[0].toInt() and 0x40 == 0x40) //Determine whether the print head is within the normal range
                return (-3).also { bPrintResult = it }
            if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
                if (status[0].toInt() and 0x04 == 0x04) //Determine whether the lid is closed properly
                    return (-6).also { bPrintResult = it }
                if (status[0].toInt() and 0x20 == 0x20) //Determine whether there is a shortage of paper
                    return (-5).also { bPrintResult = it } else {

                    for (i in 0 until nCount) {
                        if (!pos.GetIO().IsOpened()) break
                        if (nPrintContent >= 1) {
                            if (nPrintWidth == 384) {
                                pos.POS_Reset()

                                data.forEach { printInputData ->
                                    when(printInputData.dataType) {
                                        PrintInputDataType.FEEDLINE -> {
                                            pos.POS_FeedLine()
                                        }
                                        PrintInputDataType.QRCODE ->  {
                                            pos.POS_S_SetQRcode(printInputData.inputQrCode!!.strCodedata,
                                                printInputData.inputQrCode.nWidthX.toInt(),
                                                printInputData.inputQrCode.nVersion.toInt(),
                                                printInputData.inputQrCode.nErrorCorrectionLevel.toInt()
                                                )
                                        }
                                        PrintInputDataType.TEXT -> {
                                            pos.POS_TextOut(
                                                printInputData.inputText!!.pszString,
                                                printInputData.inputText.nLan.toInt(),
                                                printInputData.inputText.nOrgx.toInt(),
                                                printInputData.inputText.nWidthTimes.toInt(),
                                                printInputData.inputText.nHeightTimes.toInt(),
                                                printInputData.inputText.nFontType.toInt(),
                                                printInputData.inputText.nFontStyle.toInt()
                                            )
                                        }
                                    }
                                }
                                pos.POS_FeedLine()
                            } else {
                                pos.POS_Reset()
                                pos.POS_FeedLine()

                                pos.POS_FeedLine()
                                pos.POS_FeedLine()
                                pos.POS_FeedLine()
                                pos.POS_FeedLine()
                            }
                            if (nPrintContent == 1 && nCount > 1) {
                                pos.POS_HalfCutPaper()
                                try {
                                    Thread.currentThread()
                                    Thread.sleep(4000)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }
                            }
                        }

                    }

                }
                if (bBeeper) pos.POS_Beep(1, 5)
                if (bCutter && nCount == 1) pos.POS_FullCutPaper()
                if (bDrawer) pos.POS_KickDrawer(0, 100)
                if (nCount == 1) {
                    try {
                        Thread.currentThread()
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            return (-8).also {
                bPrintResult = it //Query failed
            }
        }
        return 0.also { bPrintResult = it }
    }

    fun resultCodeToString(code: Int): String {
        return when (code) {
            3 -> "Uncollected receipts at the paper outlet, please take them away."
            2 -> "The paper is almost out and there are untaken receipts at the paper outlet."
            1 -> "please replace the paper roll."
            0 -> " "
            -1 -> "please check whether there is a paper jam"
            -2 -> "The cutter is abnormal, please manually eliminate it."
            -3 -> "The print head is overheated, please wait for the printer to cool down"
            -4 -> "Printer offline"
            -5 -> "Printer is out of paper"
            -6 -> "Open the upper cover"
            -7 -> "Real-time status query failed"
            -8 -> "Failed to query the status. Please check whether the communication port is connected properly."
            -9 -> "There is a shortage of paper during printing. Please check the document integrity."
            -10 -> "The upper cover is opened during printing, please print again."
            -11 -> "The connection is interrupted, please confirm whether the printer is connected"
            -12 -> "Please take away the printed receipt before printing!"
            -13 -> "unknown mistake"
            else -> "unknown mistake"
        }
    }

}