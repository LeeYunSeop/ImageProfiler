package com.sia.profiler.main.vo;

public class ImageStatisticsVO {
	
	private double min;
	private double max;
	private double avg;
	
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	@Override
	public String toString() {
		return "ImageStatisticsVO [min=" + min + ", max=" + max + ", avg=" + avg + "]";
	}
	
}
