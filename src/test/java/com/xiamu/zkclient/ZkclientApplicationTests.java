package com.xiamu.zkclient;

import com.xiamu.zkclient.config.ZkUtil;
import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
class ZkclientApplicationTests {

    @Autowired
    ZkUtil zkUtil;

    @Test
    void contextLoads() {
    }

}
