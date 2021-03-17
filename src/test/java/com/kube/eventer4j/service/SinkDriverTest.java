package com.kube.eventer4j.service;

import com.kube.eventer4j.Eventer4jApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Eventer4jApplication.class)
public class SinkDriverTest {

    @Test
    public void testMysqlDriver() throws Exception {
        log.info("=========== unitMysqlDriverTest start ==========");

        Assert.assertEquals(1,1);
    }

    @Test
    public void testRedisDriver() throws Exception {
        log.info("=========== unitRedisDriverTest start ==========");
        Assert.assertEquals(1,1);
    }

    @Test
    public void testKafkaDriver() throws Exception {
        log.info("=========== unitKafkaDriverTest start ==========");
        Assert.assertEquals(1,1);
    }

    @Test
    public void testMinioDriver() throws Exception {
        log.info("=========== unitMinioTest start ==========");
        Assert.assertEquals(1,1);
    }
}
