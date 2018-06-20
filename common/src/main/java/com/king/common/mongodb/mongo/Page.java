package com.king.common.mongodb.mongo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 自定义MongoDB分页，实现Pageable接口
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月19日
 */
public class Page implements Pageable{
	//每页记录数
	private int pageSize;
	//当前页数
	private int currPage;//从0开始
	private int offset;
	private Sort sort;
	@Override
	public int getPageNumber() {
		// TODO Auto-generated method stub
		return currPage;
	}

	@Override
	public int getPageSize() {
		// TODO Auto-generated method stub
		return pageSize;
	}

	@Override
	public int getOffset() {
		// TODO Auto-generated method stub
		return offset;
	}
	

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public Sort getSort() {
		// TODO Auto-generated method stub
		return sort;
	}

	@Override
	public Pageable next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pageable previousOrFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pageable first() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPrevious() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}
	

	
}
