package sample.redis.keyspacenotification

import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisOperations
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ExpirationListener(
    val redissonClient: RedissonClient,
    val redisTemplate: RedisOperations<String, String>
) : MessageListener {
    companion object {
        private val log = LoggerFactory.getLogger(this.javaClass)
    }

    override fun onMessage(message: Message, bytes: ByteArray?) {
        val hashOperations = redisTemplate.opsForHash<String, String>()
        val key: String = String(message.getBody())
        val lock = redissonClient.getLock("${key}:lock")

        if (lock.tryLock()) {
            try {
                val cache = hashOperations.get("cache", key)
                if(cache == null) {
                    hashOperations.put("cache", key, key)
                    log.info("expired key: {}", key)
                } else {
                    log.info("already expired.")
                }
            } finally {
                lock.unlock();
            }
        } else {
            log.info("getting lock failed")
        }
    }
}
