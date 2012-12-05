package ar.edu.itba.tpf.chatterbot.web;

import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.exception.ServerServiceException;
import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.service.ServerService;

public class StatusServerManagerBean {

	private static final int INITIAL_WIDTH = 150;
	private static final int WIDTH_PER_SERVER = 50;
	private static final String DEFAULT_MESSAGE = "No hay servidores activos";
	
	private int width = INITIAL_WIDTH;
	
	private ServerService serverService;
	private DefaultCategoryDataset dataset;
	private String result = DEFAULT_MESSAGE;
	
	public String getResult() {
		return result;
	}
	
	@Required
	public void setServerService(ServerService serverService) {
		this.serverService = serverService;
		reloadDataSet();
	}
	
	
	public void reloadDataSet() {
		try {
			this.dataset = new DefaultCategoryDataset();
			
			Map<Server, Integer> servers = this.serverService.getActiveChatterbots();
			
			if (servers == null) {
				this.dataset = null;
				this.result = DEFAULT_MESSAGE;
				return;
			}
			
			int serversQty = servers.values().size();
			
			if (serversQty == 0) {
				this.dataset = null;
				this.result = DEFAULT_MESSAGE;
				return;
			}
			
			for (Server server : servers.keySet()) {
				
				if (server != null) {
					int currentSize = servers.get(server);
					this.dataset.addValue(currentSize, "activos", server.getName());
					this.dataset.addValue(server.getMaxChatterbots() - currentSize, "totales", server.getName());
				}
			}

			this.width = INITIAL_WIDTH + (serversQty) * WIDTH_PER_SERVER;

		} catch (ServerServiceException e) {
			this.result = e.getMessage();
			this.dataset = null;
		} 
		
	}
	
	public AbstractDataset getDataset() {
		return this.dataset;
	}
	
	public int getWidth() {
		return width;
	}
	
}
