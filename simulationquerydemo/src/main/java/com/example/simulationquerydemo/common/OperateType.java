package com.example.simulationquerydemo.common;

/**
 * 封装动态查询方式以及运算符
 */
public enum OperateType {

    startsWith ,//从开头匹配
    equals ,//等于
    contains ,//包含
    greater_than,//大于
    less_than, //小于
    AND, //与运算
    OR, //或运算
    ASC, // 正序
    DESC, // 倒序
    GROUP, //分组
    LIMIT //分页

}
