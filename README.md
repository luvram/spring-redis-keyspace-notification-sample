# Redis Keyspace Notification 을 위한 샘플

## 목적
Redis 의 Keyspace Notification 을 이용하여 지정된 Redis 키가 TTL에 의해 만료되었을때의 이벤트를 받아 처리할 수 있는 기능을 샘플로 만들어본다.

## API 설명
- Key 생성 및 TTL 세팅
    ```text
    POST /keys
  
    { "name": ${key name} }
    ```
  Key 생성 뒤에 바로 TTL 세팅이 이뤄질 수 있도록 Transaction 을 사용하였다.


- Heartbeat
    ```text
    GET /keys/{keyName}/heartbeat
    ```
  {keyName}의 TTL을 리셋한다.


- 남은 TTL 조회
    ```text
    GET /keys/{keyName}/ttl
    ```
  {keyName} 의 남은 TTL을 조회한다.
  