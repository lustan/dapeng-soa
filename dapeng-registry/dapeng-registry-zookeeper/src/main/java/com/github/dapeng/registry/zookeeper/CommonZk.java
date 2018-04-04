package com.github.dapeng.registry.zookeeper;

import com.github.dapeng.registry.RuntimeInstance;
import com.github.dapeng.util.SoaSystemEnvProperties;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年03月22日 上午11:17
 */
public class CommonZk {


    private static Logger logger = LoggerFactory.getLogger(CommonZk.class);

    protected String zkHost = SoaSystemEnvProperties.SOA_ZOOKEEPER_HOST;


    protected final static String SERVICE_PATH = "/soa/runtime/services";
    protected final static String CONFIG_PATH = "/soa/config/services";
    protected final static String ROUTES_PATH = "/soa/config/routes";


    protected ZooKeeper zk;
    /**
     * zk 配置 缓存 ，根据 serivceName + versionName 作为 key
     */
    protected ConcurrentMap<String, ZkConfigInfo> zkConfigMap = new ConcurrentHashMap();
    protected ConcurrentMap<String, List<RuntimeInstance>> runInstancesMap = new ConcurrentHashMap();

    /**
     * 获取zk 配置信息，封装到 ZkConfigInfo
     *
     * @param serviceName
     * @return
     */
    protected ZkConfigInfo getConfigData(String serviceName) {
        ZkConfigInfo info = zkConfigMap.get(serviceName);
        if (info != null) {
            return info;
        }

        ZkConfigInfo configInfo = new ZkConfigInfo();

        //1.获取 globalConfig
        try {
            byte[] globalData = zk.getData(CONFIG_PATH, watchedEvent -> {

                if (watchedEvent.getType() == Watcher.Event.EventType.NodeDataChanged) {
                    logger.info(watchedEvent.getPath() + "'s data changed, reset config in memory");
                    zkConfigMap.clear();
                    getConfigData(serviceName);
                }
            }, null);

            WatcherUtils.processZkConfig(globalData, configInfo, true);

        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        // 2. 获取 service config
        String configPath = CONFIG_PATH + "/" + serviceName;

        try {
            byte[] serviceData = zk.getData(configPath, watchedEvent -> {
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeDataChanged) {
                    logger.info(watchedEvent.getPath() + "'s data changed, reset zkConfigMap in memory");
                    zkConfigMap.clear();

                    getConfigData(serviceName);
                }
            }, null);
            WatcherUtils.processZkConfig(serviceData, configInfo, false);

        } catch (KeeperException e) {
            logger.error(e.getMessage());
            if (e instanceof KeeperException.NoNodeException) {
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        zkConfigMap.put(serviceName, configInfo);

        //获得 服务实例列表
        List<RuntimeInstance> runtimeInstanceList = runInstancesMap.get(serviceName);
        if (runtimeInstanceList != null && !runtimeInstanceList.isEmpty()) {
            logger.info(getClass().getSimpleName() + "::getConfigData[" + serviceName + "]: 运行实例信息[service instances]已经初始化完成");
        }else{
            // 拉取服务实例信息
            runtimeInstanceList = new ArrayList<RuntimeInstance>();
            String servicePath = SERVICE_PATH + "/" + serviceName;
            List<String> childrens = new ArrayList<String>();
            try {
                childrens = zk.getChildren(servicePath, watchedEvent -> {
                    if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                        logger.info(getClass().getSimpleName() + "::getConfigData[" + serviceName + "]:{}子节点发生变化，重新获取信息", watchedEvent.getPath());
                    }
                });
            } catch (KeeperException e) {
                logger.error(e.getMessage());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }

            if (childrens.size() == 0) {
                logger.info(getClass().getSimpleName() + "::getConfigData[" + serviceName + "]:no service instances found");
            }else{
                logger.info(getClass().getSimpleName() + "::getConfigData[" +serviceName + "], 获取{}的子节点成功", servicePath);
                //更新时清空 instances 信息
                runtimeInstanceList.clear();
                //child = 10.168.13.96:9085:1.0.0
                for (String children : childrens) {
                    String[] infos = children.split(":");
                    RuntimeInstance instance = new RuntimeInstance(serviceName, infos[0], Integer.valueOf(infos[1]), infos[2]);
                    runtimeInstanceList.add(instance);
                }
            }
            runInstancesMap.put(serviceName,runtimeInstanceList);
        }
        return configInfo;
    }

}
