package kr.co.noob.copyclient.data

enum class Log {

    // Log for socket
    ACTION_CONNECTED,
    ACTION_DISCONNECTED,
    ACTION_REJECTED,
    ACTION_ACK,
    ACTION_SEND ,
    ACTION_RECEIVED,
    ACTION_ERROR,

    // Log for broadcast receiver
    ACTION_SMS_RECEIVED,
    ACTION_ON_BOOT
}