package kr.co.noob.copyclient.uilts

import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import java.io.BufferedOutputStream
import java.io.PrintWriter
import java.net.Socket

class MessageSender(private var socket: Socket): AsyncTask<String, Boolean, Boolean>() {

    override fun doInBackground(vararg params: String?): Boolean {

        if(socket.isClosed) return true

        val writer = PrintWriter(BufferedOutputStream(socket.getOutputStream()))
        writer.println(params[0])
        writer.flush()

        return writer.checkError()
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)

    }

}