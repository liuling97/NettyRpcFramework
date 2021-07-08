package liuling.rpcClient;

import liuling.rpcCore.netty.client.NettyClient;
import liuling.rpcCore.netty.client.RpcClient;
import liuling.rpcCore.netty.client.RpcClientProxy;
import liuling.serverInterface.ByeService;
import liuling.serverInterface.HelloObject;
import liuling.serverInterface.HelloService;

public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(0);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        HelloObject object = new HelloObject(12,"This is a message");
        String res2 = byeService.bye("netty");
        String res = helloService.hello(object);
        System.out.println(res);
        System.out.println(res2);
    }
}
