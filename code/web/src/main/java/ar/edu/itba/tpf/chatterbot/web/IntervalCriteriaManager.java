package ar.edu.itba.tpf.chatterbot.web;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;

public class IntervalCriteriaManager {

	private static final int FROM_HOUR_INITIAL = 0;
	private static final int TO_HOUR_INITIAL = 23;
	
	private IntervalCriteria intervalCriteria;
	
	
	public IntervalCriteriaManager() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date());
		gc.add(GregorianCalendar.MONTH, -1);
		this.intervalCriteria = new IntervalCriteria(gc.getTime(), new Date());
		this.setFromHour(FROM_HOUR_INITIAL);
		this.setToHour(TO_HOUR_INITIAL);
	}
	
	public void setFromDate(Date fromDate) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(intervalCriteria.getFrom());
		int prevFromHour = gc.get(Calendar.HOUR_OF_DAY);
		gc.setTime(fromDate);
		gc.set(Calendar.HOUR_OF_DAY, prevFromHour);
		intervalCriteria.setFrom(gc.getTime());
	}
	
	public Date getFromDate() {
		return intervalCriteria.getFrom();
	}
	
	public void setFromHour(int fromHour) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(intervalCriteria.getFrom());
		gc.set(Calendar.HOUR_OF_DAY, fromHour);
		intervalCriteria.setFrom(gc.getTime());
	}
	
	public int getFromHour() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(intervalCriteria.getFrom());
		return gc.get(Calendar.HOUR_OF_DAY);
	}
	
	
	public void setToDate(Date toDate) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(intervalCriteria.getTo());
		int prevSetHour = gc.get(Calendar.HOUR_OF_DAY);
		gc.setTime(toDate);
		gc.set(Calendar.HOUR_OF_DAY, prevSetHour);
		intervalCriteria.setTo(toDate);
	}
	
	public Date getToDate() {
		return intervalCriteria.getTo();
	}
	
	public void setToHour(int toHour) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(intervalCriteria.getTo());
		gc.set(Calendar.HOUR_OF_DAY, toHour);
		intervalCriteria.setTo(gc.getTime());
	}
	
	public int getToHour() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(intervalCriteria.getTo());
		return gc.get(Calendar.HOUR_OF_DAY);
	}
	
	public IntervalCriteria getIntervalCriteria() {
		return intervalCriteria;
	}
}
