package com.king.utils.excel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Excel2007版本及以上通过解析xml文件读取
 * 支持多个sheet
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年8月23日
 */
public class Excel2007Reader extends DefaultHandler {
	
	//工作簿
	private static final String WORK_SHEET = "worksheet";
    //列元素
    private static final String C_ELEMENT = "c";
    //列中属性r
    private static final String R_ATTR = "r";
    //列中的v元素
    private static final String V_ELEMENT = "v";
    //列中的t元素
    private static final String T_ELEMENT = "t";
    //列中属性值
    private static final String S_ATTR_VALUE = "s";
    //列中属性值
    private static final String T_ATTR_VALUE = "t";

    //sheet r:Id前缀
    private static final String RID_PREFIX = "rId";

    //行元素
    private static final String ROW_ELEMENT = "row";

    //时间格式化字符串
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //saxParser
    private static final String CLASS_SAXPARSER = "org.apache.xerces.parsers.SAXParser";

    //填充字符串
    private static final String CELL_FILL_STR = "@";
    //列的最大位数
    private static final int MAX_CELL_BIT = 3;


    //excel 2007 的共享字符串表,对应sharedString.xml
    private SharedStringsTable sharedStringsTable;

    private final DataFormatter dataFormatter = new DataFormatter();

    //当前行
    private int curRow;

    //当前列
    private int curCell;

    //上一次的内容
    private String lastContent;

    //是否是String类型的
    private boolean nextIsString;
    
    //worksheet结束标记
    private boolean end=false;

    //单元数据类型
    private CellDataType cellDataType;

    //当前列坐标， 如A1，B5
    private String curCoordinate;
    
    private Map<String, String> cellValue= new HashMap<>();

    //前一个列的坐标
    private String preCoordinate;

    //行的最大列坐标
    private String maxCellCoordinate;

    //单元格的格式表，对应style.xml
    private StylesTable stylesTable;

    //单元格存储格式的索引，对应style.xml中的numFmts元素的子元素索引
    private int numFmtIndex;
    //单元格存储的格式化字符串，nmtFmt的formateCode属性的值
    private String numFmtString;

    //sheet的索引
    private int sheetIndex = -1;
    
    //存储每行的列元素
    List<String> rowCellList = new ArrayList<String>();

    //单元格的数据格式
    enum CellDataType {
        BOOL("b"),
        ERROR("e"),
        FORMULA("str"),
        INLINESTR("inlineStr"),
        SSTINDEX("s"),
        NUMBER(""),
        DATE("m/d/yy"),
        NULL("");

        private String name;

        CellDataType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private IExcelRowReader rowReader;
    private int offset;

    public void setRowReader(int offset,IExcelRowReader rowReader) {
        this.rowReader = rowReader;
        this.offset=offset;
    }

    /**
     * 读取excel中的制定索引sheet
     *
     * @param path
     * @param sheetIndex
     * @throws OpenXML4JException
     * @throws IOException
     * @throws SAXException
     */
    public void load(String path, int sheetIndex) throws OpenXML4JException, IOException,
            SAXException {
        OPCPackage opcPackage = OPCPackage.open(path);
        XSSFReader xssfReader = new XSSFReader(opcPackage);
        //获取styleTable
        stylesTable = xssfReader.getStylesTable();
        //获取共享字符串表
        SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();
        this.sharedStringsTable = sharedStringsTable;
        //获取解析器
        XMLReader xmlReader = fetchSheetReader(sharedStringsTable);
        // 根据 rId# 或 rSheet# 查找sheet
        InputStream sheetInputStream = xssfReader.getSheet(RID_PREFIX + sheetIndex);
        InputSource sheetInputSource = new InputSource(sheetInputStream);
        xmlReader.parse(sheetInputSource);
        //关闭流
        closeStream(sheetInputStream);
    }

    /**
     * 遍历所有的sheet
     *
     * @param path
     * @throws IOException
     * @throws OpenXML4JException
     * @throws SAXException
     */
    public void load(File file) throws IOException, OpenXML4JException, SAXException {
        OPCPackage opcPackage = OPCPackage.open(file);
        XSSFReader xssfReader = new XSSFReader(opcPackage);
        stylesTable = xssfReader.getStylesTable();
        SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();
        this.sharedStringsTable = sharedStringsTable;
        XMLReader xmlReader = fetchSheetReader(sharedStringsTable);
        Iterator<InputStream> sheetsInputStream = xssfReader.getSheetsData();
        while (sheetsInputStream.hasNext()) {
            curRow = 0;
            sheetIndex++;
            InputStream sheetInputStream = sheetsInputStream.next();
            int size=sheetInputStream.available()/1000000;//M
            System.out.println(size+"M");
           InputSource sheetInputSource = new InputSource(sheetInputStream);
           xmlReader.parse(sheetInputSource);
            closeStream(sheetInputStream);
        }

    }

