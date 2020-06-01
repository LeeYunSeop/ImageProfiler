package com.sia.profiler.main.routes;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.sia.profiler.main.dao.ImageProfile;
import com.sia.profiler.main.vo.ResponseVO;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.unmarshalling.Unmarshaller;

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
						entity(Unmarshaller.entityToMultipartFormData(), formData -> {
					      return complete(StatusCodes.OK, "이미지 전송 API", Jackson.marshaller());
						})
					)),
				pathEnd(() -> 
					get(() ->
						complete(StatusCodes.OK, mapper.findByAll(), Jackson.<List<ResponseVO>>marshaller()))),
				path(PathMatchers.integerSegment(), id -> 
					get(() ->
						complete(StatusCodes.OK, mapper.findById(id), Jackson.<ResponseVO>marshaller())))
				)
			)
		);
	}
}
