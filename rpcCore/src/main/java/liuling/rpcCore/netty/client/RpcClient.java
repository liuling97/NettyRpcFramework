package liuling.rpcCore.netty.client;

import liuling.rpcCommon.agreement.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);

}
