package com.mars.cloud.core.helper;

import com.mars.cloud.config.MarsCloudConfig;
import com.mars.cloud.config.model.CloudConfig;
import com.mars.cloud.util.MarsCloudConfigUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper帮助类
 */
public class ZkHelper {

    private static Logger marsLogger = LoggerFactory.getLogger(ZkHelper.class);

    /**
     * zk对象
     */
    private static ZooKeeper zooKeeper;

    /**
     * session超时时间
     */
    private static int sessionTimeout = 100000;

    /**
     * 注册中心地址
     */
    private static String registeds;

    private static CountDownLatch countDownLatch;

    /**
     * 初始化
     *
     * @throws Exception 异常
     */
    private static void init() throws Exception {
        MarsCloudConfig config = MarsCloudConfigUtil.getMarsCloudConfig();
        CloudConfig cloudConfig = config.getCloudConfig();
        registeds = cloudConfig.getRegister();
        Object configTimeOut = cloudConfig.getSessionTimeout();
        if (configTimeOut != null) {
            sessionTimeout = Integer.parseInt(configTimeOut.toString());
            if (sessionTimeout <= 30000) {
                sessionTimeout = 30000;
            }
        }
    }

    /**
     * 打开连接
     *
     * @throws Exception 异常
     */
    public static void openConnection() throws Exception {
        try {
            if (registeds == null) {
                init();
            }

            if (!hasConnection()) {
                countDownLatch = new CountDownLatch(1);
                zooKeeper = new ZooKeeper(registeds, sessionTimeout, new ZkWatcher(countDownLatch));
                countDownLatch.await();
                marsLogger.info("连接zookeeper成功");
            }
        } catch (Exception e) {
            throw new Exception("连接zookeeper失败", e);
        }
    }

    /**
     * 是否处于连接状态
     *
     * @return false 没有连接，true 已连接
     */
    public static boolean hasConnection() {
        if (zooKeeper != null && zooKeeper.getState().isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 创建节点
     *
     * @param path 路径
     * @param data 数据
     * @return 结果
     * @throws Exception 异常
     */
    public static String createNodes(String path, String data) throws Exception {
        openConnection();

        String[] pa = path.split("/");
        StringBuffer pat = new StringBuffer();
        for (int i = 1; i < pa.length; i++) {
            pat.append("/");
            pat.append(pa[i]);
            if (i < pa.length - 1) {
                createNode(pat.toString(), "blank", CreateMode.PERSISTENT);
            } else {
                createNode(pat.toString(), data, CreateMode.EPHEMERAL);
            }
        }
        return "ok";
    }

    /**
     * 创建节点
     *
     * @param path 路径
     * @param data 数据
     * @param createMode 模式
     * @return 结果
     * @throws Exception 异常
     */
    public static String createNode(String path, String data, CreateMode createMode) throws Exception {
        openConnection();

        Stat stat = zooKeeper.exists(path, true);
        if (stat != null && createMode.equals(CreateMode.EPHEMERAL)) {
            zooKeeper.delete(path, -1);
        } else if (stat != null && createMode.equals(CreateMode.PERSISTENT)) {
            setData(path, data);
            return "ok";
        }
        return zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    /**
     * 获取路径下所有子节点
     *
     * @param path 路径
     * @return 结果
     * @throws Exception 异常
     */
    public static List<String> getChildren(String path) throws Exception {
        openConnection();

        List<String> children = zooKeeper.getChildren(path, true);
        return children;
    }

    /**
     * 获取节点上面的数据
     *
     * @param path 路径
     * @return 结果
     * @throws Exception 异常
     */
    public static String getData(String path) throws Exception {
        openConnection();

        Stat stat = zooKeeper.exists(path, true);
        if (stat != null) {
            byte[] data = zooKeeper.getData(path, true, stat);
            if (data != null) {
                return new String(data);
            }
        }
        return null;
    }

    /**
     * 设置节点信息
     *
     * @param path 路径
     * @param data 数据
     * @throws Exception 异常
     */
    public static void setData(String path, String data) throws Exception {
        openConnection();

        if (zooKeeper.exists(path, true) != null) {
            zooKeeper.setData(path, data.getBytes(), -1);
        }
    }
}
