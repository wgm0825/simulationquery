package com.example.simulationquerydemo.selector;

import com.alibaba.fastjson.JSONObject;
import com.example.simulationquerydemo.common.OperateType;
import com.example.simulationquerydemo.entity.ProductPO;
import com.example.simulationquerydemo.util.MyStringUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定与排序实现
 *
 * @ProgramName: simulationquerydemo
 * @ClassName: MySelector
 * @Description: 自定义排序实现
 * @Author: gm
 * @Date: 2021/3/5 0005
 * @Version: 1.0
 */
@Component
public class MySelector  {

    /**
     * 多条件对集合分组排序以及筛选  TODO 还需要对传入条件类做优化封装，以便实现成一个公共查询接口
     *
     * @param dataList 待筛选查询的数据集合（这里假设用商品信息模拟全量数据集合）
     * @param filterAndMap 筛选过滤”与“条件，key值传运算符（主要实现了以下几种：equals,contains,startsWith），value值传对应数据对象条件
     * @param filterOrMap 筛选过滤”或“条件，key值传运算符（主要实现了以下几种：equals,contains,startsWith），value值传对应数据对象条件
     * @param sortAndGroupMap 排序分组对象，key值传ASC,DESC,GROUP，value值传对应数据对象条件
     * @param limit 分页数量
     * @return
     */
    public String query(List<ProductPO> dataList, Map<String,ProductPO> filterAndMap,
                                 Map<String,ProductPO> filterOrMap,Map<String,ProductPO> sortAndGroupMap,int limit) {
        Stream<ProductPO> stream = dataList.stream();

        if(MyStringUtil.isNotEmpty(filterAndMap)){//进入与运算逻辑
            stream = this.makeFilterAnd(stream,filterAndMap);
        }

        if(MyStringUtil.isNotEmpty(filterAndMap)) {//进入或运算逻辑
            stream = this.makeFilterOr(stream,filterOrMap);
        }
        if (limit > 0) {//分页取值
            stream = stream.limit(limit);
        }
        List<ProductPO> returnList = new ArrayList<>();
        if(MyStringUtil.isNotEmpty(sortAndGroupMap)) {//进入分组排序逻辑
            stream = this.makeSort(stream,sortAndGroupMap);
            returnList = stream.collect(Collectors.toList());
            System.out.println("=============打印排序分页后的集合数据===========：" + JSONObject.toJSONString(returnList));
            if (sortAndGroupMap.containsKey(OperateType.GROUP.toString())) {//分组
                return this.makeGroup(returnList,sortAndGroupMap);
            } else {//没有分组信息，直接获取前面筛选排序的结果，否则返回分组后的数据
                return JSONObject.toJSONString(returnList);
            }
        } else {//没有进最后的分组排序逻辑，直接返回前面条件筛选的结果
            returnList = stream.collect(Collectors.toList());
            return JSONObject.toJSONString(returnList);
        }
    }

    /**
     * 获取与条件下的筛选数据
     *
     * @param stream Stream流
     * @param filterAndMap  筛选过滤”与“条件，key值传运算符（主要实现了以下几种：equals,contains,startsWith），value值传对应数据对象条件
     * @return
     */
    public Stream<ProductPO> makeFilterAnd(Stream<ProductPO> stream,Map<String,ProductPO> filterAndMap){
        if (filterAndMap.containsKey(OperateType.equals.toString())) {//判断传入条件是否包含等于运算符
            ProductPO product = filterAndMap.get(OperateType.equals.toString());
            if (MyStringUtil.isNotEmpty(product)) {
                Predicate<ProductPO> predicate = lsProductPO -> (MyStringUtil.isNotEmpty(product.getId()) ? lsProductPO.getId() == product.getId() : true)
                        && (MyStringUtil.isNotEmpty(product.getProductName()) ? lsProductPO.getProductName().equals(product.getProductName()) : true)
                        && (MyStringUtil.isNotEmpty(product.getProductDesc()) ? lsProductPO.getProductDesc().equals(product.getProductDesc()) : true)
                        && (MyStringUtil.isNotEmpty(product.getStockNum()) ? lsProductPO.getStockNum() == product.getStockNum() : true)
                        && (MyStringUtil.isNotEmpty(product.getAddTime()) ? lsProductPO.getAddTime().equals(product.getAddTime()) : true);
                stream = stream.filter(predicate);

            }
        }
        if (filterAndMap.containsKey("contains")) {//判断传入条件是否拥有包含运算符
            ProductPO product = filterAndMap.get("contains");
            if (MyStringUtil.isNotEmpty(product)) {
                Predicate<ProductPO> predicate = lsProductPO -> (MyStringUtil.isNotEmpty(product.getProductName()) ? lsProductPO.getProductName().contains(product.getProductName()) : true)
                        && (MyStringUtil.isNotEmpty(product.getProductDesc()) ? lsProductPO.getProductDesc().contains(product.getProductDesc()) : true);
                stream = stream.parallel().filter(predicate);
            }
        }
        if (filterAndMap.containsKey("startsWith")) {//判断传入条件是否拥有顺序匹配运算符
            ProductPO product = filterAndMap.get("startsWith");
            if (MyStringUtil.isNotEmpty(product)) {
                Predicate<ProductPO> predicate = lsProductPO -> (MyStringUtil.isNotEmpty(product.getProductName()) ? lsProductPO.getProductName().startsWith(product.getProductName()) : true)
                        && (MyStringUtil.isNotEmpty(product.getProductDesc()) ? lsProductPO.getProductDesc().startsWith(product.getProductDesc()) : true);
                stream = stream.parallel().filter(predicate);
            }
        }
        return stream;
    }

