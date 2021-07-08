package liuling.rpcCommon.exception;

public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }

}
