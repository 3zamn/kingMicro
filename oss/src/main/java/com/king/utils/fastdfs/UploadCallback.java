
package com.king.utils.fastdfs;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年2月28日
 */
public interface UploadCallback
{
	/**
	* send file content callback function, be called only once when the file uploaded
	* @param out output stream for writing file content
	* @return 0 success, return none zero(errno) if fail
	*/
	public int send(OutputStream out) throws IOException;
}
