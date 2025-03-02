package com.cmcc.gray.loadbalancer;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.cmcc.gray.constant.GrayConstant;
import com.cmcc.gray.holder.GrayFlagRequestHolder;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class AbstractGrayLoadBalancerRule extends AbstractLoadBalancerRule {

    private static final String GRAY_TAG = "version";

    /**
     * 只有已启动且可访问的服务器，并对灰度标识进行判断
     */
    public List<Server> getReachableServers() {
        ILoadBalancer lb = getLoadBalancer();
        if (lb == null) {
            return new ArrayList<>();
        }
        List<Server> reachableServers = lb.getReachableServers();

        return getGrayServers(reachableServers);
    }

    /**
     * 所有已知的服务器，可访问和不可访问，并对灰度标识进行判断
     */
    public List<Server> getAllServers() {
        ILoadBalancer lb = getLoadBalancer();
        if (lb == null) {
            return new ArrayList<>();
        }
        List<Server> allServers = lb.getAllServers();
        return getGrayServers(allServers);
    }

    /**
     * 获取灰度版本服务列表
     */
    protected List<Server> getGrayServers(List<Server> servers) {
        List<Server> result = new ArrayList<>();
        String grayTag = GrayFlagRequestHolder.getGrayTag();
        if (servers == null) {
            return result;
        }
        //if (StringUtils.isEmpty(grayTag)) {
        //    grayTag = GrayConstant.PROD_TAG;
        //}
        List<Server> prodServers = new ArrayList<>();

        for (Server server : servers) {
            NacosServer nacosServer = (NacosServer) server;
            Map<String, String> metadata = nacosServer.getMetadata();
            String version = metadata.get(GRAY_TAG);
            // 判断服务metadata下的version是否于设置的请求版本一致
            if (version != null && version.equalsIgnoreCase(grayTag)) {
                result.add(server);
            }
            if (version == null || version.equalsIgnoreCase(GrayConstant.PROD_TAG)) {
                prodServers.add(server);
            }
        }
        if (StringUtils.isEmpty(grayTag) || CollectionUtils.isEmpty(result)) {
            return prodServers;
        } else {
            return result;
        }
    }
}
