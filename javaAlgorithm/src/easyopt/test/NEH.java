package easyopt.test;

import easyopt.chart.GantChart;
import easyopt.common.EasyArray;
import easyopt.common.EasyMath;
import easyopt.shopSch.Schedule;
import easyopt.shopSch.fsp.FSP;

import java.util.ArrayList;

/**
 * @author YLPJFR
 * @date 2022��06��13�� 0:00
 */
public class NEH {
    public static void main(String[] args) {

    }

    public double[][] NEH(double[][] ptimes) {
        /**
         *�������������ҵ��ʱ ��ȡ���ŵ��ȷ��� ��ά����cshedule[][],ͨ����ά���鿴������˳��
         * @author YPLFJR
         * @date 2022/6/13 0:17
         * @param ptimes ������ҵ�ڸ��������ϵĵļӹ���ʱ �б�ʾ��ҵ �б��ʾ����
         * @return double[][] ��ϸ���ȷ���
         * ��һ�� ��ҵ���
         * �ڶ��� �豸���
         * ������ ������
         * ������ ����ʱ��
         * ������ �깤ʱ��
         *
         * SCHOptResult ���� ӵ��ʮһ��
         */
        //�������Ĳ�������һЩ������ֵ
        int jobQty = ptimes.length;//��ҵ����
        int machQty = ptimes[0].length;//��������
        double[][] Sch = new double[jobQty * machQty][5];

        //����NEH�ļ���
        //step1 ���������ҵ���ܼӹ�ʱ��
        double[][] jobSumTimes = new double[jobQty][2];//��һ�д洢��ҵ��ţ��ڶ��д洢���ܼӹ�ʱ��
        

        return Sch;
    }

}
