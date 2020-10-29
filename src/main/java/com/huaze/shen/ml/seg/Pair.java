package com.huaze.shen.ml.seg;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 动态规划寻找最佳路径时，记录最佳后驱节点以及对应的累积权重
 */
public class Pair {
    private int nextNode;
    private double weight;

    public Pair(int nextNode, double weight) {
        this.nextNode = nextNode; // 后驱节点
        this.weight = weight; // 累积权重
    }

    public int getNextNode() {
        return nextNode;
    }

    public void setNextNode(int nextNode) {
        this.nextNode = nextNode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
