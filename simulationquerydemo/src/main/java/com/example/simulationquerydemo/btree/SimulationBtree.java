package com.example.simulationquerydemo.btree;

import com.example.simulationquerydemo.common.ConstantEnum;


/**
 * 思路：考虑到模仿sql查询问题，首先想到的是mysql底层的B+Tree数据结构。这里我们使用java代码模拟实现B+tree,因时间原因，功能未完全实现，且此处代码有参考网上实现。
 * 这里考虑到全量数据可能会有数据量过大的问题，在生成B+Tree数据结构后，考虑将树结构缓存到本地内存，再次加载时，通过加载内存中树结构中的数据，提升查询效率。
 * 经过本地测试，在不考虑初次数据加载生成B+Tree的时间情况下，单纯通过查询树结构
 *
 * TODO 未完成全部逻辑，待时间充裕后补充
 *
 * @ProgramName: simulationquerydemo
 * @ClassName: SimulationBtree
 * @Description: java模仿B+tree实现SQL查询
 * @Author: gm
 * @Date: 2021/3/4 0004
 * @Version: 1.0
 */
public class SimulationBtree<K extends Comparable<K>,T>   {

    //实现B+tree的高度
    private static Integer treeHeight;

    //设置非叶子节点最大的子节点数量（一般不超过树的高度）
    private  Integer maxNoLeafNodeNum;

    //用来生成树的中间节点类（节点父类）
    private ParentNode<K,T> parentNode;

    //叶子节点类
    private LeafNode<K,T> leafNode;

    //空构造方法，默认高度为3
    public SimulationBtree(){
        this(ConstantEnum.CONSTANT_THREE.getCode());
    }

    //构造方法，自定义B+Tree高度
    public SimulationBtree(Integer treeHeight) {
        this.treeHeight = treeHeight;
        //避免插入节点超过上限
        this.maxNoLeafNodeNum = treeHeight + 1;
        this.parentNode = new LeafNode<K, T>();
        this.leafNode = null;
    }

    //根据节点查询树节点
    public T find(K key){
        T t = this.parentNode.find(key);
        if(t == null){
            System.out.println("数据不存在");
        }
        return t;
    }

    //插入树节点
    public void insert(K key, T value){
        if(key == null) {
            return;
        }
        ParentNode<K, T> t = this.parentNode.insert(key, value);
        if(t != null){
            this.parentNode = t;
        }
        this.leafNode = this.parentNode.refreshLeft();
    }

    abstract class ParentNode<K extends Comparable<K>, T>{
        //父节点
        protected ParentNode<K, T> parent;
        //子节点数组
        protected ParentNode<K, T>[] children;
        //子节点数量
        protected Integer number;
        //节点对应的键数组
        protected Object keys[];

        //构造方法
        ParentNode(){
            this.keys = new Object[maxNoLeafNodeNum];
            this.children =  new ParentNode[maxNoLeafNodeNum];
            this.number = 0;
            this.parent = null;
        }

        //根据key查找对应树节点数据
        abstract T find(K key);

        //插入
        abstract ParentNode<K, T> insert(K key,T value);

        abstract LeafNode<K, T> refreshLeft();

    }

    class NonLeafNode<K extends Comparable<K>, T> extends ParentNode<K,T> {
        public NonLeafNode() {
            super();
        }

        /**
         * 递归查找,这里只是为了确定值究竟在哪一块,真正的查找到叶子节点才会查
         * @param key
         * @return
         */
        @Override
        T find(K key) {
            int i = 0;
            while(i < this.number){
                if(key.compareTo((K) this.keys[i]) <= 0)
                    break;
                i++;
            }
            if(this.number == i){
                return null;
            }
            return this.children[i].find(key);
        }

