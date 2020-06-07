package com.sia.profiler.main.dao;

import java.util.List;

import com.sia.profiler.main.vo.ImageProfileVO;
import com.sia.profiler.main.vo.ResponseVO;

public interface ImageProfile {
	
	public List<ResponseVO> findByAll();
	public ResponseVO findById(int id);
	public int insertProfile(ImageProfileVO param);
	public int insertHistogram(ImageProfileVO param);
	
}
