package liuling.rpcCore.serializer;

import lombok.Data;

@Data
public class SerializerCode {

    private int code;

    public SerializerCode(int code){
        this.code = code;
    }

    public static SerializerCode valueOf(String str){
        if(str.equals("JSON")){
            return new SerializerCode(1);
        }
        if(str.equals("KRYO")){
            return new SerializerCode(0);
        }
        return null;
    }

}
