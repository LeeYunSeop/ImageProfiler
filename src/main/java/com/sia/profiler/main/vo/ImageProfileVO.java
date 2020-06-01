package com.sia.profiler.main.vo;

import java.util.Map;

public class ImageProfileVO {
	
	private int id;
	private ImageMetaInfoVO imageMetaInfo;
	private ImageStatisticsVO imageStatistics;
	private Map<String, Double[]> imageHistogram;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ImageMetaInfoVO getImageMetaInfo() {
		return imageMetaInfo;
	}
	public void setImageMetaInfo(ImageMetaInfoVO imageMetaInfo) {
		this.imageMetaInfo = imageMetaInfo;
	}
	public ImageStatisticsVO getImageStatistics() {
		return imageStatistics;
	}
	public void setImageStatistics(ImageStatisticsVO imageStatistics) {
		this.imageStatistics = imageStatistics;
	}
	public Map<String, Double[]> getImageHistogram() {
		return imageHistogram;
	}
	public void setImageHistogram(Map<String, Double[]> imageHistogram) {
		this.imageHistogram = imageHistogram;
	}
	@Override
	public String toString() {
		return "ImageProfileVO [id=" + id + ", imageMetaInfo=" + imageMetaInfo + ", imageStatistics=" + imageStatistics
				+ ", imageHistogram=" + imageHistogram + "]";
	}
}
