package JavaData;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class DataCheck {
	private double f;
	private double precision;
	private double recall;
	
	public DataCheck(){}

	public DataCheck(double f, double precision, double recall) {
		super();
		this.f = f;
		this.precision = precision;
		this.recall = recall;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}
	
	public int computeBrandUnion(Set<Brand> brands1,Set<Brand> brands2){
		int count = 0;
		Iterator<Brand> it1 = brands1.iterator();
		while(it1.hasNext()){
			Brand brand1=it1.next();
			Iterator<Brand> it2 = brands2.iterator();
			while(it2.hasNext()){
				Brand brand2 = it2.next();
				if(brand1.brandId==brand2.brandId)
					count++;
			}
		}
		return count;
	}
	public double computeF(){
		return this.f=2*precision*recall/(precision+recall);
	}
	
	public double computePrecision(ArrayList<User> preUsers,ArrayList<User> activeUsers){
		double p=0.0;
		int hitBrands=0;
		for(int i=0;i<preUsers.size();i++){
			User preUser = preUsers.get(i);
			if(preUser.isRecommand){
				if(activeUsers.contains(preUser)){
					int index=activeUsers.indexOf(preUser);
					hitBrands+=computeBrandUnion(preUser.favourBrandSet,activeUsers.get(index).buyedBrand);
				}
			}
		}
		
		int pBrands = 0;
		for(int i=0;i<preUsers.size();i++){
			User user = preUsers.get(i);
			if( user.isRecommand && !user.favourBrandSet.isEmpty()){
				pBrands+=user.favourBrandSet.size();
			}
			
		}
		
		if(pBrands!=0) p=(double)hitBrands/pBrands;
		
		this.precision=p;
		return p;
	}
	
	public double computeRecall(ArrayList<User> preUsers,ArrayList<User> activeUsers){
		double r=0.0;
		
		int hitBrands=0;
		for(int i=0;i<activeUsers.size();i++){
			User user = activeUsers.get(i);
			if(user.buyCouts>0 && preUsers.contains(user)){
				int index = preUsers.indexOf(user);
				if(preUsers.get(index).isRecommand){
					hitBrands+=computeBrandUnion(user.buyedBrand,preUsers.get(index).favourBrandSet);
				}
			}
		}
		
		int bBrands = 0;
		for(int i=0;i<activeUsers.size();i++){
			bBrands+=activeUsers.get(i).buyedBrand.size();
		}
		
		if(bBrands!=0) r=(double)hitBrands/bBrands;
		
		this.recall=r;
		return r;
	}
	
	public int reverseAnylise(ArrayList<User> preUsers,ArrayList<User> activeUsers){
		
		int count = 0;
		DecimalFormat df=new DecimalFormat("#0.000000");
		
		for(int i=0;i<activeUsers.size();i++){
			User user = activeUsers.get(i);
			if(user.buyCouts>0 && preUsers.contains(user)){
				int index=preUsers.indexOf(user);
				User puser = preUsers.get(index);
				
				Iterator<Brand> it =user.buyedBrand.iterator();
				while(it.hasNext()){
					Brand brand = it.next();
					if(puser.brands.contains(brand)){
						count++;
						Brand b = puser.brands.get(puser.brands.indexOf(brand));
						System.out.println(puser.getUserId()+"\t"+df.format(puser.behavRate)+"\t"+b.brandId+"\t"+b.score+"\t"+(double)b.hitCouts/user.getRecords()+"\t"+b.hitCouts+"\t"+b.buyCouts);
					}
					else
						System.out.println(puser.getUserId()+"\t"+df.format(puser.behavRate)+"\t"+brand.brandId+"\t"+"上月无记录");
				}
			}
		}
		return count;
	}
	
	public int PredictJudge(ArrayList<User> preUsers,ArrayList<User> activeUsers){
		int count=0;
		for(int i=0;i<preUsers.size();i++){
			User user = preUsers.get(i);
			if(!user.brands.isEmpty()){
				Iterator<Brand> it = user.brands.iterator();
				
				while(it.hasNext()){
					
					boolean u = activeUsers.contains(user);
					
					int index = activeUsers.indexOf(user);
					
					Brand brand = it.next();
					boolean buy=false;
					if(index>=0)
						buy = activeUsers.get(index).buyedBrand.contains(brand);
					
					boolean f = user.favourBrandSet.contains(brand);
					if(buy)count++;
					System.out.println(user.getUserId()+"\t"+brand.brandId+"\t"+brand.score+"\t"+brand.records+"\t"+brand.buyCouts+"\t"+buy+"\t"+f+"\t"+u);

				}
			}
		}
		return count;
	}
	
	public Set<User> KNN(ArrayList<User> users,User user,int k){
		Set<User> re = new HashSet<User>();
		
		class Tmp{
			public int couts;
			public User user;
		}
		
		TreeSet<Tmp> set=new TreeSet<Tmp>(new Comparator(){
			@Override
			public int compare(Object arg0, Object arg1) {
				Tmp t0 = (Tmp)arg0;
				Tmp t1 = (Tmp)arg1;
				return t0.couts-t1.couts;
			}
		});
		
		
		
		for(int i =0;i<users.size();i++){
			User usr = users.get(i);
			Tmp tmp = new Tmp();
			tmp.couts = computeBrandUnion(usr.buyedBrand,user.buyedBrand);  System.out.println(usr.buyedBrand.size()+"dd"+user.buyedBrand.size()+"----"+tmp.couts);
			tmp.user=usr;
			set.add(tmp);
		}
		 
		while(set.size()>k){
			Iterator<Tmp> it = set.iterator();
			Tmp t = it.next();
			set.remove(t);
		}
		
		Iterator<Tmp> it = set.iterator();
		while(it.hasNext()){
			Tmp u = it.next();
			System.out.println(u.couts);
			if(u.couts>1)
				re.add(u.user);
		}
		return re;
	}
	
}
