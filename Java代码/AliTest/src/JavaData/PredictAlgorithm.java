package JavaData;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class PredictAlgorithm {
	public ArrayList<User> users;
	public int behCouts;           //行为总数
	public int buyCouts;			//购买总数
	private double conduction;
	private Map<Integer,Integer> day;
	private	Map<Integer,Integer> brandBuyCouts;
	private  int conductDay;
	private double meanBeh;
	private double meanBuy;
	private double meanBrandBuyPro;
	public	int		lastMonthFirstDay=106;
	
	public PredictAlgorithm(double conduction) {
		users =new ArrayList<User>();
		behCouts 	= 0;
		this.conduction=conduction;
		day = new HashMap<Integer,Integer>();
		brandBuyCouts = new HashMap<Integer,Integer>();
		day.put(4, 0);
		day.put(5, 0);
		day.put(6, 1);
		day.put(7, 1);
		day.put(8, 2);
		conductDay = 20;
		this.buyCouts = 0;
		this.meanBeh = 0.0;
		this.meanBuy = 0.0;
		this.meanBrandBuyPro = 0.0;
	}

	public void userStatic(ArrayList<Data> list){
		Iterator<Data> itData = list.iterator();
		behCouts = list.size();
		int lastDay = 0;
		
		while(itData.hasNext()){
			Data data=itData.next();
			User user = new User();
			Brand brand = new Brand();
			
			brand.brandId= data.getBrandId();
			user.setUserId(data.getUserId());
			
			if(!users.contains(user))
				users.add(user);
			int index=users.indexOf(user);
			user=users.get(index);
			
			user.setRecords(user.getRecords()+1);
			user.month=data.getDate();
			
			String str[]=data.getDate().split("日|月");
			int curDay=(Integer.parseInt(str[0])-4)*30+day.get(Integer.parseInt(str[0]))+Integer.parseInt(str[1]);
			lastDay = curDay;
			
			if(!user.brands.contains(brand)){
				brand.relativeDay=curDay;
				user.brands.add(brand);
			}	
	
			int indBrand = user.brands.indexOf(brand);
			brand = user.brands.get(indBrand);
			brand.records++;
				
			
			if(curDay-brand.relativeDay>1){
				int cha = curDay-brand.relativeDay;   
				
				//if(cha>=conductDay)brand.score = 0;
				//cha = cha<=conductDay?cha:conductDay;         
				brand.score-=(double)cha*conduction/6;
				brand.relativeDay=curDay;               
				brand.continiousCouts=0;
			}
			else{
				brand.continiousCouts++;
				
			}
			switch(data.getType()){
			case 0:
				user.hitCouts++;
				brand.hitCouts++;
				brand.records++;
				brand.score+=brand.continiousCouts*1.1+1;
				break;
			case 1:
				if(!brandBuyCouts.containsKey(data.getBrandId()))
					brandBuyCouts.put(data.getBrandId(), 1);
				else
					brandBuyCouts.put(data.getBrandId(), brandBuyCouts.get(data.getBrandId())+1);
				buyCouts++;
				user.buyCouts++;
				brand.isShopped=true;
				brand.buyCouts++;
				brand.score+=1+brand.continiousCouts*1.1;
				user.buySet.add(brand.brandId);
				user.buyedBrand.add(brand);
				break;
			case 2:
				brand.isCollect=true;
				brand.score+=1+brand.continiousCouts*1.1;
				user.collections.add(brand.brandId);
				break;
			case 3:
				brand.isAddCat=true;
				brand.score+=1+brand.continiousCouts*1.3;
				user.buyCat.add(brand.brandId);
				break;
			default:
					break;
			}
			
		}
		afhandle(lastDay);
	}
	
	public void afhandle(int lastDay){
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			Iterator<Brand> it = user.brands.iterator();
			while(it.hasNext()){
				Brand brand = it.next();
				if(lastDay-brand.relativeDay>0){
					
					int cha = lastDay-brand.relativeDay;
					//cha = cha<=conductDay?cha:conductDay;
					brand.score-=(double)cha*conduction/1;
					
					//if(cha>=conductDay)brand.score = 0;
				}
				//System.out.println(lastDay+"\t"+brand.relativeDay+"\t"+brand.score);
			}
		}
	}
	
	public void userCompute(){
		
		int userCouts = users.size();
		for(int i=0;i<userCouts;i++){
			
			User user = users.get(i);
			user.behavRate = (double)user.getRecords()/behCouts;
			user.buyProbility = (double)user.buyCouts/user.getRecords();
			//user.buyProbility = (double)user.buyCouts/behCouts;
			
			//System.out.println(user.getUserId()+"\t"+user.buyedBrand.size());
			//Iterator<Brand> itBrand = user.buyedBrand.iterator();
			
		}
		double mean=0.0,meanb=0.0;
		
		for(int i=0;i<userCouts;i++){
			User user = users.get(i);
			mean += user.behavRate;
			meanb += user.buyProbility;
		}
		meanBeh=mean/userCouts;
		meanBuy = meanb/userCouts;
		
		//System.out.println(meanBeh+"\t"+meanBuy);
		for(int i=0;i<userCouts;i++){
			User user = users.get(i);
			if(user.behavRate>1.2*meanBeh && user.buyProbility<meanBuy/1.2)
				user.userType = 0;
			else if(user.behavRate<meanBeh/1.2 && user.buyProbility>meanBuy*1.2)
				user.userType = 2;
			else
				user.userType =1;
		}
		double meanBrB=0.0;
		Iterator it=brandBuyCouts.entrySet().iterator(); 
		while(it.hasNext()){
			  Map.Entry entry = (Map.Entry) it.next(); 
			meanBrB+=(double)(Integer)entry.getValue()/buyCouts;
			
		}
		meanBrandBuyPro = (double)meanBrB/brandBuyCouts.size();
	}
	
	public int userPredict(double userActive,double remCo){
		int userCouts = users.size();
		int count=0;
		for(int i=0;i<userCouts;i++){
			User user = users.get(i);
			DecimalFormat df=new DecimalFormat("#0.000000");
			
			for(int j=0;j<user.brands.size();j++){
				
				Brand brand = user.brands.get(j);
				
			//if(brand.score>=remCo || ((double)brand.buyCouts/brand.records>=0.5))
				if(brand.score*user.buyProbility>=remCo || brand.score>=12*remCo )
					user.favourBrandSet.add(user.brands.get(j));
				//System.out.println(user.getUserId()+"\t"+user.brands.get(j).brandId+"\t"+user.brands.get(j).score+"\t"+brand.score*user.buyProbility);

			}
			/*推荐收藏夹*/
			Iterator<Integer> it = user.collections.iterator();
			while(it.hasNext()){
				Brand b = new Brand();
				b.brandId = it.next();
				b.score = user.brands.get(user.brands.indexOf(b)).score;
				if(!user.buyedBrand.contains(b) && b.score>2*remCo) user.favourBrandSet.add(b);
			}
			/*推荐购物车*/
			
			Iterator<Integer> ite = user.buyCat.iterator();
			while(ite.hasNext()){
				Brand b = new Brand();
				b.brandId = ite.next();
				b.score = user.brands.get(user.brands.indexOf(b)).score;
				if(!user.buyedBrand.contains(b) && b.score>8*remCo) user.favourBrandSet.add(b);
			}
			
			
			if(user.behavRate>=userActive && !user.favourBrandSet.isEmpty()){
				user.isRecommand=true;
			}
		
			//System.out.println(user.getUserId()+"\t"+user.buyCouts+"\t"+df.format(user.behavRate)+"\t"+df.format(user.buyProbility)+"\t"+user.userType);
			
			if(user.isRecommand){	
				count++;
			}
			
			//System.out.println(user.getUserId()+"\t"+user.favourBrandSet.size()+"\t"+user.buyedBrand.size());
		}
		System.out.println(count);
		return count;
	}
	
	public void brandPredict(Map<Integer,List<Integer>> confer){
		
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			if(!user.buyedBrand.isEmpty()){
				Iterator<Brand> it = user.buyedBrand.iterator();
				while(it.hasNext()){
					//user.isRecommand=true;
					Brand b = it.next();
					if(confer.containsKey(b.brandId) && b.relativeDay>=lastMonthFirstDay){
						
					user.preBrands.addAll(confer.get(b.brandId));
					
					}
				}
			}
			
		}
	}
	
	public void brandToFavorate(){
		for(int i=0;i<users.size();i++){
			User user =users.get(i);
			//System.out.println("before:"+user.getUserId()+"\t"+user.favourBrandSet.size()+"\t"+user.buyedBrand.size());
			if(!user.preBrands.isEmpty()){
				Iterator<Integer> it = user.preBrands.iterator();
				while(it.hasNext()){
					Brand b=new Brand();
					b.brandId = it.next();
					if((double)brandBuyCouts.get(b.brandId)/buyCouts>2*meanBrandBuyPro)
						user.favourBrandSet.add(b);
				}
			}
			//if(!user.favourBrandSet.isEmpty())
				//user.isRecommand=true;
			//System.out.println("after:"+user.getUserId()+"\t"+user.favourBrandSet.size()+"\t"+user.buyedBrand.size());
		}
	}
	
	public void ApriPredict(Map<Set<Integer>,Set<Integer>> map){
		
		Iterator it=map.entrySet().iterator(); 
		while(it.hasNext()){
			  Map.Entry entry = (Map.Entry) it.next(); 
			  Set<Integer> key = (Set<Integer>) entry.getKey();
			  for(int i=0;i<users.size();i++){
				  User user = users.get(i);
				  if(user.buySet.containsAll(key))
					  user.preBrands.addAll((Set<Integer>) entry.getValue());
			  }
		}
	}
	
	public void Addition(ArrayList<User> us){
		for(int i = 0;i<us.size();i++){
			User u = us.get(i);
			if(users.contains(u)){
				int ind = users.indexOf(u);
				User user = users.get(ind);
				Iterator<Brand> it = u.favourBrandSet.iterator();
				while(it.hasNext()){
					Brand b = it.next();
					if(!user.favourBrandSet.contains(b)) user.favourBrandSet.add(b);
				}
			}
				
		}
	}
}
