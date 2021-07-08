package liuling.rpcCore.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import liuling.rpcCommon.agreement.RpcRequest;


/*
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
首先是 4 字节魔数，表识一个协议包
Package Type，标明这是一个调用请求还是调用响应
Serializer Type 标明了实际数据使用的序列化器，这个服务端和客户端应当使用统一标准；
Data Length 就是实际数据的长度，设置这个字段主要防止粘包，最后就是经过序列化后的实际数据，
可能是 RpcRequest 也可能是 RpcResponse 经过序列化后的字节，取决于 Package Type。
*/


// CommonEncoder 继承了MessageToByteEncoder 类，见名知义，
// 就是把 Message（实际要发送的对象）转化成 Byte 数组。
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if(msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
