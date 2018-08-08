package test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.king.dal.gen.service.BaseService;

public class Test {
	/**
	 * bean更新具体内容记录
	 * @param old_object
	 * @param new_object
	 * @return
	 */
	@SuppressWarnings({"rawtypes" })
	public <T> String record(Object new_object, BaseService service) {
		String result=null;
		try {
			Field[] fields = new_object.getClass().getDeclaredFields();
			Object old_object = null;
			for (Field field : fields) {
				field.setAccessible(true); // 设置些属性是可以访问的
				if (field.getAnnotation(Id.class) != null) {//主键Id
					old_object = service.queryObject(field.get(new_object));
					break;
				}
			}
			Field[] fs = old_object.getClass().getDeclaredFields();
			List<Object> list_old= new ArrayList<>();
			List<Object> list_new= new ArrayList<>();
			for (Field field : fs) {
				field.setAccessible(true); // 设置些属性是可以访问的
				Object val_old = field.get(old_object);// 得到此属性的修改前值
				Object val_new = field.get(new_object);// 得到此属性的修改后值
				if(val_old!=null&&val_new!=null&&!val_old.equals(val_new)){
					list_old.add(val_old);
					list_new.add(val_new);
				}			
			}
			if(!list_new.isEmpty() && !list_old.isEmpty()){
				result=list_old.toString()+"改为："+list_new.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