    /**
     * 条件或筛选数据
     *
     * @param stream Stream流
     * @param filterAndMap 筛选过滤”与“条件，key值传运算符（主要实现了以下几种：equals,contains,startsWith），value值传对应数据对象条件
     * @return
     */
    public Stream<ProductPO> makeFilterOr(Stream<ProductPO> stream,Map<String,ProductPO> filterAndMap){
        if (filterAndMap.containsKey(OperateType.equals.toString())) {//判断传入条件是否包含等于运算符
            ProductPO product = filterAndMap.get(OperateType.equals.toString());
            if (MyStringUtil.isNotEmpty(product)) {//等于条件或运算
                Predicate<ProductPO> predicate = orProductPO -> ((MyStringUtil.isNotEmpty(product.getId()) ? orProductPO.getId() == product.getId() : true)
                        || (MyStringUtil.isNotEmpty(product.getProductName()) ? orProductPO.getProductName().equals(product.getProductName()) : true)
                        || (MyStringUtil.isNotEmpty(product.getProductDesc()) ? orProductPO.getProductDesc().equals(product.getProductDesc()) : true)
                        || (MyStringUtil.isNotEmpty(product.getStockNum()) ? orProductPO.getStockNum() == product.getStockNum() : true)
                        || (MyStringUtil.isNotEmpty(product.getAddTime()) ? orProductPO.getAddTime().equals(product.getAddTime()) : true));
                stream = stream.filter(predicate);
            }
        }
        if (filterAndMap.containsKey(OperateType.contains.toString())) {//判断传入条件是否包含等于包含符
            ProductPO product = filterAndMap.get(OperateType.contains.toString());
            if (MyStringUtil.isNotEmpty(product)) {//包含条件或运算
                Predicate<ProductPO> predicate = orProductPO -> ((MyStringUtil.isNotEmpty(product.getProductName()) ? orProductPO.getProductName().contains(product.getProductName()) : true)
                        || (MyStringUtil.isNotEmpty(product.getProductDesc()) ? orProductPO.getProductDesc().contains(product.getProductDesc()) : true));
                stream = stream.filter(predicate);
            }
        }
        if (filterAndMap.containsKey(OperateType.startsWith.toString())) {//判断传入条件是否包含等于运算符
            ProductPO product = filterAndMap.get(OperateType.startsWith.toString());
            if (MyStringUtil.isNotEmpty(product)) {//等于条件或运算
                Predicate<ProductPO> predicate = orProductPO -> ((MyStringUtil.isNotEmpty(product.getProductName()) ? orProductPO.getProductName().startsWith(product.getProductName()) : true)
                        || (MyStringUtil.isNotEmpty(product.getProductDesc()) ? orProductPO.getProductDesc().startsWith(product.getProductDesc()) : true));
                stream = stream.filter(predicate);
            }
        }
        return stream;
    }

