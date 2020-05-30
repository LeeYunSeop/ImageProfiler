package com.sia.profiler.main.vo;

public class ImageProfileVO {
	
	private int id;
	private String name;
	private int width;
	private int height;
	private String shootingTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getShootingTime() {
		return shootingTime;
	}
	public void setShootingTime(String shootingTime) {
		this.shootingTime = shootingTime;
	}
	@Override
	public String toString() {
		return "ImageProfileVO [id=" + id + ", name=" + name + ", width=" + width + ", height=" + height
				+ ", shootingTime=" + shootingTime + "]";
	}
	
}