        /**
         * 递归插入节点数据，插入数据是会按照B+Tree的特点，插入生成排好序的树
         * 这里主要是获取到地址，插入数据需要到叶子结点类去实现
         *
         * @param value
         * @param key
         */
        @Override
        ParentNode<K, T> insert( K key ,T value) {
            int i = 0;
            while(i < this.number){
                if(key.compareTo((K) this.keys[i]) < 0)
                    break;
                i++;
            }
            if(key.compareTo((K) this.keys[this.number - 1]) >= 0) {
                i--;
            }
           /* if (Integer)key > 10) {
                System.out.println("监控中");
            }*/
            return this.children[i].insert(key, value);
        }

        @Override
        LeafNode<K, T> refreshLeft() {
            return this.children[0].refreshLeft();
        }


        /**
         * 当叶子节点插入成功完成分解时,递归地向父节点插入新的节点以保持平衡
         *
         * @param node1
         * @param node2
         * @param key
         */
        ParentNode<K, T> insertNode(ParentNode<K, T> node1, ParentNode<K, T> node2, K key){

            K oldKey = null;
            if(this.number > 0){
                oldKey = (K) this.keys[this.number - 1];
            }
            //如果原有key为null,说明这个非叶子节点是空的,直接放入两个节点即可
            if(key == null || this.number <= 0){
                this.keys[0] = node1.keys[node1.number - 1];
                this.keys[1] = node2.keys[node2.number - 1];
                this.children[0] = node1;
                this.children[1] = node2;
                this.number += 2;
                return this;
            }
            //原有节点不为空,则应该先寻找原有节点的位置,然后将新的节点插入到原有节点中
            int i = 0;
            while(key.compareTo((K)this.keys[i]) != 0){
                i++;
            }
            //左边节点的最大值可以直接插入,右边的要挪一挪再进行插入
            this.keys[i] = node1.keys[node1.number - 1];
            this.children[i] = node1;

            Object tempKeys[] = new Object[maxNoLeafNodeNum];
            Object tempChildren[] = new ParentNode[maxNoLeafNodeNum];

            System.arraycopy(this.keys, 0, tempKeys, 0, i + 1);
            System.arraycopy(this.children, 0, tempChildren, 0, i + 1);
            System.arraycopy(this.keys, i + 1, tempKeys, i + 2, this.number - i - 1);
            System.arraycopy(this.children, i + 1, tempChildren, i + 2, this.number - i - 1);
            tempKeys[i + 1] = node2.keys[node2.number - 1];
            tempChildren[i + 1] = node2;

            this.number++;

            //如果不需要拆分,把数组复制回去,直接返回
            if(this.number < treeHeight){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempChildren, 0, this.children, 0, this.number);
                return null;
            }
            //如果需要拆分,和拆叶子节点时类似,从中间拆开
            Integer middle = this.number / 2;
            //新建非叶子节点,作为拆分的右半部分
            NonLeafNode<K, T> parentNode = new NonLeafNode<K, T>();
            //非叶子节点更新父节点指针
            parentNode.number = this.number - middle;
            parentNode.parent = this.parent;
            //如果父节点为空,则新建一个非叶子节点作为父节点,并且让拆分成功的两个非叶子节点的指针指向父节点
            if(this.parent == null) {
                NonLeafNode<K, T> tempNonLeafNode = new NonLeafNode<>();
                parentNode.parent = tempNonLeafNode;
                this.parent = tempNonLeafNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, middle, parentNode.keys, 0, parentNode.number);
            System.arraycopy(tempChildren, middle, parentNode.children, 0, parentNode.number);
            for(int j = 0; j < parentNode.number; j++){
                parentNode.children[j].parent = parentNode;
            }
            //让原有非叶子节点作为左边节点
            this.number = middle;
            this.keys = new Object[maxNoLeafNodeNum];
            this.children = new ParentNode[maxNoLeafNodeNum];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempChildren, 0, this.children, 0, middle);

