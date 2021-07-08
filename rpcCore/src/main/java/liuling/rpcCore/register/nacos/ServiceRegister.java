package liuling.rpcCore.register.nacos;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress inetSocketAddress);
    InetSocketAddress lookupService(String serviceName);
}
