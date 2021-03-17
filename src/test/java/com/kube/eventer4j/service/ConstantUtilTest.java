package com.kube.eventer4j.service;

import com.kube.eventer4j.Eventer4jApplication;
import com.kube.eventer4j.utils.ConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Eventer4jApplication.class)
public class ConstantUtilTest {

    @Test
    public void testReadYaml() throws Exception {
        log.info("=========== unitReadYamlTest start ==========");
        log.info(ConstantUtil.initSource);
        Assert.assertEquals(1,1);
    }
}
