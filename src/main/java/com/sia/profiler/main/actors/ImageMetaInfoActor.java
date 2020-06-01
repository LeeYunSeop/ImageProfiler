package com.sia.profiler.main.actors;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.sia.profiler.main.vo.ImageMetaInfoVO;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ImageMetaInfoActor extends AbstractActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private String filePath = null;

	public ImageMetaInfoActor(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public Receive createReceive() {
		log.info("start ImageMetaInfo");
		return receiveBuilder().matchAny(x -> getSender().tell(getMetaInfo(), getSelf())).build();
	}

	private ImageMetaInfoVO getMetaInfo() throws ImageProcessingException, IOException {
		ImageMetaInfoVO result = new ImageMetaInfoVO();
		File file = new File(filePath);
		Metadata metadata = ImageMetadataReader.readMetadata(file);
		
		// 파일명
		String value = getValue(metadata.getDirectories().iterator(), "File Name");
		if (value != null) {
			result.setName(value);
		}
		// 촬영 시각
		value = getValue(metadata.getDirectories().iterator(), "Date/Time Original");
		if (value != null) {
			result.setShootingTime(value);
		}
		// 너비
		value = getValue(metadata.getDirectories().iterator(), "Image Width");
		if (value != null) {
			result.setWidth(Integer.parseInt(value.split(" ")[0]));
		}
		// 높이
		value = getValue(metadata.getDirectories().iterator(), "Image Height");
		if (value != null) {
			result.setHeight(Integer.parseInt(value.split(" ")[0]));
		}
		return result;
	}
	
	private String getValue(Iterator<Directory> meta, String name) {
		while (meta.hasNext()) {
			Directory dir = meta.next();
			Iterator<Tag> tags = dir.getTags().iterator();
			while (tags.hasNext()) {
				Tag tag = tags.next();
				if (tag.getTagName().equals(name)) {
					return tag.getDescription();
				}
			}
		}
		return null;
	}

}
