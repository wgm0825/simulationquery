package com.example.simulationquerydemo.entity;

import java.util.Date;

/**
 * 自定义全量数据实体类(用于B+Tree实现)
 *
 * @ProgramName: simulationquerydemo
 * @ClassName: SimulationPO
 * @Description:
 * @Author: gm
 * @Date: 2021/3/3 0003
 * @Version: 1.0
 */
public class SimulationPO {
    public Integer A;
    public String B;
    public String C;
    public Integer D;
    public Date E;

    public SimulationPO(Integer a, String b, String c, Integer d, Date e) {
        A = a;
        B = b;
        C = c;
        D = d;
        E = e;
    }

    public Integer getA() {
        return A;
    }

    public void setA(Integer a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public Integer getD() {
        return D;
    }

    public void setD(Integer d) {
        D = d;
    }

    public Date getE() {
        return E;
    }

    public void setE(Date e) {
        E = e;
    }
}
