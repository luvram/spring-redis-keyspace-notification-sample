package sample.redis.keyspacenotification

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class ExpirationListener : MessageListener {
    companion object {
        private val log = LoggerFactory.getLogger(this.javaClass)
    }

    override fun onMessage(message: Message, bytes: ByteArray?) {
        val key: String = String(message.getBody())
        if (key.toLong() % 10000L == 0L) {
            log.info("expired key: {}", key)
        }

    }
}
