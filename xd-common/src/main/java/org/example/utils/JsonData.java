package org.example.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.BizCodeEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonData {
    /**
     * status 0 means success，1 means pending，-1 means fail */
    private Integer code;
    /**
     * data
     */
    private Object data;
    /**
     * description
     */
    private String msg;
    /**
     * success
     * @return
     */

    /**
     * get feign call data
     * notice:
     *      support underline to camel
     * @param typeReference
     * @param <T>
     * @return
     */
    public <T> T getData(TypeReference<T> typeReference) {
        return JSON.parseObject(JSON.toJSONString(data), typeReference);
    }

    public static JsonData buildSuccess() {
        return new JsonData(0, null, null);
    }
    /**
     * succress with data
     * @param data
     * @return
     */
    public static JsonData buildSuccess(Object data) {
        return new JsonData(0, data, null);
    }
    /**
     * fail with msg
     * @param msg
     * @return
     */
    public static JsonData buildError(String msg) {
        return new JsonData(-1, null, msg);
    }
    /**
     * fail with custom code&msg
     * @param code
     * @param msg
     * @return
     */
    public static JsonData buildCodeAndMsg(int code, String msg) {
        return new JsonData(code, null, msg);
    }
    /**
     * pass enum，return msg
     * @param codeEnum
     * @return
     */
    public static JsonData buildResult(BizCodeEnum codeEnum){
        return JsonData.buildCodeAndMsg(codeEnum.getCode(),codeEnum.getMsg());
    }
}


