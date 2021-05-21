# Redis Keyspace Notification 을 위한 샘플

## 목적
Redis 의 Keyspace Notification 을 이용하여 지정된 Redis 키가 TTL에 의해 만료되었을때의 이벤트를 받아 처리할 수 있는 기능을 샘플로 만들어본다.

## 실행
### Embedded Redis 이용 방법
Embedded Redis는 프로젝트에 기본으로 포함시켜 두었고, active profile 일때만 작동하게 해두었으므로 다음과 같이 실행하면 된다.
```shell
./gradlew clean build
java -jar -Dspring.profiles.active=local build/libs/keyspacenotification-0.0.1-SNAPSHOT.jar

```

### Docker 이용 방법
```shell
docker-compose -f docker-compose.yml up -d
./gradlew clean build
java -jar build/libs/keyspacenotification-0.0.1-SNAPSHOT.jar   
```

## Redis 설정
Redis 의 Keyspace Notification 은 CPU 리소스를 약간 잡아먹기 때문에 기본적으로 기능이 off 되어있다. 따라서 다음중 한가지 활성화 할 수 있다. ( 자세한 설정 정보는 [링크](https://redis.io/topics/notifications) 의 `Configuration` 섹션을 참조하도록 한다. )
1. `redis.conf` 에서 `notify-keyspace-events` 설정
2. `config set notify-keyspace-events 값` 으로 명령 실행
    ```shell
    config set notify-keyspace-events Egx
    ```
    
이 프로젝트에서는 embedded redis 및 docker-compose 의 command 를 이용하여 설정하였다.

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
  