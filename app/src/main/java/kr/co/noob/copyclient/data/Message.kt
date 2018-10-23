package kr.co.noob.copyclient.data

enum class Message(val code: Int) {
    EMPTY(0), CONNECTED(1), EOC(2), ACK(3), REJ(4), RQ_PW(5);
}