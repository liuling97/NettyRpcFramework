package liuling.rpcCore.netty.client;

import liuling.rpcCommon.agreement.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 由于在客户端侧没有接口的具体实现类，就没有办法生成实例对象。
 * 这时，我们可以通过动态代理的方式生成实例，并且调用方法时生成需要的RpcRequest对象并且发送给服务端。
 */
public class RpcClientProxy implements InvocationHandler {


    /**
     * 我们需要传递host和port来指明服务端的位置。并且使用getProxy()方法来生成代理对象。
     */
    private RpcClient client;

    public RpcClientProxy(RpcClient client){
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }


    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        return client.sendRequest(rpcRequest);
    }
}