    /**
     * 排序方法实现
     *
     * @param stream Stream流
     * @param sortAndGroupMap 排序以及分组的遍历参数对象
     * @return
     */
    public Stream<ProductPO> makeSort(Stream<ProductPO> stream,Map<String,ProductPO> sortAndGroupMap){
        if (sortAndGroupMap.containsKey(OperateType.ASC.toString())) {////排序，升序
            ProductPO product = sortAndGroupMap.get(OperateType.ASC.toString());
            if (MyStringUtil.isNotEmpty(product)) {//升序排列
                if (MyStringUtil.isNotEmpty(product.getId()) || "id".equalsIgnoreCase(product.getOperateName())) {
                    stream = stream.sorted(Comparator.comparing(ProductPO::getId));
                }
                if (MyStringUtil.isNotEmpty(product.getStockNum()) || "stockNum".equalsIgnoreCase(product.getOperateName())) {
                    stream = stream.sorted(Comparator.comparing(ProductPO::getStockNum));
                }
                if (MyStringUtil.isNotEmpty(product.getAddTime()) || "addTime".equalsIgnoreCase(product.getOperateName())) {
                    stream = stream.sorted(Comparator.comparing(ProductPO::getAddTime));
                }

            }
        }
        if (sortAndGroupMap.containsKey(OperateType.DESC.toString())) {//排序，倒序
            ProductPO product = sortAndGroupMap.get(OperateType.DESC.toString());
            if (MyStringUtil.isNotEmpty(product)) {//升序排列
                if (MyStringUtil.isNotEmpty(product.getId())) {
                    stream = stream.sorted(Comparator.comparing(ProductPO::getId).reversed());
                }
                if (MyStringUtil.isNotEmpty(product.getStockNum())) {
                    stream = stream.sorted(Comparator.comparing(ProductPO::getStockNum).reversed());
                }
                if (MyStringUtil.isNotEmpty(product.getAddTime())) {
                    stream = stream.sorted(Comparator.comparing(ProductPO::getAddTime).reversed());
                }
            }
        }
        return stream;
    }

    /**
     * 排序方法实现
     *
     * @param returnList 筛选排序过后的返回值
     * @param sortAndGroupMap 排序以及分组的遍历参数对象
     * @return
     */
    public String makeGroup(List<ProductPO> returnList ,Map<String,ProductPO> sortAndGroupMap){
        ProductPO product = sortAndGroupMap.get(OperateType.GROUP.toString());
        Map<Object, List<ProductPO>> groupMap = new HashMap<>();
        Map<Object, Map<Object, List<ProductPO>>> map = new HashMap<>();
        if (MyStringUtil.isNotEmpty(product)) {//分组
            boolean isId = false;
            boolean isProductName = false;
            boolean isStockNum = false;
            boolean isAddTime = false;
            if (MyStringUtil.isNullOrEmpty(product.getId()) || "id".equalsIgnoreCase(product.getOperateName())) {
                groupMap = returnList.stream().collect(Collectors.groupingBy(t -> ((ProductPO) t).getId()));
                isId = true;
            }
            if (MyStringUtil.isNotEmpty(product.getProductName()) || "productName".equalsIgnoreCase(product.getOperateName())) {
                groupMap =  returnList.stream().collect(Collectors.groupingBy(t -> ((ProductPO) t).getProductName()));
                isProductName =true;
            }
            if (MyStringUtil.isNotEmpty(product.getStockNum()) || "stockNum".equalsIgnoreCase(product.getOperateName())) {
                groupMap=  returnList.stream().collect(Collectors.groupingBy(t -> ((ProductPO) t).getStockNum()));
                isStockNum = true;
            }
            if (MyStringUtil.isNotEmpty(product.getProductName()) || "addTime".equalsIgnoreCase(product.getOperateName())) {
                groupMap = returnList.stream().collect(Collectors.groupingBy(t -> ((ProductPO) t).getAddTime()));
                isAddTime = true;
            }
            if(isId&&isProductName){//多个条件组合排序
                map =  returnList.stream().collect(Collectors.groupingBy(t -> ((ProductPO) t).getId(), Collectors.groupingBy(t -> ((ProductPO) t).getProductName())));
            }
            if(isStockNum&&isProductName){//多个条件组合排序
                map =  returnList.stream().collect(Collectors.groupingBy(t -> ((ProductPO) t).getStockNum(), Collectors.groupingBy(t -> ((ProductPO) t).getProductName())));
            }

            if(isAddTime&&isProductName){//多个条件组合排序
                map =  returnList.stream().collect(Collectors.groupingBy(t -> ((ProductPO) t).getProductName(), Collectors.groupingBy(t -> ((ProductPO) t).getAddTime())));
            }

        }
        if(MyStringUtil.isNotEmpty(map)){
            return JSONObject.toJSONString(map);
        }
        return JSONObject.toJSONString(groupMap);
    }


