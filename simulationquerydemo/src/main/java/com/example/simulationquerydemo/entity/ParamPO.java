package com.example.simulationquerydemo.entity;

import lombok.*;

import java.io.Serializable;

/**
 * 参数条件类型封装，主要用于判断操作类型
 *
 * @ProgramName: simulationquerydemo
 * @ClassName: ParamPO
 * @Description: 暂时只支持处理预设定的类型处理
 * @Author: gm
 * @Date: 2021/3/4 0004
 * @Version: 1.0
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParamPO implements Serializable {

    //操作类型（主要包括：AND,OR,ASC,DESC,GROUP,LIMIT）
    private  String operateType;

    //运算符名称（主要包括：equals,contains,startsWith）
    private  String operateName;

}
