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


## 이벤트 성능 테스트
Keyspace Notification으로 받는 Expired 이벤트는 제때 받는것을 보장받지 못한다. 그 이유는 TTL 만료를 다음과 같은 두 방식중 하나로 수행하기 때문이다.
1. 만료가 된 데이터에 접근했을때
2. 주기적으로 실행되는 백그라운드 프로세스가 수행될 때

더군다나 TTL 이 걸려있는 데이터가 많으면 많을수록 이벤트가 늦춰질 가능성이 더 늘어난다.
따라서 어느정도 까지 Expired 이벤트가 지연이 될 지 측정하고 테스트해볼 필요가 있다.

테스트 방법은 다음과 같다.

docker 를 이용하여 redis의 cpu 사용에 제약을걸고 다량의 데이터를 입력하여 실제로 만료되는 시간을 체크한다.  
데이터가 모두 입력되기 전에 데이터가 만료된다면 테스트가 부정활해질 수 있으므로 충분한 TTL시간을 주어 테스트를 진행한다.

1. cpu 하나, 50% cpu 성능, 총 데이터 입력: 10만건, TTL: 1분
    - 10만번째 데이터 입력 시간:     2021-05-23 20:08:26.899
    - 10만번째 데이터 expired 시간: 2021-05-23 20:09:28.374
    - 차이: 약 1.5초
    
2. cpu 하나, 50% cpu 성능, 총 데이터 입력: 100만건, TTL: 1분
    - 100만번째 데이터 입력 시간: 2021-05-23 20:21:49.095
    - 100만번째 데이터 만료 시간: 2021-05-23 20:23:12.712
    - 차이: 23초

3. cpu 하나, 100% cpu 성능, 총 데이터 입력: 100만건, TTL: 3분
    - 첫번째 데이터 입력 시간: 2021-05-23 22:51:02.141
    - 첫번째 데이터 만료 시간: 2021-05-23 22:54:58.407
    - 차이: 약 56초

    - 만번째 데이터 입력 시간: 2021-05-23 22:51:04.110
    - 만번째 데이터 만료 시간: 2021-05-23 22:54:49.140
    - 차이: 약 45초
    
    - 50만번째 데이터 입력 시간: 2021-05-23 22:51:45.749
    - 50만번째 데이터 만료 시간: 2021-05-23 22:55:05.728
    - 차이: 약 20초
    
    - 100만번째 데이터 입력 시간: 2021-05-23 22:52:31.430
    - 100만번째 데이터 만료 시간: 2021-05-23 22:55:38.995
    - 차이: 약 7초

4. cpu 하나, 100% cpu 성능, 총 데이터 입력: 500만건, TTL: 10분
    - 2만번째 데이터 입력 시간: 2021-05-23 21:45:24.288
    - 2만번째 데이터 만료 시간: 2021-05-23 21:58:39.641
    - 차이: 약 3분 15초

    - 6만번째 데이터 입력 시간: 2021-05-23 21:45:27.574
    - 6만번째 데이터 만료 시간: 2021-05-23 21:58:04.287
    - 차이: 약 2분 37초

    - 350만번째 데이터 입력 시간: 2021-05-23 21:50:35.528
    - 350만번째 데이터 만료 시간: 2021-05-23 22:01:29.575
    - 차이: 약 1분

    - 500만번째 데이터 입력 시간: 2021-05-23 21:52:54.818
    - 500만번째 데이터 만료 시간: 2021-05-23 22:03:10.646
    - 차이: 약 16초

