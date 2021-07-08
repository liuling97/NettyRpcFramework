package liuling.rpcCore.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import liuling.rpcCommon.agreement.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JSON 的序列化器
 */
public class JsonSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            Object obj = objectMapper.readValue(bytes,clazz);
            if(obj instanceof RpcRequest){
                obj = handlerRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    /*
        在 RpcRequest 反序列化时，由于其中有一个字段是 Object 数组，
        在反序列化时序列化器会根据字段类型进行反序列化，而 Object 就是一个十分模糊的类型，
        会出现反序列化失败的现象，
        这时就需要 RpcRequest 中的另一个字段 ParamTypes 来获取到 Object 数组中的每个实例的实际类，
        辅助反序列化，这就是 handleRequest() 方法的作用。
     */
    private Object handlerRequest(Object obj) throws IOException {

        RpcRequest rpcRequest = (RpcRequest)obj;
        for(int i = 0;i<rpcRequest.getParamTypes().length;i++){
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            /*
                isAssignableFrom()方法用于检查此Class对象所表示的类或接口是否与该类或接口相同，
                或者该Class对象是超类还是超接口。
            */
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())){
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes,clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
