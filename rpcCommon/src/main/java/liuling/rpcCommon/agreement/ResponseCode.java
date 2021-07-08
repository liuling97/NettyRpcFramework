package liuling.rpcCommon.agreement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCode {

    private Integer code;

    private String message;

    public static final ResponseCode SUCCESS = new ResponseCode(200,"SUCCESS");

    public static final ResponseCode METHOD_NOT_FOUND = new ResponseCode(301,"METHOD_NOT_FOUND");
}
