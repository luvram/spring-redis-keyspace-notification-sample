package sample.redis.keyspacenotification

import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class SampleService(
    private val redisTemplate: RedisOperations<String, String>
) {
    companion object {
        private const val MAX_KEY_ALIVE_TIME = 10L
        private val MAX_KEY_ALIVE_TIMEUNIT = TimeUnit.SECONDS
    }

    fun create(keyName: String) {
        redisTemplate.execute { connection: RedisConnection ->
            connection.multi()
            connection.set(keyName.toByteArray(), keyName.toByteArray())
            connection.expire(keyName.toByteArray(), MAX_KEY_ALIVE_TIME)
            connection.exec()
        }
    }

    fun heartbeat(keyName: String) {
        redisTemplate.expire(keyName, MAX_KEY_ALIVE_TIME, MAX_KEY_ALIVE_TIMEUNIT)
    }

    fun leftTime(keyName: String): Long? {
        return redisTemplate.getExpire(keyName)
    }
}