package com.example.simulationquerydemo.common;

public enum ConstantEnum {
    CONSTANT_THREE( 3);


//    private String name ;
    private Integer code ;

    ConstantEnum(Integer code) {
//        this.name = name;
        this.code = code;
    }

    public int getCode() {
        return code;
    }


}
