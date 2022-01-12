package com.wgcloud.dto;

import com.wgcloud.entity.BaseEntity;

/**
 * @version v2.3
 * @ClassName:MessageDto.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: 表单提交返回信息
 *
 */
public class MessageDto extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -2913111613773445949L;


    private String code;

    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
