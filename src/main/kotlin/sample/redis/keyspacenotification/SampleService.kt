package sample.redis.keyspacenotification

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class SampleService(
    private val redisTemplate: RedisOperations<String, String>
) {
    companion object {
        private val log = LoggerFactory.getLogger(this.javaClass)
        private const val MAX_KEY_ALIVE_TIME = 5L
        private val MAX_KEY_ALIVE_TIMEUNIT = TimeUnit.SECONDS
    }

    fun create(keyName: String) {
        redisTemplate.execute { connection: RedisConnection ->
            connection.multi()
            connection.set(keyName.toByteArray(), keyName.toByteArray())
            connection.expire(keyName.toByteArray(), MAX_KEY_ALIVE_TIME)
            val keyData = "${keyName}:data"
            connection.set(keyData.toByteArray(), keyData.toByteArray())
            connection.exec()
        }
    }

    fun heartbeat(keyName: String) {
        redisTemplate.expire(keyName, MAX_KEY_ALIVE_TIME, MAX_KEY_ALIVE_TIMEUNIT)
    }

    fun leftTime(keyName: String): Long? {
        return redisTemplate.getExpire(keyName)
    }

    fun createBulk() {
        val opsForValue = redisTemplate.opsForValue()
        log.info("### start bulk insert ###")
        for (i in 0..1_000_000) {
            CoroutineScope(Dispatchers.IO).launch {
                val key = i.toString()
                opsForValue.set(key, key)
                redisTemplate.expire(key, 180L, MAX_KEY_ALIVE_TIMEUNIT)
                if (i % 10000 === 0) {
                    log.info("complete {}", key)
                }

            }
        }
        log.info("### end bulk insert ###")
    }
}
