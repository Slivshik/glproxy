package com.wlrus.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.wlrus.AppConfig
import com.wlrus.R
import com.wlrus.databinding.ActivityNoneBinding
import com.wlrus.extension.toast
import com.wlrus.handler.MmkvManager
import com.wlrus.util.QRCodeDecoder
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
// removed BarcodeFormat import - using fully qualified
import io.github.g00fy2.quickie.config.ScannerConfig

class ScannerActivity : HelperBaseActivity() {
    private val binding by lazy {  ActivityNoneBinding.inflate(layoutInflater) }

    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::handleResult)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentViewWithToolbar(binding.root, showHomeAsUp = true, title = getString(R.string.menu_item_import_config_qrcode))

        if (MmkvManager.decodeSettingsBool(AppConfig.PREF_START_SCAN_IMMEDIATE)) {
            launchScan()
        }
    }

private fun launchScan() {
    scanQrCode.launch(
        ScannerConfig.build {
            setHapticSuccessFeedback(true)
            setShowTorchToggle(true)
            setShowCloseButton(true)
            // ❌ Remove this line that's causing the error:
            // setBarcodeFormats(listOf(io.github.g00fy2.quickie.config.BarcodeFormat.QR_CODE))
            
            // ✅ QR_CODE is scanned by default - no need to specify
        }
    )
}

    private fun handleResult(result: QRResult) {
        if (result is QRResult.QRSuccess) {
            finished(result.content.rawValue.orEmpty())
        } else {
            finish()
        }
    }

    private fun finished(text: String) {
        val intent = Intent()
        intent.putExtra("SCAN_RESULT", text)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scanner, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.scan_code -> {
            launchScan()
            true
        }

        R.id.select_photo -> {
            showFileChooser()
            true
        }


        else -> super.onOptionsItemSelected(item)
    }

    private fun showFileChooser() {
        launchFileChooser("image/*") { uri ->
            if (uri == null) {
                return@launchFileChooser
            }
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val text = QRCodeDecoder.syncDecodeQRCode(bitmap)
                if (text.isNullOrEmpty()) {
                    toast(R.string.toast_decoding_failed)
                } else {
                    finished(text)
                }
            } catch (e: Exception) {
                Log.e(AppConfig.TAG, "Failed to decode QR code from file", e)
                toast(R.string.toast_decoding_failed)
            }
        }
    }
}
