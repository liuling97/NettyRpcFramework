package liuling.rpcCommon.agreement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户端向服务端发送的请求。服务端需要接口的名字，和方法的名字，但是由于方法重载的缘故，
 * 我们还需要这个方法的所有参数的类型，最后，客户端调用时，还需要传递参数的实际值，那么服务端知道以上四个条件，
 * 就可以找到这个方法并且调用了。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 待调用接口名称
     */
    private String interfaceName;
    /**
     * 待调用方法名称
     */

    private String methodName;
    /**
     * 调用方法的参数
     */
    private Object[] parameters;
    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;
}
