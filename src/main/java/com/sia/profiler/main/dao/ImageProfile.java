package com.sia.profiler.main.dao;

import java.util.List;

import com.sia.profiler.main.vo.ImageProfileVO;

public interface ImageProfile {
	
	public List<ImageProfileVO> findByAll();
	public ImageProfileVO findById(int id);
	
}
