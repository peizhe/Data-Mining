package JavaData;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DataOption {
	
	public ArrayList<Data> readDataFromCsv(String fileName)throws IOException{
		ArrayList<Data> list = new ArrayList<Data>();
		
		FileReader fr = new FileReader(new File(fileName)); 
        BufferedReader br = new BufferedReader(fr); 
        
        String line="";
        while((line=br.readLine())!=null){
        	String str[]=line.split(",");
        	if(str[0].equals("user_id")) continue;
            Data data = new Data(str[0],Integer.parseInt(str[1]),Integer.parseInt(str[2]),str[3]);
            list.add(data);
        }
		br.close();
		
		 CompareToData comparator=new CompareToData();
		 Collections.sort(list, comparator);
		 
		return list;
	}
	
	public int writeDataToCsv(ArrayList<Data> list,String fileName,String month1,String month2) throws IOException{
		FileWriter fw = new FileWriter(new File(fileName));
		BufferedWriter bw= new BufferedWriter(fw);
		int count=0;
		Iterator<Data> it=list.iterator();
		bw.write("user_id"+","+"brand_id"+","+"type"+","+"visit_datetime");
		while(it.hasNext()){
			Data data = it.next();
			SimpleDateFormat sdf = new SimpleDateFormat("MM‘¬dd»’");
			Date date = null,monthL=null,monthR=null;
			try {
				date =sdf.parse(data.getDate());
				monthL = sdf.parse(month1);
				monthR = sdf.parse(month2);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if((date.after(monthL)||date.equals(monthL))&&(date.before(monthR))){
				bw.newLine();
				bw.write(data.getUserId()+","+data.getBrandId()+","+data.getType()+","+data.getDate());
				count++;
			}	
		}
		
		bw.close();
		return count;
	}
	
	public int saveResult(ArrayList<User> users,String fileName) throws IOException{
		int count = 0;
		FileWriter fw = new FileWriter(new File(fileName));
		BufferedWriter bw= new BufferedWriter(fw);
		
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			if(!user.favourBrandSet.isEmpty()){
				count+=user.favourBrandSet.size();
				StringBuffer tmp= new StringBuffer();
				tmp.append(user.getUserId()+"\t");
				Iterator<Brand> it =user.favourBrandSet.iterator();
				if(it.hasNext()) tmp.append(it.next().brandId);
				while(it.hasNext()){
					tmp.append(","+it.next().brandId);
				}
				tmp.append("\n");
				
				bw.write(tmp.toString());
			}
		}
		bw.close();
		return count;
	}
	public Map<Integer,List<Integer>> ReadRelationMap(String fileName) throws NumberFormatException, IOException{
		Map<Integer,List<Integer>> map = new HashMap<Integer,List<Integer>>();
		
		FileReader fr = new FileReader(new File(fileName)); 
        BufferedReader br = new BufferedReader(fr); 
        
        String line="";
        while((line=br.readLine())!=null){
        	String str[]=line.split(" ");
        	List<Integer> ls = new ArrayList<Integer>();
        	int key = Integer.parseInt(str[0]);
        	int value = Integer.parseInt(str[1]);
        	ls.add(value);
            if(!map.containsKey(Integer.parseInt(str[0])))
            	map.put(key, ls);
            else{
            	map.get(key).addAll(ls);
            }
            //list.add(data);
        }
		br.close();
		
		return map;
	}
	
	public void writeObject(String outFile, Object object) throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(outFile)));
		out.writeObject(object);
		out.close();
	}

	public Object readObject(String filePath) throws FileNotFoundException, IOException {
		File inFile = new File(filePath);
		Object o = null;
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(inFile)));
		try {
			o = in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		in.close();
	return o;
	}
	
	public Map<Set<Integer>,Set<Integer>> reandApri(String fileName) throws FileNotFoundException, IOException{
		Map<Set<Integer>,Set<Integer>> map = new HashMap<Set<Integer>,Set<Integer>>();
		
		Map<Set<Integer>, Set<Set<Integer>>> reMap = new HashMap<Set<Integer>, Set<Set<Integer>>>();
		reMap = (Map<Set<Integer>, Set<Set<Integer>>>) readObject(fileName);
		
		Iterator it=reMap.entrySet().iterator(); 
		while(it.hasNext()){
			  Map.Entry entry = (Map.Entry) it.next(); 
			  Set<Integer> tmp = new HashSet<Integer>();
			  Set<Set<Integer>> tt = (Set<Set<Integer>>) entry.getValue();
			  Iterator<Set<Integer>> iter = tt.iterator();
			  while(iter.hasNext()){
				  tmp.addAll(iter.next());
			  }
			  map.put((Set<Integer>) entry.getKey(),tmp);
			
		}
		
		return map;
	}
	
	public int saveBuyed(ArrayList<User> users,String fileName) throws IOException{
		int count = 0;
		FileWriter fw = new FileWriter(new File(fileName));
		BufferedWriter bw= new BufferedWriter(fw);
		
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			if(!user.buyedBrand.isEmpty()){
				count++;
				StringBuffer tmp= new StringBuffer();
				tmp.append(user.getUserId()+"\t");
				Iterator<Brand> it =user.buyedBrand.iterator();
				if(it.hasNext()) tmp.append(it.next().brandId);
				while(it.hasNext()){
					tmp.append(","+it.next().brandId);
				}
				tmp.append("\n");
				
				bw.write(tmp.toString());
			}
		}
		bw.close();
		return count;
	}
	
	public ArrayList<User> readResult(String fileName) throws IOException{
		ArrayList<User> prUsers = new ArrayList<User>();

		FileReader fr = new FileReader(new File(fileName)); 
        BufferedReader br = new BufferedReader(fr); 
        
        String line="";
        while((line=br.readLine())!=null){
        	String str[]=line.split("\t|,");
        	User user = new User();
        	user.setUserId(str[0]);
        	int size = str.length;
        	for(int i=1;i<size;i++){
        		Brand b = new Brand();
        		b.brandId = Integer.parseInt(str[i]);
        		user.favourBrandSet.add(b);
        	}
        	prUsers.add(user);
        	
        }
		br.close();
		
		return prUsers;
	}
}
