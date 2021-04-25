package com.xiamu.zkclient.config;

import com.xiamu.zkclient.zookeper.core.ConfigManage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ybfu3
 * @description
 * @date Create in 17:15 2021/4/9
 */
@Configuration
public class DataSourceConfig {
    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;

    @Bean
    public void test(){
        System.out.println(username);
        System.out.println(password);
        System.out.println(ConfigManage.propertiesMap.toString());
    }
}
