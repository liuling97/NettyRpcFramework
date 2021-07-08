package liuling.rpcCore.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import liuling.rpcCommon.agreement.RpcRequest;
import liuling.rpcCommon.agreement.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 这里 Kryo 可能存在线程安全问题，文档上是推荐放在 ThreadLocal 里，
 * 一个线程一个 Kryo。在序列化时，先创建一个 Output 对象（Kryo 框架的概念），
 * 接着使用 writeObject 方法将对象写入 Output 中，最后调用 Output 对象的 toByte() 方法即可获得对象的字节数组。
 * 反序列化则是从 Input 对象中直接 readObject，这里只需要传入对象的类型，而不需要具体传入每一个属性的类型信息。
 */
public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        //支持对象循环引用（否则会栈溢出）
        kryo.setReferences(true);//默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
        //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
        kryo.setRegistrationRequired(false);//默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj)  {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (Exception e){
            logger.error("序列化时有错误发生:", e);
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            final Input input = new Input(byteArrayInputStream);
            final Kryo kryo = kryoThreadLocal.get();
            final Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        }catch (Exception e){
            logger.error("反序列化时有错误发生:", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
