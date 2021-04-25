package com.xiamu.zkclient.zookeper.core;

import com.alibaba.fastjson.JSONObject;
import com.xiamu.zkclient.config.ZkUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ybfu3
 * @description
 * @date Create in 14:14 2021/4/9
 */
@Configuration
public class ConfigManage {
    public static final ConcurrentHashMap<String, Object> propertiesMap = new ConcurrentHashMap<>();
    public static final String HOST = "host";
    public static final String ROOT_NODE = "/config";
    public static Properties properties = new Properties();
    public static Properties springProperties = new Properties();


    private static void initDefaultConfig() {
        ClassPathResource classPathResource = new ClassPathResource("config.properties");
        InputStream inputStream = null;
        InputStreamReader temp = null;
        try {
            inputStream = classPathResource.getInputStream();
            temp = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties.load(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRootPath() {
        String app = properties.getProperty("app");
        String group = properties.getProperty("group");
        return ROOT_NODE + "/" + app + "/" + group;
    }

    private static void initMap() {
        initDefaultConfig();
        ZkUtil zkUtil = new ZkUtil(properties.getProperty(HOST));
        List<String> childPath = zkUtil.getChildren(getRootPath());
        childPath.forEach(path -> {
            Properties properties = new Properties();
            String dataStr = zkUtil.getData(getRootPath() + "/" + path);
            JSONObject data = JSONObject.parseObject(dataStr);
            data.forEach(springProperties::put);
            data.forEach(properties::put);
            propertiesMap.put(path, properties);
        });
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        initMap();
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setProperties(springProperties);
        return propertySourcesPlaceholderConfigurer;
    }

}
