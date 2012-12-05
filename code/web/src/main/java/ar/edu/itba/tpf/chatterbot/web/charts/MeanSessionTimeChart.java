package ar.edu.itba.tpf.chatterbot.web.charts;

import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.service.LoggingService;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;

public class MeanSessionTimeChart extends ReportChart {

    private LoggingService loggingService;
    private DefaultCategoryDataset dataset = null;

    @Required
    public void setLoggingService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }
    
    public MeanSessionTimeChart() {
        /* No tocar este nombre que se utiliza para identificar al gráfico. */
        this.chartName = "mean_session_time";
        this.chartType = "bar";
        this.xLabelName = " ";
        this.yLabelName = "cantidad de chats";
        this.chartDescription = "Reporte de tiempo promedio";
        this.width = 450;
        this.height = 300;
    }

    @Override
    public AbstractDataset getDataSet() {
        return dataset;
    }

    @Override
    public void reloadDataSet(IntervalCriteria intervalCriteria) {
        Map<String, Long> chatsPerDay = loggingService.getAverageChatTime(intervalCriteria);
        this.dataset = new DefaultCategoryDataset();

        if (chatsPerDay.values().size() == 0) {
        	this.dataset = null;
        	return;
        }
        
        for (String avgTime : chatsPerDay.keySet()) {
            this.dataset.addValue(chatsPerDay.get(avgTime), avgTime, "");
        }
    }
}
