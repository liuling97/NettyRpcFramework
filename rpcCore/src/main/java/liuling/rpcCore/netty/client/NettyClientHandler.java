package liuling.rpcCore.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import liuling.rpcCommon.agreement.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);


    /**
     * 这里只需要处理收到的消息，即 RpcResponse 对象，
     * 由于前面已经有解码器解码了，这里就直接将返回的结果放入 ctx 中即可
     * @param ctx ChannelHandlerContext对象
     * @param obj RpcResponse对象
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        RpcResponse msg = (RpcResponse) obj;
        try {
            logger.info(String.format("客户端接收到消息: %s", msg));
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(msg);
            ctx.channel().close();
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
