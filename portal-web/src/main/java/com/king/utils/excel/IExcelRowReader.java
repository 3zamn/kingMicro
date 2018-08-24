package com.king.utils.excel;
import java.util.List;

/**
 * Excel读取处理接口
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月23日
 */
public interface IExcelRowReader {
    void getRows(int sheetIndex, int curRow,Boolean end, List<String> rowlist);
}