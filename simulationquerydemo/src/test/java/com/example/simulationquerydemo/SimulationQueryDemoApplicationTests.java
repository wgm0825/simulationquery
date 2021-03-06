package com.example.simulationquerydemo;

import com.alibaba.fastjson.JSONObject;
import com.example.simulationquerydemo.btree.SimulationBtree;
import com.example.simulationquerydemo.common.ConstantEnum;
import com.example.simulationquerydemo.entity.SimulationPO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class SimulationQueryDemoApplicationTests {

    /**
     * 基于java实现B+Tree，这里用于测试通过数结构，模拟SQL查询效率问题（时间原因，暂时未实现）
     *
     */
    @Test
    void testBtree() {
        //初始化B+Tree类，这里的3表示设定B+Tree的高度，参照mysql数据库，设置为3层
//        SimulationBtree<Integer,SimulationPO> btree_int = new SimulationBtree<Integer,SimulationPO>(ConstantEnum.CONSTANT_THREE.getCode());
        SimulationBtree<String, SimulationPO> btree_str = new SimulationBtree<String, SimulationPO>(ConstantEnum.CONSTANT_THREE.getCode());
        System.out.println("千万数据录入查询耗时");
        long time1 = System.currentTimeMillis();
        //初始化录入全量数据
        for (int i = 0; i < 10000; i++) {
            SimulationPO simulationPO = new SimulationPO(i, "test", "查询" + i, i * 2, new Date());
//            btree_int.insert( simulationPO.getA(),simulationPO);
            btree_str.insert(simulationPO.getA().toString() + simulationPO.getC(), simulationPO);
        }
        long time2 = System.currentTimeMillis();
        SimulationPO p1 = btree_str.find("9999查询9999");

        long time3 = System.currentTimeMillis();

        System.out.println("全量数据录入耗时: " + (time2 - time1));
        System.out.println("查询耗时: " + (time3 - time2));
        System.out.println("插入数据：" + JSONObject.toJSONString(btree_str));
        System.out.println("查询结果：" + JSONObject.toJSONString(p1));
    }
}