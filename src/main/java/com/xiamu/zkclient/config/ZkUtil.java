package com.xiamu.zkclient.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author ybfu3
 * @description
 * @date Create in 9:43 2021/4/9
 */
@Slf4j
public class ZkUtil {

    private final static ExecutorService POOL = new ThreadPoolExecutor(3,
            3,
            500,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new zkThreadFactory()
    );
    private static CuratorFramework zookeeperClient;

    public ZkUtil(String host) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zookeeperClient = CuratorFrameworkFactory.newClient(host, 5000, 5000, retryPolicy);
        zookeeperClient.start();
    }

    /**
     * 监听某个节点变化
     *
     * @param path 监听路径
     */
    public void watchNode(String path) {
        final NodeCache nodeCache = new NodeCache(zookeeperClient, path, false);
        try {
            nodeCache.start(true);
            nodeCache.getListenable().addListener(() -> log.info("Node data is changed, new data: " +
                    new String(nodeCache.getCurrentData().getData())), POOL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听子节点的变化
     *
     * @param path 父路径
     */
    public void watchChildNode(String path) {
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(zookeeperClient, path, true);
        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            pathChildrenCache.getListenable().addListener((curatorFramework, event) -> {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        log.info("CHILD_ADDED: " + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        log.info("CHILD_REMOVED: " + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        log.info("CHILD_UPDATED: " + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }, POOL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有的子路径
     *
     * @param path
     * @return
     */
    public List<String> getChildren(String path) {
        try {
            return zookeeperClient.getChildren().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取节点数据
     */
    public String getData(String path) {
        try {
            return new String(zookeeperClient.getData().forPath(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static class zkThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ZK监听线程");
        }
    }
}
