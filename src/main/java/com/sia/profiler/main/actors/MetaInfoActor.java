package com.sia.profiler.main.actors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.sia.profiler.main.vo.ImageProfileVO;

import akka.actor.UntypedAbstractActor;

public class MetaInfoActor extends UntypedAbstractActor {

	public static void main(String[] args) {
		ImageProfileVO vo = new ImageProfileVO();
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		
		String filename = "D:\\Dev\\Images\\test3.jpg";
		
        Mat src = Imgcodecs.imread(filename);
        
        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(src, bgrPlanes);
        
        int histSize = 256;
        float[] range = {0, 255};
        MatOfFloat histRange = new MatOfFloat(range);
        boolean accumulate = false;
        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
        Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);
        
        for (int i = 0; i < bgrPlanes.size(); i++) {
			Mat mat = bgrPlanes.get(i);
			System.out.println("row : " + mat.rows() + " || cols" + mat.cols());
			for (int j = 0; j < 256; j++) {
				System.out.println(i + "번째 -> " + j + " : " + mat.get(j, 0)[0] * j);
			}
		}
        
        
        
        
		try {
			File file = new File("D:\\Dev\\Images\\test3.jpg");
			Metadata metadata = ImageMetadataReader.readMetadata(file);
			metadata.getDirectories().forEach((dir) -> {
				dir.getTags().forEach((tag) -> {
					System.out.println(tag);
				});
			});
			metadata.getDirectoriesOfType(ExifIFD0Directory.class).forEach((dir) -> {
				dir.getTags().forEach((tag) -> {
					System.out.println(tag);
				});
			});
			metadata.getDirectoriesOfType(JpegDirectory.class).forEach((dir) -> {
				dir.getTags().forEach((tag) -> {
					System.out.println(tag.getTagName());
				});
			});
			metadata.getDirectoriesOfType(FileSystemDirectory.class).forEach((dir) -> {
				dir.getTags().forEach((tag) -> {
					if(tag.getTagName().equals("File Name")) {
						vo.setName(tag.getDescription());
						return;
					}
				});
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		// TODO Auto-generated method stub

	}

}
