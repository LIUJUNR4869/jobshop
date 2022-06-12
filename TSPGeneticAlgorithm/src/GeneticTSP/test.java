package GeneticTSP;
import java.util.Random;
/**
 * @author YLPJFR
 * @date 2022年05月30日 15:48
 */
public class test {
    public static void main(String[] args) {
        String[] gene = new String[TSPData.CITY_NUM];
        for (int i = 0; i < gene.length; i++) {
            gene[i] = Integer.toString(i + 1);
            System.out.print(gene[i] + "\t" );
        }
        System.out.println("");
        Random rands = new Random();

        for (int j = 0; j < gene.length; j++) {
            int num = j + rands.nextInt(gene.length - j);
            //System.out.print(num + "\t");
            //交换
            String tmp;
            tmp = gene[num];
            gene[num] = gene[j];
            gene[j] = tmp;
            System.out.print(gene[j] + "\t" );
        }


    }
}
