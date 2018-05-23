package com.king.utils.fastdht;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年2月28日
 */
public class ServerInfo
{
	protected InetSocketAddress address;
	protected Socket sock = null;
	
	public ServerInfo(InetSocketAddress address)
	{
		this.address = address;
	}
	
	public InetSocketAddress getAddress()
	{
		return this.address;
	}
	
	public Socket getSocket()
	{
		return this.sock;
	}
}
