package liuling.rpcCore.register.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import liuling.rpcCommon.exception.RpcError;
import liuling.rpcCommon.exception.RpcException;
import liuling.rpcCommon.utils.NacosUtil;
import liuling.rpcCore.loadbalance.LoadBalancer;
import liuling.rpcCore.loadbalance.RandomLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Nacos 的使用很简单，通过 NamingFactory 创建 NamingService 连接 Nacos，
 * 连接的过程写在了静态代码块中，在类加载时自动连接。namingService 提供了两个很方便的接口，registerInstance 和 getAllInstances 方法，
 * 前者可以直接向 Nacos 注册服务，后者可以获得提供某个服务的所有提供者的列表。所以接口的这两个方法只需要包装一下就好了。
 */
public class NacosServiceRegistry implements ServiceRegister {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;
    private LoadBalancer loadBalancer;

    public NacosServiceRegistry(){
        this.loadBalancer = new RandomLoadBalancer();
    }

    public NacosServiceRegistry(LoadBalancer loadBalancer){
        if(loadBalancer == null) {
            this.loadBalancer = new RandomLoadBalancer();
        }else this.loadBalancer = loadBalancer;
    }

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress){
        try {
            namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }

    }

    /**
     * 在 lookupService 方法中，通过 getAllInstance 获取到某个服务的所有提供者列表后，
     * 需要选择一个，这里就涉及了负载均衡策略
     * @param serviceName 服务名称
     * @return
     */
    @Override
    public InetSocketAddress lookupService(String serviceName) {

        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
