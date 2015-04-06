package JavaData;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PredictMain {
	
	public static void main(String args[]) throws IOException{
		DataOption rd =new DataOption();
		ArrayList<Data> list = new ArrayList<Data>();
		//list=rd.readDataFromCsv("D:/TestData/ali_5.csv");
		list.addAll(rd.readDataFromCsv("D:/TestData/ali.csv"));
		//list.addAll(rd.readDataFromCsv("D:/TestData/ali_7.csv"));
		//list.addAll(rd.readDataFromCsv("D:/TestData/ali_8.csv"));
		//System.out.println(list.size());
		
		PredictAlgorithm pa = new PredictAlgorithm(1);
		pa.userStatic(list);
		pa.userCompute();
		//pa.userPredict(0.000,1.6);
		/*
		list=rd.readDataFromCsv("D:/TestData/ali.csv");
		PredictAlgorithm pp = new PredictAlgorithm(1);
		pp.userStatic(list);
		long begin = System.currentTimeMillis();
		Apriori apriori = new Apriori(0.25,0.7);
		apriori.initTrans(pp.users);
		apriori.first();
		apriori.loop(apriori.fth);
		*/
		Map<Integer,List<Integer>> confer=rd.ReadRelationMap("D:/TestData/FPmining/ali_out8.dat");
		//Map<Integer,List<Integer>> confer=apriori.output("D:/TestData/FPmining/ali.dat");
		//System.out.println("共耗时:" + ((System.currentTimeMillis()) - begin)+ "ms");
		
		pa.brandPredict(confer);
		
		Map<Set<Integer>,Set<Integer>> map = rd.reandApri("D:/TestData/FPmining/ali_out.apr");
		pa.ApriPredict(map);
		
		pa.brandToFavorate();
		
		ArrayList<User> ls = new ArrayList<User>();
		ls = rd.readResult("E:/datamining/xiaohao/re_single4_14.txt");
		ls.addAll(rd.readResult("D:/TestData/add.txt"));
		pa.Addition(ls);
		
		pa.userPredict(0.000,1);
		
		
		
		DataOption saveOp = new DataOption();
		int bc = saveOp.saveResult(pa.users, "D:/TestData/re.txt");
		System.out.println(bc);
		
		list =rd.readDataFromCsv("D:/TestData/ali.csv");
		PredictAlgorithm ac = new PredictAlgorithm(1);
		ac.userStatic(list);
		
		/*
		DataCheck dc = new DataCheck();
		int co=dc.reverseAnylise(pa.users, ac.users);
		System.out.println(co);
		*/
		
		double f,p,r;
		DataCheck dc = new DataCheck();
		p=dc.computePrecision(pa.users, ac.users);
		r=dc.computeRecall(pa.users, ac.users);
		f=dc.computeF();
		
		DecimalFormat df=new DecimalFormat("#0.000000");
		System.out.println(df.format(f)+"\t"+df.format(p)+"\t"+df.format(r));
		
		/*
		DataOption dw = new DataOption();
		int count = dw.writeDataToCsv(list, "D:/TestData/ali_5.csv","4月15日","5月15日");
		System.out.println(count);
		*/
		
	}
}
