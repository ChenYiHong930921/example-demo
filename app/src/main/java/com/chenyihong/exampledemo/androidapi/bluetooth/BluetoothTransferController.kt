package com.chenyihong.exampledemo.androidapi.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Handler
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothTransferController(private val bluetoothAdapter: BluetoothAdapter, private val callback: ((totalLength: Int, byteArray: ByteArray) -> Unit)?) {
    private val bluetoothUUID = UUID.fromString("fc5deb71-9d4b-460b-b725-b06ea79bda5a")

    private var acceptThread: AcceptThread? = null
    private var connectThread: ConnectThread? = null
    private var connectedThread: ConnectedThread? = null

    fun startAtAcceptClient() {
        if (acceptThread != null) {
            acceptThread?.cancel()
            acceptThread = null
        }
        if (connectedThread != null) {
            connectedThread?.cancel()
            connectedThread = null
        }
        acceptThread = AcceptThread()
        acceptThread?.start()
    }

    fun connectToOtherBluetoothDevice(bluetoothDevice: BluetoothDevice) {
        if (connectThread != null) {
            connectThread?.cancel()
            connectThread = null
        }
        if (connectedThread != null) {
            connectedThread?.cancel()
            connectedThread = null
        }
        connectThread = ConnectThread(bluetoothDevice)
        connectThread?.start()
    }

    fun writeData(sendData: ByteArray) {
        connectedThread?.writeData(sendData)
    }

    private fun connected(bluetoothSocket: BluetoothSocket) {
        if (connectedThread != null) {
            connectedThread?.cancel()
            connectedThread = null
        }
        connectedThread = ConnectedThread(bluetoothSocket)
        connectedThread?.start()
    }

    inner class AcceptThread() : Thread() {
        private val bluetoothServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter.listenUsingRfcommWithServiceRecord("ExampleDemo", bluetoothUUID)
        }

        override fun run() {
            super.run()
            var waitConnect = true
            while (waitConnect) {
                try {
                    val bluetoothSocket = bluetoothServerSocket?.accept()
                    if (bluetoothSocket != null) {
                        connected(bluetoothSocket)
                        bluetoothServerSocket?.close()
                        waitConnect = false
                    }
                } catch (e: IOException) {
                    waitConnect = false
                }
            }
        }

        fun cancel() {
            try {
                bluetoothServerSocket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    inner class ConnectThread(private val bluetoothDevice: BluetoothDevice) : Thread() {

        private val bluetoothSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothDevice.createRfcommSocketToServiceRecord(bluetoothUUID)
        }

        override fun run() {
            super.run()
            bluetoothSocket?.run {
                try {
                    connect()
                    connected(this)
                } catch (e: IOException) {
                    try {
                        close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun cancel() {
            try {
                bluetoothSocket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    inner class ConnectedThread(private val bluetoothSocket: BluetoothSocket) : Thread() {

        private var inputStream: InputStream? = null
        private var outputStream: OutputStream? = null
        private var connected = false

        init {
            connected = bluetoothSocket.isConnected
            try {
                if (bluetoothSocket.isConnected) {
                    inputStream = bluetoothSocket.inputStream
                    outputStream = bluetoothSocket.outputStream
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun run() {
            super.run()
            val buffer = ByteArray(1024)
            while (connected) {
                inputStream?.let {
                    try {
                        val bytes = it.read(buffer)
                        callback?.invoke(bytes, buffer)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun writeData(sendData: ByteArray) {
            try {
                outputStream?.write(sendData)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun cancel() {
            try {
                inputStream?.close()
                outputStream?.close()
                bluetoothSocket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}