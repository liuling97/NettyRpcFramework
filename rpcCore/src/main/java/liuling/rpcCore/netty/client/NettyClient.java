package liuling.rpcCore.netty.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import liuling.rpcCommon.agreement.RpcRequest;
import liuling.rpcCommon.agreement.RpcResponse;
import liuling.rpcCommon.exception.RpcError;
import liuling.rpcCommon.exception.RpcException;
import liuling.rpcCore.register.nacos.NacosServiceRegistry;
import liuling.rpcCore.register.nacos.ServiceRegister;
import liuling.rpcCore.serializer.CommonDecoder;
import liuling.rpcCore.serializer.CommonEncoder;
import liuling.rpcCore.serializer.CommonSerializer;
import liuling.rpcCore.serializer.KryoSerializer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


@Data
public class NettyClient implements RpcClient {


    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;
    private CommonSerializer serializer;
    private ServiceRegister serviceRegister;

    public NettyClient(Integer serializer){
        serviceRegister = new NacosServiceRegistry();
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    public NettyClient(){
        serviceRegister = new NacosServiceRegistry();
        this.serializer = CommonSerializer.getByCode(0);
    }

    /*
    * 在静态代码块中就直接配置好了 Netty 客户端，等待发送数据时启动
    * */
    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new CommonDecoder());
                        pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest){
        if(serializer == null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
//        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            InetSocketAddress inetSocketAddress = serviceRegister.lookupService(rpcRequest.getInterfaceName());
            ChannelFuture channelFuture = bootstrap.connect(inetSocketAddress.getAddress(), inetSocketAddress.getPort()).sync();
            logger.info("客户端连接到服务器{}:{}",inetSocketAddress.getAddress(),inetSocketAddress.getPort());
            Channel channel = channelFuture.channel();
            if(channel!=null){
                /*
                    channel 将 RpcRequest 对象写出，并且等待服务端返回的结果。
                    注意这里的发送是非阻塞的，所以发送后会立刻返回，而无法得到结果。
                    这里通过 AttributeKey 的方式阻塞获得返回结果：
                */
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息：%s",rpcRequest.toString()));
                    }else {
                        logger.error("发送消息时有错误发送: ",future1.cause());
                    }
                });
                channel.closeFuture().sync();
                /*
                    通过这种方式获得全局可见的返回结果，在获得返回结果 RpcResponse 后，
                    将这个对象以 key 为 rpcResponse 放入 ChannelHandlerContext 中，
                    这里就可以立刻获得结果并返回，我们会在 NettyClientHandler 中看到放入的过程。
                */
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        }catch (InterruptedException e){
            logger.error("发送消息时有错误发生: ", e);
        }
        return null;
    }
}
