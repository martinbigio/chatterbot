package ar.edu.itba.tpf.chatterbot.web.charts;

import java.util.Map;

import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.service.LoggingService;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;

public class MostTraversedPathsChart extends ReportChart {

    private DefaultPieDataset pieDataSet = null;
    private LoggingService loggingService;
    
    @Required
    public void setLoggingService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }
    
    public MostTraversedPathsChart() {
        /* No tocar este nombre que se utiliza para identificar al gráfico. */
        this.chartName = "most_traversed_paths";
        this.chartType = "pie";
        this.xLabelName = " ";
        this.yLabelName = " ";
        this.chartDescription = "Reporte de caminos más recorridos";
        this.width = 450;
        this.height = 300;
    }

    @Override
    public AbstractDataset getDataSet() {
        return pieDataSet;
    }

    @Override
    public void reloadDataSet(IntervalCriteria intervalCriteria) {
    	pieDataSet = new DefaultPieDataset();
        Map<String, Long> results = loggingService.getMostVisitedNodes(intervalCriteria);
        pieDataSet = new DefaultPieDataset();
        
        if (results.values().size() == 0) {
        	pieDataSet = null;
        	return;
        }
        
        long totalValue = 0;
        
        for (String s : results.keySet()) {
        	if (s != null) {
                totalValue += results.get(s);
        	} 
        }
        
        if (totalValue == 0) {
        	return;
        }
        
        for (String s : results.keySet()) {
        	if (s != null) {
                pieDataSet.setValue(s + "(" + (results.get(s) * 100/ totalValue) + "%)", results.get(s));
        	} 
        }
    }

}
