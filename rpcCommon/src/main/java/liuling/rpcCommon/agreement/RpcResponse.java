package liuling.rpcCommon.agreement;

import lombok.Data;

import java.io.Serializable;


/**
 * 服务器调用方法后，给客户端返回的信息
 *
 * @param <T> 返回的对象类型
 */
@Data
public class RpcResponse<T> implements Serializable {

    private Integer statusCode;

    private String message;

    private T data;

    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(new ResponseCode().SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code){
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }

}
