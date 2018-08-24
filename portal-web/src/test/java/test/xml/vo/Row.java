package test.xml.vo;
import java.util.List;


import test.xml.base.XmlBase;



public class Row extends XmlBase{
	
    public List<Cell> cell;

	public List<Cell> getCell() {
		return cell;
	}

	public void setCell(List<Cell> cell) {
		this.cell = cell;
	}
	
	

}
