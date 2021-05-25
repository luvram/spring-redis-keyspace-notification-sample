package sample.redis.keyspacenotification

import org.redisson.Redisson
import org.redisson.api.RMapCache
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.transaction.annotation.EnableTransactionManagement


@Configuration
//@EnableTransactionManagement
class ListenerConfig {
    @Bean
    fun keyExpirationListenerContainer(
        connectionFactory: RedisConnectionFactory,
        expirationListener: ExpirationListener
    ): RedisMessageListenerContainer {
        val listenerContainer = RedisMessageListenerContainer()
        listenerContainer.setConnectionFactory(connectionFactory)
        listenerContainer.addMessageListener (expirationListener, PatternTopic("__keyevent@*__:expired"))
        listenerContainer.setErrorHandler { e: Throwable? ->
            println(
                "There was an error in redis key expiration listener container",
            )
        }
        return listenerContainer
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config().apply {
            this.useSingleServer().address = "redis://localhost:6379"
        }
        return Redisson.create(config)
    }
}
