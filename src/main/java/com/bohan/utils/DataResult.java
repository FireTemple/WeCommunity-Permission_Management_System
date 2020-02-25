package com.bohan.utils;


import com.bohan.exception.code.BaseResponseCode;
import com.bohan.exception.code.ResponseCodeInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataResult<T> {

    /**
     * 前后端分离约定的交互JSON格式
     */

    @ApiModelProperty(value = "响应code，0为正常 其他为错误", name = "code")
    private int code = 0;


    @ApiModelProperty(value = "错误详情", name = "msg")
    private String msg;


    @ApiModelProperty(value = "数据", name = "data")
    private T data;

    public DataResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public DataResult(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public DataResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public DataResult() { this.code= BaseResponseCode.SUCCESS.getCode(); this.msg=BaseResponseCode.SUCCESS.getMsg(); this.data=null;
    }
    public DataResult(T data) {
        this.data = data; this.code=BaseResponseCode.SUCCESS.getCode(); this.msg=BaseResponseCode.SUCCESS.getMsg();
    }
    public DataResult(ResponseCodeInterface responseCodeInterface) { this.data = null;
        this.code = responseCodeInterface.getCode();
        this.msg = responseCodeInterface.getMsg();
    }
    public DataResult(ResponseCodeInterface responseCodeInterface, T data) { this.data = data;
        this.code = responseCodeInterface.getCode();
        this.msg = responseCodeInterface.getMsg();
    }

    public static <T>DataResult success(){
        return new <T>DataResult();
    }
    public static <T>DataResult success(T data){
        return new <T>DataResult(data);
    }

    public static <T>DataResult getResult(int code,String msg,T data){
        return new <T>DataResult(code,msg,data);
    }
    public static <T>DataResult getResult(int code,String msg){
        return new <T>DataResult(code,msg);
    }

    public static <T>DataResult getResult(BaseResponseCode responseCode){
        return new <T>DataResult(responseCode);
    }

    public static <T>DataResult getResult(BaseResponseCode responseCode, T data){
        return new <T>DataResult(responseCode,data);
    }
}
