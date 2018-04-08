package com.github.dapeng.registry.zookeeper;

import com.github.dapeng.registry.ConfigKey;
import com.github.dapeng.registry.RuntimeInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年03月22日 上午10:21
 */
public class ZkConfigInfo {

    private ConcurrentMap<ConfigKey,String> configMap =new ConcurrentHashMap();
    private List<RuntimeInstance> runtimeInstanceList = new ArrayList<RuntimeInstance>();


/*    *//**
     * timeout zk config
     *//*
    public Config<Long> timeConfig = new Config<>();
    *//**
     * loadBalance zk config
     *//*
    public Config<LoadBalanceStrategy> loadbalanceConfig = new Config<>();


    public static class Config<T> {
        public T globalConfig;
        public Map<String, T> serviceConfigs = new HashMap<>();
        public Map<String, T> instanceConfigs = new HashMap<>();
    }*/

    public ConcurrentMap<ConfigKey, String> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(ConcurrentMap<ConfigKey, String> configMap) {
        this.configMap = configMap;
    }

    public List<RuntimeInstance> getRuntimeInstanceList() {
        return runtimeInstanceList;
    }

    public void setRuntimeInstanceList(List<RuntimeInstance> runtimeInstanceList) {
        this.runtimeInstanceList = runtimeInstanceList;
    }
}
