package com.sia.profiler.main.routes;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.sia.profiler.main.dao.ImageProfile;
import com.sia.profiler.main.vo.ImageProfileVO;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;

public class ImageProfileRoute extends AllDirectives {
	
	private ImageProfile mapper;
	
	public ImageProfileRoute(SqlSession session) {
		this.mapper = session.getMapper(ImageProfile.class);
	}
	
	public Route createRoute() {
		return pathPrefix("profiles", () -> (
			concat(
				pathEnd(() -> 
					post(() ->
        				complete("이미지 전송"))),
				pathEnd(() -> 
					get(() ->
						complete(StatusCodes.OK, mapper.findByAll(), Jackson.<List<ImageProfileVO>>marshaller()))),
				path(PathMatchers.integerSegment(), id -> 
					get(() ->
						complete(StatusCodes.OK, mapper.findById(id), Jackson.<ImageProfileVO>marshaller())))
				)
			)
		);
	}
}
