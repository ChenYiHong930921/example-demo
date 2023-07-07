package com.chenyihong.exampledemo.androidapi.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothTransferController(private val bluetoothAdapter: BluetoothAdapter, private val connectedCallback: () -> Unit, private val receiveCallback: (byteArray: ByteArray) -> Unit) {

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
        connectedThread?.cancel()
        if (connectedThread != null) {
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
                    // accept为阻塞方法，不能在主线程中调用
                    val bluetoothSocket = bluetoothServerSocket?.accept()
                    if (bluetoothSocket != null) {
                        // 后续可以通过bluetoothSocket传输数据
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
                    // connect为阻塞方法，不能在主线程中调用
                    connect()
                    // 后续可以通过bluetoothSocket传输数据
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
            connectedCallback.invoke()
            val buffer = ByteArray(1024)
            while (connected) {
                inputStream?.let {
                    try {
                        var length: Int
                        while (it.read(buffer).also { length = it } != -1) {
                            val dataByteArray = buffer.copyOf(length)
                            receiveCallback.invoke(dataByteArray)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun writeData(sendData: ByteArray) {
            try {
                outputStream?.write(sendData)
                outputStream?.flush()
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