            //叶子节点拆分成功后,需要把新生成的节点插入父节点
            NonLeafNode<K, T> ParentNode = (NonLeafNode<K, T>)this.parent;
            return ParentNode.insertNode(this, parentNode, oldKey);
        }

    }
    //叶子节点类
    class LeafNode<K extends Comparable<K> ,T> extends ParentNode<K, T> {
        protected Object values[];
        protected LeafNode left;
        protected LeafNode right;

        public LeafNode(){
            super();
            this.values = new Object[maxNoLeafNodeNum];
            this.left = null;
            this.right = null;
        }

        /**
         * 进行查找,使用二分查找法
         *
         * @param key
         * @return
         */
        @Override
        T find(K key) {
            if(this.number <=0){
                return null;
            }
            Integer left = 0;
            Integer right = this.number;

            Integer middle = (left + right) / 2;

            while(left < right){
                K middleKey = (K) this.keys[middle];
                if(key.compareTo(middleKey) == 0)
                    return (T) this.values[middle];
                else if(key.compareTo(middleKey) < 0)
                    right = middle;
                else
                    left = middle;
                middle = (left + right) / 2;
            }
            return null;
        }

        /**
         * 叶子节点插入key
         *
         * @param value
         * @param key
         */
        @Override
        ParentNode<K, T> insert(K key, T value) {
            K oldKey = null;
            //已存在了节点，其父节点的key值
            if(this.number > 0){
                oldKey = (K) this.keys[this.number - 1];
            }
            //先插入数据
            int i = 0;
            //递归做排序，获取到当前应该存放节点的位置，保证生成的树是有序的
            while(i < this.number){
                if(key.compareTo((K)this.keys[i]) < 0)
                    break;
                i++;
            }

            //复制数组,完成添加
            Object tempKeys[] = new Object[maxNoLeafNodeNum];
            Object tempValues[] = new Object[maxNoLeafNodeNum];
            System.arraycopy(this.keys, 0, tempKeys, 0, i);
            System.arraycopy(this.values, 0, tempValues, 0, i);
            System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i);
            System.arraycopy(this.values, i, tempValues, i + 1, this.number - i);
            tempKeys[i] = key;
            tempValues[i] = value;

            this.number++;

            //判断是否需要拆分（每一个节点，最多有等同于树高度的子节点）
            if(this.number < treeHeight){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempValues, 0, this.values, 0, this.number);

                //有可能虽然没有节点分裂，但是实际上插入的值大于了原来的最大值，所以所有父节点的边界值都要进行更新
                ParentNode node = this;
                while (node.parent != null){
                    K tempkey = (K)node.keys[node.number - 1];
                    if(tempkey.compareTo((K)node.parent.keys[node.parent.number - 1]) > 0){
                        node.parent.keys[node.parent.number - 1] = tempkey;
                        node = node.parent;
                    } else {
                        break;
                    }
                }
                return null;
            }

            //如果需要拆分,则从中间把节点拆分差不多的两部分
            Integer middle = this.number / 2;

            //新建叶子节点,作为拆分的右半部分
            LeafNode<K, T> parentNode = new LeafNode<K, T>();
            parentNode.number = this.number - middle;
            parentNode.parent = this.parent;
            //如果父节点为空,则新建一个非叶子节点作为父节点,并且让拆分成功的两个叶子节点的指针指向父节点
            if(this.parent == null) {
                NonLeafNode<K, T> tempNonLeafNode = new NonLeafNode<>();
                parentNode.parent = tempNonLeafNode;
                this.parent = tempNonLeafNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, middle, parentNode.keys, 0, parentNode.number);
            System.arraycopy(tempValues, middle, parentNode.values, 0, parentNode.number);

            //让原有叶子节点作为拆分的左半部分
            this.number = middle;
            this.keys = new Object[maxNoLeafNodeNum];
            this.values = new Object[maxNoLeafNodeNum];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempValues, 0, this.values, 0, middle);

            this.right = parentNode;
            parentNode.left = this;

            //叶子节点拆分成功后,需要把新生成的节点插入父节点
            NonLeafNode<K, T> ParentNode = (NonLeafNode<K, T>)this.parent;
            return ParentNode.insertNode(this, parentNode, oldKey);
        }

        /**
         * 刷新返回叶子结点
         *
         * @return LeafNode<T, V>
         */
        @Override
        LeafNode<K, T> refreshLeft() {
            if(this.number <= 0){
                return null;
            }
            return this;
        }
    }
}