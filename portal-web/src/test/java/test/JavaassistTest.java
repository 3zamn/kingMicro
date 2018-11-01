package test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * 利用java探针修改class文件或jar
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年10月31日
 */
public class JavaassistTest {
	
	@Test
    public  void changeMethode() {
        try {
            ///////////////////////////////////
            //  使用javaassist修改 class/jar 代码
            ///////////////////////////////////
            //  设置jar包路径
            ClassPool.getDefault().insertClassPath("C:\\Users\\Administrator\\Desktop\\test\\smp.jar");
 
            // 获取需要修改的类
            CtClass testJarClass = ClassPool.getDefault().getCtClass("com.king.SmpServer");
            // 获取类中的printTest方法
            CtMethod printTestMethod = testJarClass.getDeclaredMethod("main");
            // 修改该方法的内容
       //     printTestMethod.setBody("System.out.println(\"hello hhhhhhhhhhhhhhh\");");
            printTestMethod.insertBefore("System.out.println(\"hello hhhhhhhhhhhhhhh\");");
            // printTestMethod.insertAt(12, "System.out.println(\"hello ffff\");");
            ///////////////////////////////////
            //  使用反射测试输出,查看修改结果
            ///////////////////////////////////
            Class newTestJarClass = testJarClass.toClass();
            // 使用修改过的类创建对象
            Object newTestJar = newTestJarClass.newInstance();
            Method newPrintTestMethod = newTestJarClass.getDeclaredMethod("main");
            newPrintTestMethod.invoke(newTestJar);
 
            // 解除代码锁定,恢复可编辑状态
            testJarClass.defrost();
            // 写出到外存中
            testJarClass.writeFile();
           // testJarClass.writeFile("C:\\Users\\Administrator\\Desktop\\test");
 
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
