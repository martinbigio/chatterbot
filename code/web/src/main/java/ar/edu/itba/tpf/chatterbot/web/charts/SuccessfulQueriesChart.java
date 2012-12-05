package ar.edu.itba.tpf.chatterbot.web.charts;

import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.service.LoggingService;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;

public class SuccessfulQueriesChart extends ReportChart {

    private LoggingService loggingService;

    private DefaultPieDataset pieDataSet = null;

    @Required
    public void setLoggingService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public SuccessfulQueriesChart() {
        /* No tocar este nombre que se utiliza para identificar al gráfico. */
        this.chartName = "successful_queries";
        this.chartType = "pie";
        this.xLabelName = " ";
        this.yLabelName = " ";
        this.chartDescription = "Reporte de consultas exitosas";
        this.width = 450;
        this.height = 300;
    }

    public void reloadDataSet(IntervalCriteria intervalCriteria) {
        pieDataSet = new DefaultPieDataset();
        Long[] values = loggingService.getSuccessfulChatsRatio(intervalCriteria);

        if (values[0] + values[1] == 0) {
            pieDataSet = null;
        } else {
            pieDataSet.setValue("Consultas exitosas: " + values[0], (float)values[0]/values[1]);
            pieDataSet.setValue("Consultas fallidas: " + (values[1] - values[0]), 1 - ((float)values[0]/values[1]));
        }
    }

    @Override
    public AbstractDataset getDataSet() {
        return pieDataSet;
    }

}
