package kr.co.noob.copyclient.data

data class SMSMessage(var from: String, var content: String) {
    override fun toString(): String {
        return "SMSMessage(from='$from', content='$content')"
    }
}