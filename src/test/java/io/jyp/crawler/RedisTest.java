package io.jyp.crawler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @Rollback(value = false)
    void get() {
        String value = redisTemplate.opsForValue().get("notice:main");
        Assertions.assertEquals("1234", value);
    }

    @Test
    @Rollback(value = false)
    void set() {
        redisTemplate.opsForValue().set("notice:main", "0000");
    }
}
