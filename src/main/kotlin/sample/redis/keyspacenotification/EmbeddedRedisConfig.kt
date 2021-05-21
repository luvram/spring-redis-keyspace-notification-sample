package sample.redis.keyspacenotification

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer
import java.io.IOException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


@Profile("local")
@Configuration
class EmbeddedRedisConfig {
    @Value("\${spring.redis.port}")
    private val redisPort = 0

    private var redisServer: RedisServer? = null

    @PostConstruct
    @Throws(IOException::class)
    fun redisServer() {
        redisServer = RedisServer.builder()
            .port(redisPort)
            .setting("notify-keyspace-events Egx")
            .build()

        redisServer!!.start()
    }

    @PreDestroy
    fun stopRedis() {
        if (redisServer != null) {
            redisServer!!.stop()
        }
    }
}
