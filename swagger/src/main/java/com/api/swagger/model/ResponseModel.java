package com.api.swagger.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseModel {
    private int code;
    private String msg;

    public ResponseModel(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
