package liuling.rpcServer;


import liuling.rpcCore.annotation.ServiceScan;
import liuling.rpcCore.netty.server.NettyServer;

@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.start();
    }
}
