package com.example.permissionstroubleshooting

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.*
import android.hardware.usb.UsbConstants.USB_DIR_IN
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.permissionstroubleshooting.databinding.ActivityMainBinding
import androidx.core.app.ActivityCompat


const val ACTION_USB_PERMISSION = "com.android.example.permissionstroubleshooting.USB_PERMISSION"
const val ACTION_CAMERA_PERMISSION = "com.android.example.permissionstroubleshooting.CAMERA"

// TODO: Implement the broadcast receiver at https://developer.android.com/guide/topics/connectivity/usb/host as I have for the main app

private const val TAG = "ActivityMain"

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var bytes: ByteArray
    private val TIMEOUT = 0
    private val forceClaim = true
    lateinit var cameraReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "App created")
        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        Log.d(TAG, "device created")
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        Log.d(TAG, "manager created")
        val interfacecount = device?.interfaceCount

        cameraReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d(TAG, "onReceive started - received ${intent.action}")
            }
        }

        binding.permissionButton.setOnClickListener {
            checkForCameraPermission()
        }

        binding.getCameraButton.isEnabled = false

        binding.getCameraButton.setOnClickListener {
            var usbInterfaceTemp: UsbInterface?
            var usbInterface: UsbInterface? = null
            var endpointIN: UsbEndpoint?
            var endpointOUT: UsbEndpoint? = null
            if (interfacecount != null) {
                for (i in 0 until interfacecount + 1) {
                    Log.d(TAG, "Interface count is $interfacecount")
                    usbInterfaceTemp = device.getInterface(i)
                    Log.d(TAG, "device.getinterface($i) loop started")
                    Log.d(TAG, "endpoint count ${usbInterfaceTemp.endpointCount}")
                    if (usbInterfaceTemp.endpointCount == 1) {
                        //for (j in 0 until usbInterfaceTemp.endpointCount+1) {
                        Log.d(TAG, "device.getinterfacetemp.endpointcount loop started")
                        val usbEndpointTemp = usbInterfaceTemp.getEndpoint(0)
                        Log.d(TAG, "endpoint type ${usbEndpointTemp.type}")
                        if (usbEndpointTemp.type == UsbConstants.USB_ENDPOINT_XFERTYPE_MASK) {
                            Log.d(TAG, "endpoint type SUB_ENDPOINT_XFERTYPE_MASK loop started")
                            Log.d(TAG, "endpoint direction is ${usbEndpointTemp.direction}")
                            if (usbEndpointTemp.direction == USB_DIR_IN) {
                                Log.d(
                                    TAG,
                                    "yes indeed the usbendpointtemp direction is USB_DIR_IN number 128 $usbEndpointTemp"
                                )
                                endpointIN = usbEndpointTemp
                                usbInterface = usbInterfaceTemp
                                Log.d(TAG, "usbinterface set as usbinterfaceTemp")

                                checkForCameraPermission()

                                val usbDeviceConnection = manager.openDevice(device)
                                Log.d(TAG, "manager has opened the device line")
                                usbDeviceConnection.claimInterface(usbInterface, forceClaim)
                                Log.d(TAG, "manager has claimed the interface")
                                Log.d(TAG, "at this point the endpointin is $endpointIN")
                                val dataReceived = usbDeviceConnection.bulkTransfer(
                                    endpointIN,
                                    bytes,
                                    bytes.size,
                                    50
                                )
                                Log.d(TAG, "usbDeviceConnection.bulkTransfer called")
                                Log.d(TAG, "Data received is $dataReceived")
                            }
                        }
                    }
                }
            }
        }
//        val permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_CAMERA_PERMISSION), 0)
//        Log.d(TAG, "Permission intent created")
//        val filter = IntentFilter(ACTION_CAMERA_PERMISSION)
//        registerReceiver(CameraReceiver(), filter)
//        Log.d(TAG, "Receiver registered")
//
//        manager.requestPermission(device, permissionIntent)
//        Log.d(TAG, "Permission intent sent by request permission")
    }



    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                binding.getCameraButton.isEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun checkForUsbPermission() {
        if (checkSelfPermissionCompat(ACTION_USB_PERMISSION) ==
            PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, enable the get camera button
        } else {
            // Permission is missing and must be requested.
            requestPermissionLauncher.launch(ACTION_USB_PERMISSION)
        }
    }
    private fun checkForCameraPermission() {
        if (checkSelfPermissionCompat(ACTION_CAMERA_PERMISSION) ==
            PackageManager.PERMISSION_GRANTED) {
            binding.getCameraButton.isEnabled = true
            // Permission is already available, enable the get camera button
        } else {
            // Permission is missing and must be requested.
            requestPermissionLauncher.launch(ACTION_CAMERA_PERMISSION)
        }
    }


fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)



//            if (!(usbDeviceConnection != null && usbDeviceConnection.claimInterface(
//                    usbInterface,
//                    true
//                ))
//            ) {
//                usbDeviceConnection = null
//                Toast.makeText(
//                    this,
//                    "usbDeviceConnection not found",
//                    Toast.LENGTH_LONG
//                ).show()
//            }


//            usbDeviceConnection.controlTransfer(USB_DIR_IN, 34, 0, 0, null, 0, 0)
//            usbDeviceConnection.controlTransfer(
//                USB_DIR_IN, 32, 0, 0, byteArrayOf(
//                    0x80.toByte(),
//                    0x25, 0x00, 0x00, 0x00, 0x00, 0x08
//                ), 7, 0
//            )

//            Toast.makeText(
//                applicationContext,
//                "Device opened and Interface claimed!",
//                Toast.LENGTH_SHORT
//            ).show()
//            Toast.makeText(
//                this,
//                "The device connected to this phone is $devicename $interfacecount",
//                Toast.LENGTH_LONG
//            ).show()
//            device?.getInterface(0)?.also { intf ->
//                intf.getEndpoint(0)?.also { endpoint ->
//                    manager.openDevice(device)?.apply {
//                        Log.d(TAG, "manager is opening device")
//                        claimInterface(intf, forceClaim)
//                        Log.d(TAG, "interface claimed")
//                        bulkTransfer(endpoint, bytes, bytes.size, TIMEOUT) //do in another thread
//                        Log.d(TAG, "bulktransfer called")
//                    }
//                }
//            }


    }

