package GeneticTSP;
//������������

/**
 * @author YLPJFR
 * @date 2022��05��30�� 13:46
 */

public class MainRun {
    public static void main(String[] args) {

        //�����Ŵ��㷨��������
        GeneticAlgorithm GA=new GeneticAlgorithm();

        //������ʼ��Ⱥ
        SpeciesPopulation speciesPopulation = new SpeciesPopulation();

        //��ʼ�Ŵ��㷨��ѡ�����ӡ��������ӡ��������ӣ�
        SpeciesIndividual bestRate=GA.run(speciesPopulation);

        //��ӡ·������̾���
        bestRate.printRate();
}}
