package com.sia.profiler.main.actors;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.sia.profiler.main.vo.ImageStatisticsVO;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ImageStatisticsActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	private String filePath = null;
	private int flag = 0;
	private int size = 256;
	float[] range = { 0, 255 };

	public ImageStatisticsActor(String filePath, int flag) {
		this.filePath = filePath;
		this.flag = flag;
	}

	@Override
	public Receive createReceive() {
		log.info("start ImageStatistics");
		return receiveBuilder().matchAny(x -> getSender().tell(getStatistics(), getSelf())).build();
	}
	
	private ImageStatisticsVO getStatistics() {
		Mat image = Imgcodecs.imread(filePath, flag);
		List<Mat> splitImage = new ArrayList<>();
		Core.split(image, splitImage);

		String[] channels;
		// flag에 맞게 channel을 선언해준다.(0=gray, 1=color)
		if (flag == 0) {
			channels = new String[] { "gray" };
		} else {
			channels = new String[] { "blue", "green", "red" };
		}

		// histogram 범위와 사이즈를 설정해준다.
		MatOfFloat histRange = new MatOfFloat(range);
		MatOfInt histSize = new MatOfInt(size);

		// Histogram을 계산한다.
		List<Mat> histList = new ArrayList<Mat>();
		for (int i = 0; i < channels.length; i++) {
			Mat hist = new Mat();
			Imgproc.calcHist(splitImage, new MatOfInt(i), new Mat(), hist, histSize, histRange);
			histList.add(hist);
		}
		
		double max = Integer.MIN_VALUE;
		double min = Integer.MAX_VALUE;
		
		// 계산된 히스토그램을 이용하여 최소,최대,평균값을 구한다.
		for (int i = 0; i < histList.size(); i++) {
			Mat hist = histList.get(i);
			for (int j = 0; j < size; j++) {
				double histVal = hist.get(j, 0)[0];
				// MAX 값
	            if(histVal > max){
	                max = histVal;
	            }
	            // MIN 값
	            if(histVal < min){
	                min = histVal;
	            }
			}
		}
		
		ImageStatisticsVO result = new ImageStatisticsVO();
		result.setMin(min);
		result.setMax(max);
		result.setAvg((min+max)/2);
		
		return result;
	}
}
