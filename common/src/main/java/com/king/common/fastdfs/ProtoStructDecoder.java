
package com.king.common.fastdfs;

import java.io.IOException;
import java.lang.reflect.Array;


/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年2月28日
 * @param <T>
 */
public class ProtoStructDecoder<T extends StructBase>
{	
/**
* Constructor
*/
	public ProtoStructDecoder()
	{
	}
	
/**
* decode byte buffer
*/
	@SuppressWarnings("unchecked")
	public T[] decode(byte[] bs, Class<T> clazz, int fieldsTotalSize) throws Exception
	{
		if (bs.length % fieldsTotalSize != 0)
		{
			throw new IOException("byte array length: " + bs.length + " is invalid!");
		}
		
		int count = bs.length / fieldsTotalSize;
		int offset;
		T[] results = (T[])Array.newInstance(clazz, count);
		
		offset = 0;
		for (int i=0; i<results.length; i++)
		{
			results[i] = clazz.newInstance();
			results[i].setFields(bs, offset);
			offset += fieldsTotalSize;
		}
		
		return results;
	}
}
