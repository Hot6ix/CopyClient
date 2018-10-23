package kr.co.noob.copyclient.uilts

import android.util.Log
import kr.co.noob.copyclient.data.Message
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Error
import java.net.Socket

class ReceiverThread(private var socket: Socket): Thread() {

    private var messageListener: MessageListener? = null


    override fun start() {
        super.start()

        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            while(true) {
                val msg = reader.readLine()
                val result: Message = if(msg.isNullOrEmpty()) Message.EOC else Message.valueOf(msg)

                messageListener?.onMessageReceived(result)
                if(result == Message.EOC || result == Message.REJ) {
                    break
                }
            }

            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            messageListener?.onMessageReceived(Message.EOC)
        }

    }

    fun setOnMessageListener(listener: MessageListener) {
        this.messageListener = listener
    }

    interface ThreadListener {
        public fun onThreadStart(id: Long)
        public fun onThreadEnd(id: Long)
    }

    interface MessageListener {
        public fun onMessageReceived(msg: Message)
    }

}