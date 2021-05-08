package sample.redis.keyspacenotification

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class ExpirationListener: MessageListener {
    override fun onMessage(message: Message, bytes: ByteArray?) {
        val key: String = String(message.getBody())
        println("expired key: ${key}")
    }


}