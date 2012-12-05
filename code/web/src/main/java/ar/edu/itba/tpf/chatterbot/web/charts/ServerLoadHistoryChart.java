package ar.edu.itba.tpf.chatterbot.web.charts;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.jfree.data.general.AbstractDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.service.ServerService;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;
import ar.edu.itba.tpf.chatterbot.support.ServerCriteria;

public class ServerLoadHistoryChart extends ReportChart {

    private ServerService serverService;
    private TimeSeriesCollection dataset; 

    @Required
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }
    
    public ServerLoadHistoryChart() {
        /* No tocar este nombre que se utiliza para identificar al gráfico. */
        this.chartName = "server_load_history";
        this.chartType = "timeseries";
        this.xLabelName = "";
        this.yLabelName = "cantidad de chats";
        this.chartDescription = "Reporte de carga histórica";
        this.width = 450;
        this.height = 300;
    }

    @Override
    public AbstractDataset getDataSet() {
    	return this.dataset ;
    }
    
    @Override
    public void reloadDataSet(IntervalCriteria intervalCriteria) {
    	Collection<Server> servers = serverService.getServers();

    	if (servers.size() == 0) {
    		this.dataset = null;
    		return;
    	}

    	this.dataset = new TimeSeriesCollection();

    	boolean isDataPresent = false;
    	
    	for (Server server : servers) {
    		if (server != null) {
        		ServerCriteria serverCriteria = new ServerCriteria(intervalCriteria.getFrom(), intervalCriteria.getTo(),
        				server);
        		Map<Date, Long> serverLoad = serverService.getServerLoad(serverCriteria);
        		
        		if (serverLoad != null && serverLoad.values().size() > 1) {        
        			isDataPresent = true;
        			TimeSeries serie = new TimeSeries(server.getName());
        			for (Date date : serverLoad.keySet()) {
						serie.add(new Day(date), serverLoad.get(date).doubleValue());
					}
            		dataset.addSeries(serie);
        		} 
    		} 
		}
    	
    	if (!isDataPresent) {
    		this.dataset = null;
    	}
    }
}