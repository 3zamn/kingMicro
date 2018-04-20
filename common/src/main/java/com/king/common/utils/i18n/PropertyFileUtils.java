package com.king.common.utils.i18n;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Properties;

import com.king.common.utils.pattern.StringToolkit;


public class PropertyFileUtils {
  private static final String ENCODE = "utf-8";

  Properties properties = new Properties();

  private String filepath;

  public PropertyFileUtils( String filepath ) throws IOException {
    if ( !StringToolkit.isValuedString(filepath) )
      throw new IOException("读文件失败:Properties文件路径错误");

    this.filepath = filepath;
    try {
      FileInputStream input = new FileInputStream(this.filepath);
      this.properties.load( input );
      input.close();
    } catch (IOException e) {
      this.properties = null;
      throw e;
    }
  }
  
  public PropertyFileUtils(URL url)throws IOException{
	  InputStream input = null;
	  try{
		  input = url.openStream();
	      this.properties.load( input );
	  }catch(Exception e){
		  throw new IOException(e.getMessage());
	  }finally{
		  try{
			  if(input != null){
				  input.close();
			  }
		  }catch(IOException e){
			  
		  }
	  }
  }

  public PropertyFileUtils( Properties properties ) {
    if ( properties!=null )
      this.properties = properties;
  }
  
  public long getModifyTime(){
	  return new File(filepath).lastModified();
  }
  
  public void reload()throws Exception{
	  try {
	      FileInputStream input = new FileInputStream(this.filepath);
	      this.properties.load( input );
	      input.close();
	    } catch (IOException e) {
	      this.properties = null;
	      throw e;
	    }
  }

  /**
   * 获取值
   * @param key String
   * @param defaultValule String
   * @return String
   */
  public String getPropertiesValue(String key, String defaultValule) {
    if (this.properties==null)
      return defaultValule;

    String value = this.properties.getProperty(key);
    value = StringToolkit.ISOtoGB(value);
    if (!StringToolkit.isValuedString(value)) {
      value = defaultValule;
    }
    return value;
  }

  public void setPropertiesValue( String key, String value ) {
    if ( this.properties==null )
      return;

    //value = StringToolkit.GBtoISO( value );

    this.properties.setProperty( key, value );
  }

  /**
   * 获取properties,不会为null
   * @return Properties
   */
  public Properties getProperties() {
    return this.properties;
  }

  /**
   * 保存文件
   * @throws Exception
   */
  public void save( String filepath, String description ) throws Exception {
    FileOutputStream output = new FileOutputStream(this.filepath);
    this.properties.store( output, description );
    output.close();
  }

  /**
   * 保存文件
   * @throws Exception
   */
  public void save( String filepath ) throws Exception {
    this.save( filepath, "" );
  }

  /**
   * 保存文件
   * @throws Exception
   */
  public void save() throws Exception {
    if ( !StringToolkit.isValuedString(this.filepath) ) {
      throw new Exception("写文件失败:Properties文件路径错误");
    }

    this.save( this.filepath );
  }

  /**
   * 获取文件路径
   * @return String
   */
  public String getFilePath() {
    return this.filepath;
  }

  /**
   * 更新指定properties文件的指定项
   * @param filepath String
   * @param key String
   * @param value String
   * @throws IOException
   * @return boolean true:成功更新  false,不存在指定的项
   */
  public static boolean updatePropertyValue( String filepath, String key, String value ) throws IOException {
    if ( !StringToolkit.isValuedString(filepath) )
      throw new IOException( "文件路径为空" );

    boolean isUpdate = false;

    BufferedReader input = null;
    BufferedWriter output = null;
    String line = null;
    StringBuffer buffer = new StringBuffer();
    try {
      //读
      input = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
      while ( (line=input.readLine())!=null ) {
        if ( !line.startsWith("#") ) { //不是注解
          String[] pair = line.split("=");
          if (pair != null && pair.length > 0) { //读到数据
            if ( pair[0].equals(key) ) {
              //value = StringToolkit.GBtoISO( value );
              line = key+"=" + value;
              isUpdate = true; //是更新ZipUtils.java
            }
          }
        }
        buffer.append( line + "\r\n" );//换行
      }


      //写
      output = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(filepath)));
      output.write(buffer.toString());
      output.flush();
      output.close();
      output = null;
    } finally {
      if ( input!=null ) input.close();
      if ( output!=null ) output.close();
    }
    return isUpdate;
  }
}
