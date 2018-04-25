package tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Prototype implements Cloneable{

    private String name="aa";
    private int age=11;
    private ArrayList<String> arrayList = new ArrayList<String>();
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	//浅克隆
	public Object clone1()   {
		Object object =null;
        try {
        	object= super.clone();
        	
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return object;
    }
	 /* 深复制 */  
    public Object deepClone() throws IOException, ClassNotFoundException {  
  
        /* 写入当前对象的二进制流 */  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
        ObjectOutputStream oos = new ObjectOutputStream(bos);  
        oos.writeObject(this);  
  
        /* 读出二进制流产生的新对象 */  
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());  
        ObjectInputStream ois = new ObjectInputStream(bis);  
        return ois.readObject();  
    }  

    public static void main ( String[] args){
        Prototype pro = new Prototype();
      
        Prototype pro1 = (Prototype)pro.clone1();
        System.out.println(pro1);
    }
}