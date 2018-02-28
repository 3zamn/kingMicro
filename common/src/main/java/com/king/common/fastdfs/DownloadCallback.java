
package com.king.common.fastdfs;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年2月28日
 */
public interface DownloadCallback
{
	/**
	* recv file content callback function, may be called more than once when the file downloaded
	* @param file_size file size
	*	@param data data buff
	* @param bytes data bytes
	* @return 0 success, return none zero(errno) if fail
	*/
	public int recv(long file_size, byte[] data, int bytes);
}
