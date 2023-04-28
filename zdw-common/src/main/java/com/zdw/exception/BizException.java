package com.zdw.exception;

import com.zdw.enums.BizCodeEnum;
import lombok.Data;


/**
 * 全局异常处理
 **/

@Data
public class BizException extends RuntimeException {

    private int code;
    private String msg;

    public BizException(int code, String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BizException(BizCodeEnum bizCodeEnum){
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.msg = bizCodeEnum.getMessage();
    }


}
