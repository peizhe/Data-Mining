package JavaData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class Apriori {
	private int minNum;// 最小支持数
	private double minCon;// 最小置信度
	private double minDegree;
	private List<Set<Integer>> records;// 原始数据
	//private String output;// 输出路径
	private List<List<ItemSet>> result = new ArrayList<List<ItemSet>>();// 频繁项集结果
	public List<ItemSet> fth;// 频繁1项集

	public Apriori(double minDegree, double minCon) {
		//this.output = output;
		this.minCon = minCon;
		this.minDegree = minDegree;
		/*
		init(input);
		if (records.size() == 0) {
			System.err.println("不符合计算条件。退出！");
			System.exit(1);
		}
		minNum = (int) (minDegree * records.size());
		*/
	}
    
	public void initTrans(ArrayList<User> users){
		records = new ArrayList<Set<Integer>>();
		
		for(int i=0;i<users.size();i++){
			Set<Integer> record;
			User user = users.get(i);
			if(!user.buyedBrand.isEmpty()){
				record =new TreeSet<Integer>();
				Iterator<Brand> it = user.buyedBrand.iterator();
				while(it.hasNext()){
					record.add(it.next().brandId);
				}
				records.add(record);
			}
		}
		
		if (records.size() == 0) {
			System.err.println("不符合计算条件。退出！");
			System.exit(1);
		}
		minNum = (int) ((minDegree * records.size())>1?(int) (minDegree * records.size()):1);
		
	}
	
	
	private void init(String path) {
		// TODO Auto-generated method stub
		records = new ArrayList<Set<Integer>>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					new File(path)));

			String line = null;
			Set<Integer> record;
			while ((line = br.readLine()) != null) {
				if (!"".equals(line.trim())) {
					record = new TreeSet<Integer>();
					String[] items = line.split(" ");
					for (String item : items) {
						record.add(Integer.valueOf(item));
					}
					records.add(record);
				}
			}

			br.close();
		} catch (IOException e) {
			System.err.println("读取事务文件失败。");
		}
		
	}

	public void first() {
		// TODO Auto-generated method stub
		fth = new ArrayList<ItemSet>();
		Map<Integer, Integer> first = new HashMap<Integer, Integer>();
		for (Set<Integer> si : records)
			for (Integer i : si) {
				if (first.get(i) == null)
					first.put(i, 1);
				else
					first.put(i, first.get(i) + 1);
			}

		for (Integer i : first.keySet())
			if (first.get(i) >= minNum)
				fth.add(new ItemSet(i, first.get(i)));

	}

	public void loop(List<ItemSet> items) {
		// TODO Auto-generated method stub
		List<ItemSet> copy = new ArrayList<ItemSet>(items);
		List<ItemSet> res = new ArrayList<ItemSet>();
		int size = items.size();

		// 连接
		for (int i = 0; i < size; i++)
			for (int j = i + 1; j < size; j++)
				if (copy.get(i).isMerge(copy.get(j))) {
					ItemSet is = new ItemSet(copy.get(i));
					is.merge(copy.get(j).item.last());
					res.add(is);
				}
		// 剪枝
		pruning(copy, res);

		if (res.size() != 0) {
			result.add(res);
			loop(res);
		}
	}

	private void pruning(List<ItemSet> pre, List<ItemSet> res) {
		// TODO Auto-generated method stub
		// step 1 k项集的子集属于k-1项集
		Iterator<ItemSet> ir = res.iterator();
		while (ir.hasNext()) {
			// 获取所有k-1项子集
			ItemSet now = ir.next();
			Map<Integer, List<Integer>> ss = subSet(now);
			// 判断是否在pre集中
			boolean flag = false;
			for (List<Integer> li : ss.values()) {
				if (flag)
					break;
				for (ItemSet pis : pre) {
					if (pis.item.containsAll(li)) {
						flag = false;
						break;
					}
					flag = true;
				}
			}
			if (flag) {
				ir.remove();
				continue;
			}
			// step 2 支持度
			int i = 0;
			for (Set<Integer> sr : records) {
				if (sr.containsAll(now.item))
					i++;

				now.support = i;
			}
			if (now.support < minNum) {
				ir.remove();
				continue;
			}
			// 产生关联规则
			double deno = now.support;
			for (Map.Entry<Integer, List<Integer>> me : ss.entrySet()) {
				ItemCon ic = new ItemCon(me.getKey(), me.getValue());
				int nume = 0;

				for (ItemSet f : fth)
					if (f.item.contains(me.getKey())) {
						nume = f.support;
						break;
					}
				if (deno / nume > minCon) {
					now.calcon(ic);
					ic.setC1(deno / nume);
				}
				for (ItemSet pis : pre)
					if (pis.item.size() == me.getValue().size()
							&& pis.item.containsAll(me.getValue())) {
						nume = pis.support;
						break;
					}
				if (deno / nume > minCon)
					ic.setC2(deno / nume);
			}
		}
	}

	private Map<Integer, List<Integer>> subSet(ItemSet is) {
		// TODO Auto-generated method stub
		List<Integer> li = new ArrayList<Integer>(is.item);
		Map<Integer, List<Integer>> res = new HashMap<Integer, List<Integer>>();
		for (int i = 0, j = li.size(); i < j; i++) {
			List<Integer> _li = new ArrayList<Integer>(li);
			_li.remove(i);
			res.put(li.get(i), _li);
		}
		return res;
	}

	public Map<Integer,List<Integer>> output(String output) throws FileNotFoundException {
		Map<Integer,List<Integer>> confer =new HashMap<Integer,List<Integer>>();
		
		if (result.size() == 0) {
			System.err.println("无结果集。退出！");
			return confer;
		}
		FileOutputStream out = new FileOutputStream(output);
		PrintStream ps = new PrintStream(out);
		for (List<ItemSet> li : result) {
			//ps.println("=============频繁" + li.get(0).item.size()
					//+ "项集=============");
			//if(li.get(0).item.size()>1) continue;
			for (ItemSet is : li) {
				//ps.println(is.item + " : " + is.support);
				//ps.println();
				if (is.ics.size() != 0) {
					//ps.println("******关联规则******");
					for (ItemCon ic : is.ics) {
						if(!confer.containsKey(ic.i))
							confer.put(ic.i, ic.li);
						else{
							for(Integer d:ic.li){	
								if(!confer.get(ic.i).contains(d))
									confer.get(ic.i).add(d);
							}
						}
						ps.println(ic.i + " " + ic.li.get(0) + " con: "
								+ ic.confidence1);
						/*if (ic.confidence2 > minCon)
							ps.println(ic.li + " ---> " + ic.i + " con: "
									+ ic.confidence2);
						*/
					}
					
				}
			}
			break;
		}
		ps.close();
		return confer;
	}

	/*
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		long begin = System.currentTimeMillis();
		Apriori apriori = new Apriori(0.25, 0.5);
		// apriori.first();//频繁1项集
		apriori.first();
		apriori.loop(apriori.fth);

		apriori.output("D:/TestData/FPmining/ali_out.dat");
		System.out.println("共耗时:" + ((System.currentTimeMillis()) - begin)
				+ "ms");
	}
	*/
}