    /**
     * 获取sheet的解析器
     *
     * @param sharedStringsTable
     * @return
     * @throws SAXException
     */
    public XMLReader fetchSheetReader(SharedStringsTable sharedStringsTable) throws SAXException {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader(CLASS_SAXPARSER);
        xmlReader.setContentHandler(this);
        return xmlReader;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        //c 表示列
        if (C_ELEMENT.equals(qName)) {

            //获取当前列坐标
            String tempCurCoordinate = attributes.getValue(R_ATTR);
            //前一列为null，则将其设置为"@",A为第一列，ascii码为65，前一列即为@，ascii码64
            if (preCoordinate == null) {
                preCoordinate = CELL_FILL_STR;
            } else {
                //存在，则前一列要设置为上一列的坐标
                preCoordinate = curCoordinate;
            }
            //重置当前列
            curCoordinate = tempCurCoordinate;

            //设置单元格类型
            setCellType(attributes);
            //t 属性值
            String type = attributes.getValue(T_ATTR_VALUE);
            //s 表示该列为字符串
            if (S_ATTR_VALUE.equals(type)) {
                nextIsString = true;
            }
        }

        lastContent = "";
    }

    /**
     * 设置单元格的类型
     *
     * @param attribute
     */
    private void setCellType(Attributes attribute) {
        //默认是数字类型
        cellDataType = CellDataType.NUMBER;
        //重置numFmtIndex,numFmtString的值
        numFmtIndex = 0;
        numFmtString = "";

        //单元格的格式类型
        String cellType = attribute.getValue(T_ATTR_VALUE);
        //获取单元格的xf索引，对应style.xml中cellXfs的子元素xf
        String xfIndexStr = attribute.getValue(S_ATTR_VALUE);

        if (CellDataType.BOOL.getName().equals(cellType)) {
            cellDataType = CellDataType.BOOL;
        } else if (CellDataType.ERROR.getName().equals(cellType)) {
            cellDataType = CellDataType.ERROR;
        } else if (CellDataType.INLINESTR.getName().equals(cellType)) {
            cellDataType = CellDataType.INLINESTR;
        } else if (CellDataType.FORMULA.getName().equals(cellType)) {
            cellDataType = CellDataType.FORMULA;
        } else if (CellDataType.SSTINDEX.getName().equals(cellType)) {
            cellDataType = CellDataType.SSTINDEX;
        }

        if (xfIndexStr != null) {
            int xfIndex = Integer.parseInt(xfIndexStr);
            XSSFCellStyle xssfCellStyle = stylesTable.getStyleAt(xfIndex);
            numFmtIndex = xssfCellStyle.getDataFormat();
            numFmtString = xssfCellStyle.getDataFormatString();

            if (CellDataType.DATE.getName().equals(numFmtString)) {
                cellDataType = CellDataType.DATE;
                numFmtString = DATE_FORMAT;
            }

            if (numFmtString == null) {
                cellDataType = CellDataType.NULL;
                numFmtString = BuiltinFormats.getBuiltinFormat(numFmtIndex);
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String value = StringUtils.trim(lastContent);
        if (T_ELEMENT.equals(qName) || V_ELEMENT.equals(qName)) {
            rowCellList.add(curCell++, value);
            cellValue.put(curCoordinate, value);
        } else if (C_ELEMENT.equals(qName)) {
            value = getDataValue(value);
            //补全单元格之间的空格
            fillBlackCell(curCoordinate, preCoordinate, false);
            if(cellValue.get(curCoordinate)==null){
            	 rowCellList.add(curCell++, value);
            }
        } else {
            //如果是row标签，说明已经到了一行的结尾
            if (ROW_ELEMENT.equals(qName)) {
                //最大列坐标以第一行的为准
                if (curRow == 0) {
                    maxCellCoordinate = curCoordinate;
                }
                //补全一行尾部可能缺失的单元格
                if (maxCellCoordinate != null) {
                    fillBlackCell(maxCellCoordinate, curCoordinate, true);
                }
                if(curRow>=offset){//从第几行开始读
                	 rowReader.getRows(sheetIndex, curRow,end, rowCellList);
                }            
                //一行结束
                //清空rowCellList,
                rowCellList.clear();
                //清空
                cellValue.clear();
                //行数增加
                curRow++;
                //当前列置0
                curCell = 0;
                //置空当前列坐标和前一列坐标
                curCoordinate = null;
                preCoordinate = null;
            }else if(WORK_SHEET.equals(qName)){
            	end=true;//当前worksheet结束
            	rowReader.getRows(sheetIndex, curRow,end, rowCellList);
            	end=false;          	
            }
        }
    }

    /**
     * 填充空白单元格
     *
     * @param curCoordinate
     * @param preCoordinate
     */
    private void fillBlackCell(String curCoordinate, String preCoordinate, boolean isEnd) {
        if (!curCoordinate.equals(preCoordinate)) {
            int len = calNullCellCount(curCoordinate, preCoordinate, isEnd);
            for (int i = 0; i < len; i++) {
                rowCellList.add(curCell++, "");
            }
        }
    }

    /**
     * 计算当前单元格和前一个单元格之间的空白单元格数量
     * 如果是尾部则不减1
     *
     * @param curCoordinate
     * @param preCoordinate
     * @return
     */
    private int calNullCellCount(String curCoordinate, String preCoordinate, boolean isEnd) {
        // excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
        String curCellCoordinate = curCoordinate.replaceAll("\\d+", "");
        String preCellCoordinate = preCoordinate.replaceAll("\\d+", "");

        curCellCoordinate = fillChar(curCellCoordinate, MAX_CELL_BIT, CELL_FILL_STR);
        preCellCoordinate = fillChar(preCellCoordinate, MAX_CELL_BIT, CELL_FILL_STR);

        char[] cur = curCellCoordinate.toCharArray();
        char[] pre = preCellCoordinate.toCharArray();

        int len = (cur[0] - pre[0]) * 26 * 26 + (cur[1] - pre[1]) * 26 + (cur[2] - pre[2]);
        if (!isEnd) {
            len = len - 1;
        }
        return len;
    }

    /**
     * 将不足指定位数的字符串补全，高位补上指定字符串
     *
     * @param cellCoordinate
     * @param maxLen
     * @param fillChar
     * @return
     */
    private String fillChar(String cellCoordinate, int maxLen, String fillChar) {
        int coordinateLen = cellCoordinate.length();
        if (coordinateLen < maxLen) {
            for (int i = 0; i < (maxLen - coordinateLen); i++) {
                cellCoordinate = fillChar + cellCoordinate;
            }
        }
        return cellCoordinate;
    }

    private String getDataValue(String lastContent) {
        String value = "";
        XSSFRichTextString xssfRichTextString = null;
        switch (cellDataType) {
            case BOOL:
                char first = lastContent.charAt(0);
                value = first == '0' ? "FALSE" : "TRUE";
                break;
            case ERROR:
                value = "\"ERROR:" + lastContent + '"';
                break;
            case FORMULA:
                value = '"' + lastContent + '"';
                break;
            case INLINESTR:
                xssfRichTextString = new XSSFRichTextString(lastContent);
                value = xssfRichTextString.getString();
                xssfRichTextString = null;
                break;
            case SSTINDEX:
                try {
                    int index = Integer.parseInt(lastContent);
                    xssfRichTextString = new XSSFRichTextString(sharedStringsTable.getEntryAt
                            (index));
                    value = xssfRichTextString.getString();
                    xssfRichTextString = null;
                } catch (NumberFormatException e) {
                    value = lastContent;
                }
                break;
            case NUMBER:
                if (StringUtils.isNotBlank(numFmtString)) {
                    value = dataFormatter.formatRawCellContents(Double.parseDouble(lastContent),
                            numFmtIndex, numFmtString);
                } else {
                    value = lastContent;
                }
                value = value.replace("_", "");
                break;
            case DATE:
                value = dataFormatter.formatRawCellContents(Double.parseDouble(lastContent),
                        numFmtIndex, numFmtString);
                break;
            default:
                value = "";
                break;
        }

        return value;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // 得到单元格内容的值
        lastContent += new String(ch, start, length);
    }

    /**
     * 关闭流
     *
     * @param inputStream
     */
    public void closeStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        IExcelRowReader rowReader = new ExcelRowReader(null,null,null);//业务处理接口
        try {
        	 long startTime = System.currentTimeMillis();
            ExcelReaderUtil.read2007Excel(0,rowReader, new File("F://test.xlsx"));
            long stopTime = System.currentTimeMillis();
    		System.out.println("write xlsx file time: " + (stopTime - startTime)/1000 + "m");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}