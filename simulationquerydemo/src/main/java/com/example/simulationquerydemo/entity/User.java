package com.example.simulationquerydemo.entity;

import lombok.*;

/**
 *
 *
 * @ProgramName: simulationquerydemo
 * @ClassName: User
 * @Description: 实体demo
 * @Author: gm
 * @Date: 2021/3/3 0003
 * @Version: 1.0
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int chinese;
    private int english;
    private int math;
    private String name;
    private String sex;
}
