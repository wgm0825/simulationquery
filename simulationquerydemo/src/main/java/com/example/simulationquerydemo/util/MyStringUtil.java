package com.example.simulationquerydemo.util;

import java.util.List;

/**
 * 代码逻辑验证验证信息处理器
 *
 * @ProgramName: simulationquerydemo
 * @ClassName: MyStringUtil
 * @Description: 公共参数处理类
 * @Author: gm
 * @Date: 2021/3/5 0005
 * @Version: 1.0
 */
public class MyStringUtil {

    /**
     * 判断对象是否为空或者空串或者为null
     *
     * @param obj
     * @return Boolean
     */
    public static boolean isNullOrEmpty(Object obj){
        if (obj == null)
        {
            return true;
        }
        if ((obj instanceof List))
        {
            return ((List) obj).size() == 0;
        }
        if ((obj instanceof String))
        {
            return ((String) obj).trim().equals("");
        }
        return false;
    }

    /**
     * 判断对象不为空
     *
     * @param obj
     * @return Boolean
     */
    public static boolean isNotEmpty(Object obj){
        return !isNullOrEmpty(obj);
    }

}
