package com.example.simulationquerydemo.selector;

import com.example.simulationquerydemo.btree.SimulationBtree;
import com.example.simulationquerydemo.entity.ProductPO;
import com.example.simulationquerydemo.entity.SimulationPO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池方式加载数据，千万级数据加载速度基本数倍于常规遍历
 *
 * @ProgramName: simulationquerydemo
 * @ClassName: Test
 * @Description:
 * @Author: gm
 * @Date: 2021/3/5 0004
 * @Version: 1.0
 */
public class ThreadTask {
    public static void main(String[] args) {
        //开启线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(5));
        System.out.println("任务开启咯");
        for(int i=0;i<10;i++){
            MyTask myTask = new MyTask(i);
            executor.execute(myTask);
            System.out.println("线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+
            executor.getQueue().size()+"，已执行玩别的任务数目："+executor.getCompletedTaskCount());
        }

        executor.shutdown();
    }
}

class MyTask implements Runnable {
    private int taskNum;

    public MyTask(int num) {
        this.taskNum = num;
    }

    @Override
    public void run() {
        System.out.println("正在执行task " + taskNum);
        try {
            SimulationBtree<String, SimulationPO> btree1 = new SimulationBtree<String, SimulationPO>(3);

            List<ProductPO> dataList = new ArrayList<ProductPO>();
            String[] productNames = new String[]{"羽绒服","阔腿裤","T恤","大衣","短裤","篮球","足球","羽毛球","手机","电脑"};
            Random random = new Random();
            for(int i=0;i<1000000;i++){
                ProductPO  productPO = new ProductPO(i,productNames[random.nextInt(productNames.length)]+i/2,"备注"+i/5,1000000-i/3,new Date());
                dataList.add(productPO);
            }
            System.out.println("====================s数据长度："+dataList.size());
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("task " + taskNum + "执行完毕");
    }
}