class ItemSet {
	TreeSet<Integer> item;
	int support;
	List<ItemCon> ics = new ArrayList<ItemCon>(); // 关联规则结果

	ItemSet(ItemSet is) {
		this.item = new TreeSet<Integer>(is.item);
	}

	ItemSet() {
		item = new TreeSet<Integer>();
	}

	ItemSet(int i, int v) {
		this();
		merge(i);
		setValue(v);
	}

	void setValue(int i) {
		this.support = i;
	}

	void merge(int i) {
		item.add(i);
	}

	void calcon(ItemCon ic) {
		ics.add(ic);
	}

	boolean isMerge(ItemSet other) {
		if (other == null || other.item.size() != item.size())
			return false;
		// 前k-1项相同，最后一项不同，满足连接条件
		/*
		 * Iterator<Integer> i = item.headSet(item.last()).iterator();
		 * Iterator<Integer> o =
		 * other.item.headSet(other.item.last()).iterator(); while (i.hasNext()
		 * && o.hasNext()) if (i.next() != o.next()) return false;
		 */
		Iterator<Integer> i = item.iterator();
		Iterator<Integer> o = other.item.iterator();
		int n = item.size();
		while (i.hasNext() && o.hasNext() && --n > 0)
			if (i.next() != o.next())
				return false;

		return !(item.last() == other.item.last());
	}
}

class ItemCon {
	Integer i;
	List<Integer> li;
	double confidence1;
	double confidence2;

	ItemCon(Integer i, List<Integer> li) {
		this.i = i;
		this.li = li;
	}

	void setC1(double c1) {
		this.confidence1 = c1;
	}

	void setC2(double c2) {
		this.confidence2 = c2;
	}
}

