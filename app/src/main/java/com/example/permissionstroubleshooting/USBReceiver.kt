package com.example.permissionstroubleshooting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService

private lateinit var bytes: ByteArray
private val TIMEOUT = 0
private val forceClaim = true


//private const val TAG = "USBReceiver"
//class USBReceiver: BroadcastReceiver() {
//
//    override fun onReceive(context: Context, intent: Intent) {
//        if (ACTION_USB_PERMISSION == intent.action) {
//            Log.d(TAG, "onReceive if ACTION_USB_PERMISSION asked")
//            synchronized(this) {
//                val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
//
//                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                    device?.apply {
//                        Log.d(TAG, "Looking for method to set up device comms")
//                        //call method to set up device communication
//                        device.getInterface(0).getEndpoint(0)
//
//                        Toast.makeText(context, "Permissions granted whoop!", Toast.LENGTH_LONG).show()
//                    }
//                } else {
//                    Log.d(TAG, "permission denied for device $device")
//                    Toast.makeText(context, "Permissions denied boo!", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//}