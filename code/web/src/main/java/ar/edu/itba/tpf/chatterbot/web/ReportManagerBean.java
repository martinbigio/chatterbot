package ar.edu.itba.tpf.chatterbot.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.web.charts.ReportChart;

public class ReportManagerBean {
	
	private static ResourceBundle rb;

	{
	    rb =  ResourceBundle.getBundle("ar.edu.itba.tpf.chatterbot.ErrorMessages");
	}

	private boolean showChart = true;
	private Map<String, ReportChart> reports;
	private ReportChart currentChart;
	
	private IntervalCriteriaManager intervalCriteriaManager;
	
	public ReportManagerBean() {
		this.intervalCriteriaManager = new IntervalCriteriaManager();
	}
	
	public IntervalCriteriaManager getIntervalCriteriaManager() {
		return intervalCriteriaManager;
	}
	
	public boolean isShowChart() {
		return showChart;
	}
	
	public String updateDate() {
		if (intervalCriteriaManager.getFromDate().compareTo(intervalCriteriaManager.getToDate()) == 1) {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			FacesMessage facesMessage = new FacesMessage();
			facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			facesMessage.setDetail(rb.getString("ar.edu.itba.tpf.chatterbot.logs.errorDate"));
			facesMessage.setSummary(rb.getString("ar.edu.itba.tpf.chatterbot.logs.errorDate"));
			
			facesContext.addMessage("", facesMessage);
			this.showChart = false;
			return "error";
		} else {
			this.showChart = true;
			this.currentChart.reloadDataSet(intervalCriteriaManager.getIntervalCriteria());
			return "successful";
		}
		
	}
	
	public String changeReport() {
		FacesContext context = FacesContext.getCurrentInstance();
		String reportType = (String) context.getExternalContext().getRequestParameterMap().get("reportType");
		this.currentChart = this.reports.get(reportType);
		this.currentChart.reloadDataSet(intervalCriteriaManager.getIntervalCriteria());
		return "show_report";
	}

	public ReportChart getCurrentChart() {
		return this.currentChart;
	}

	@Required
	public void setReports(List<ReportChart> reportsList) {
		this.reports = new HashMap<String, ReportChart>();
		
		for (ReportChart reportChart : reportsList) {
			this.reports.put(reportChart.getChartName(), reportChart);
		}
	}
	
}
