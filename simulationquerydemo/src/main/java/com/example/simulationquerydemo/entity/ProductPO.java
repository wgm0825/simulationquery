package com.example.simulationquerydemo.entity;

import lombok.*;

import java.util.Date;

/**
 * 模拟传入的全量数据结构
 *
 * @ProgramName: simulationquerydemo
 * @ClassName: ProductPO
 * @Description: 假定全量数据为商品信息表数据
 * @Author: gm
 * @Date: 2021/3/5 0005
 * @Version: 1.0
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductPO extends ParamPO{

    private Integer id;

    private String productName;

    private String productDesc;

    private Integer stockNum;

    private Date addTime;



}
