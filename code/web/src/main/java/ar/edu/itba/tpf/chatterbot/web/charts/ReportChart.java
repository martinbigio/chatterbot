package ar.edu.itba.tpf.chatterbot.web.charts;

import org.jfree.data.general.AbstractDataset;

import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;

public abstract class ReportChart {
	
	protected String chartType;
	protected String chartName;
	protected String xLabelName;
	protected String yLabelName;
	protected String chartDescription;
	protected Integer height;
	protected Integer width;
	
	public String getChartType() {
		return chartType;
	}
	
	public String getChartName() {
		return chartName;
	}
	
	public String getXLabelName() {
		return xLabelName;
	}
	
	public String getYLabelName() {
		return yLabelName;
	}
	
	public Integer getHeight() {
		return height;
	}
	
	public Integer getWidth() {
		return width;
	}
	
	public String getChartDescription() {
		return chartDescription;
	}
	
	public void reloadDataSet(IntervalCriteria intervalCriteria) {
		throw new RuntimeException("Falta implementar");
	}
	
	public abstract AbstractDataset getDataSet();
	

}
