package com.sia.profiler.main.actors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ImageHistogramActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	private String filePath = null;
	private int flag = 0;
	private int size = 256;
	float[] range = { 0, 255 };

	public ImageHistogramActor(String filePath, int flag) {
		this.filePath = filePath;
		this.flag = flag;
	}

	@Override
	public Receive createReceive() {
		log.info("start ImageHistogram");
		return receiveBuilder().matchAny(x -> getSender().tell(calcHist(), getSelf())).build();
	}

	private Map<String, Double[]> calcHist() {
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

		// 계산된 히스토그램을 channel명을 가진 Map으로 완성시킨다.
		Map<String, Double[]> result = new HashMap<String, Double[]>();
		for (int i = 0; i < histList.size(); i++) {
			Mat hist = histList.get(i);
			Double[] histVal = new Double[size];
			for (int j = 0; j < size; j++) {
				histVal[j] = hist.get(j, 0)[0];
			}
			result.put(channels[i], histVal);
		}

		return result;
	}

}
