package liuling.rpcCore.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import liuling.rpcCommon.agreement.RpcRequest;
import liuling.rpcCommon.agreement.RpcResponse;
import liuling.rpcCore.netty.RequestHandler;
import liuling.rpcCore.register.ServiceProvider;
import liuling.rpcCore.register.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * NettyServerHandler 和 NettyClientHandler 都分别位于服务器端和客户端责任链的尾部，直
 * 接和 RpcServer 对象或 RpcClient 对象打交道，而无需关心字节序列的情况。
 * NettyServerhandler 用于接收 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去。
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceProvider serviceProvider;

    static {
        requestHandler = new RequestHandler();
        serviceProvider = new ServiceProviderImpl();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try {
            logger.info("服务器收到请求：{}",msg);
            String interfaceName = msg.getInterfaceName();
            Object service = serviceProvider.getServiceProvider(interfaceName);
            Object result = requestHandler.handle(msg, service);
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("处理过程调用时有错误发送:");
        cause.printStackTrace();
        ctx.close();
    }
}
