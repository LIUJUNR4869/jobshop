package easyopt.test;

import easyopt.chart.GantChart;
import easyopt.common.EasyArray;
import easyopt.common.EasyMath;
import easyopt.shopSch.Schedule;
import easyopt.shopSch.fsp.FSP;

import java.util.ArrayList;

/**
 * @author YLPJFR
 * @date 2022年06月13日 0:00
 */
public class NEH {
    public static void main(String[] args) {

    }

    public double[][] NEH(double[][] ptimes) {
        /**
         *根据想输入的作业工时 获取最优调度方案 二维数组cshedule[][],通过二维数组看出调度顺序
         * @author YPLFJR
         * @date 2022/6/13 0:17
         * @param ptimes 各个作业在各个工序上的的加工工时 行表示作业 列表表示机器
         * @return double[][] 详细调度方案
         * 第一列 作业编号
         * 第二列 设备编号
         * 第三列 工序编号
         * 第四列 开工时间
         * 第五列 完工时间
         *
         * SCHOptResult 类中 拥有十一列
         */
        //获得输入的参数并给一些变量赋值
        int jobQty = ptimes.length;//作业数量
        int machQty = ptimes[0].length;//过程数量
        double[][] Sch = new double[jobQty * machQty][5];

        //进行NEH的计算
        //step1 计算各个作业的总加工时间
        double[][] jobSumTimes = new double[jobQty][2];//第一列存储作业编号，第二列存储其总加工时间
        

        return Sch;
    }

}
