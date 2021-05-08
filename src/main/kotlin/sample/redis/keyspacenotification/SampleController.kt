package sample.redis.keyspacenotification

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/keys")
class SampleController(
    private val sampleService: SampleService
) {
    @PostMapping
    fun create(@RequestBody body: RequestDto) {
        sampleService.create(body.name)
    }
    @GetMapping("/{keyName}/heartbeat")
    fun heartbeat(@PathVariable keyName: String) {
        sampleService.heartbeat(keyName)
    }

    @GetMapping("/{keyName}/ttl")
    fun getTTL(@PathVariable keyName: String): Long? {
        return sampleService.leftTime(keyName)
    }


}