    /**
     * 接口测试入口，涉及到与计算机的输入交互，没有放到测试类下面去放在测试内下
     * ，代码覆盖率测试跑不过去
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        MySelector mySelector = new MySelector();
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要生成的数据》》》》》》输入整数：");
        int data = sc.nextInt();
        List<ProductPO> dataList = new ArrayList<ProductPO>();
        String[] productNames = new String[]{"羽绒服","阔腿裤","T恤","大衣","短裤","篮球","足球","羽毛球","手机","电脑"};
        Random random = new Random();
        long loadDataTimeStart =System.currentTimeMillis();
        for(int i=0;i<data;i++){
            ProductPO  productPO = new ProductPO(i,productNames[random.nextInt(productNames.length)]+i/2,"备注"+i/5,data-i/3,new Date());
            dataList.add(productPO);
        }
        long loadDataTimeEnd =System.currentTimeMillis();
        System.out.println("系统数据加载时间为："+(loadDataTimeEnd-loadDataTimeStart));
        System.out.println("=============================数据录入成功,当前有数据："+data + "条");

        System.out.println("马上为您生成动态查询条件，请稍后：");
        //封装与运算条件
        ProductPO paramProduct = new ProductPO(null,productNames[random.nextInt(productNames.length)],"",null,null);
        Map<String,ProductPO> filterAndMap = new HashMap<>();
        paramProduct.setOperateType(OperateType.contains.toString());
        paramProduct.setOperateName(OperateType.contains.toString());
        filterAndMap.put(OperateType.contains.toString(),paramProduct);
        //封装或运算条件
        Map<String,ProductPO> filterOrMap = new HashMap<>();
        paramProduct = new ProductPO(random.nextInt(dataList.size()),null,"",random.nextInt(dataList.size()/5),null);
        paramProduct.setOperateType(OperateType.equals.toString());
        paramProduct.setOperateName(OperateType.equals.toString());
        filterOrMap.put(OperateType.equals.toString(),paramProduct);
        //封装分组排序条件
        Map<String,ProductPO> sortAndGroupMap = new HashMap<>();
        paramProduct = new ProductPO(null,null,null,random.nextInt(dataList.size()),null);
        paramProduct.setOperateType(OperateType.ASC.toString());
        paramProduct.setOperateName(OperateType.ASC.toString());
        sortAndGroupMap.put(OperateType.ASC.toString(),paramProduct);
        paramProduct = new ProductPO(null,"大衣",null,1,null);
        paramProduct.setOperateType(OperateType.GROUP.toString());
        paramProduct.setOperateName(OperateType.GROUP.toString());
        sortAndGroupMap.put(OperateType.GROUP.toString(),paramProduct);
        System.out.println("=================================查询数据准备完成，当前条件：filterAndMap---"+JSONObject.toJSONString(filterAndMap));
        System.out.println("=================================过滤或条件：filterOrMap----"+JSONObject.toJSONString(filterOrMap));
        System.out.println("=================================排序分组条件：sortAndGroupMap----"+JSONObject.toJSONString(sortAndGroupMap));
        System.out.println("=========================================================================================");
        System.out.println("===========================请输入想控制的页数limit，必须输入哦！赶快输入——————————》》》》》：");
        int limit = sc.nextInt();
        System.out.println("========================参数录入完毕啦，开始请求接口了===================================");
        long currentTimeMillisStart = System.currentTimeMillis();
        String result = mySelector.query(dataList,filterAndMap,filterOrMap,sortAndGroupMap,limit);
        long currentTimeMillisEnd = System.currentTimeMillis();
        System.out.println("--------------------------------》》》》请求一共耗时：" + (currentTimeMillisEnd-currentTimeMillisStart));
        System.out.println("==============================>>>>>>>>>>>>>最后筛选结果："+result);
    }
}
