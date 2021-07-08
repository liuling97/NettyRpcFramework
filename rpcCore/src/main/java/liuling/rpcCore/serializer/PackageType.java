package liuling.rpcCore.serializer;

import lombok.Data;

@Data
public class PackageType {

    private int code;

    public static final PackageType REQUEST_PACK = new PackageType(1000);

    public static final PackageType RESPONSE_PACK = new PackageType(1001);

    private PackageType(int code){
        this.code = code;
    }
}
