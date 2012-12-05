package ar.edu.itba.tpf.chatterbot.web;

import org.jfree.data.general.DefaultPieDataset;

public class PieBean {

	public PieBean() {
	}
	
	public DefaultPieDataset getPieDataSet() {
		DefaultPieDataset pieDataSet = new DefaultPieDataset();
		
		pieDataSet.setValue("A",52);
		pieDataSet.setValue("B", 18);
		pieDataSet.setValue("C", 30); return pieDataSet;
	}

}
