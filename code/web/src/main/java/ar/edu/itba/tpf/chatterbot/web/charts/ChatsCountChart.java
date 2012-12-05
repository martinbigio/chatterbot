package ar.edu.itba.tpf.chatterbot.web.charts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.service.LoggingService;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;

public class ChatsCountChart extends ReportChart {

    private LoggingService loggingService;
    private DefaultCategoryDataset dataset = null;

    public ChatsCountChart() {
        /* No tocar este nombre que se utiliza para identificar al gráfico. */
        this.chartName = "chats_count";
        this.chartType = "bar";
        this.xLabelName = "días";
        this.yLabelName = "cantidad de chats";
        this.chartDescription = "Reporte de cantidad de chats por día";
        this.width = 450;
        this.height = 300;
    }

    @Override
    public AbstractDataset getDataSet() {
        return this.dataset;
    }

    @Required
    public void setLoggingService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public void reloadDataSet(IntervalCriteria intervalCriteria) {
        Map<Date, Long> chatsPerDay = loggingService.getChatsPerDay(intervalCriteria);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        this.dataset = new DefaultCategoryDataset();

        if (chatsPerDay.values().size() == 0) {
        	this.dataset = null;
        	return;
        } 
        
        for (Date date : chatsPerDay.keySet()) {
            this.dataset.addValue(chatsPerDay.get(date), dateFormat.format(date), "");
        }
    }
}
