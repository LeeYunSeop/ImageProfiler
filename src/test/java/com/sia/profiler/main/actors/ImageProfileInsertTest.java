package com.sia.profiler.main.actors;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.ibatis.session.SqlSession;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sia.profiler.config.mybatis.MybatisSessionFactory;
import com.sia.profiler.main.actors.testkit.AkkaJUnitActorSystemResource;
import com.sia.profiler.main.dao.ImageProfile;
import com.sia.profiler.main.vo.ImageMetaInfoVO;
import com.sia.profiler.main.vo.ImageProfileVO;
import com.sia.profiler.main.vo.ImageStatisticsVO;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.testkit.TestActorRef;

public class ImageProfileInsertTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ImageProfileInsertTest.class);
	
	@ClassRule
	public static AkkaJUnitActorSystemResource actorSystemResource = new AkkaJUnitActorSystemResource("ImageMetaInfoActorTest",
			ConfigFactory.parseString("akka.loggers = [akka.testkit.TestEventListener]"));

	private final ActorSystem system = actorSystemResource.getSystem();
	
	// 이미지 기본정보 Insert 테스트
	@Test
	public void insertTest() throws InterruptedException, ExecutionException {

		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

		final Props imageMetaInfoActorProps = Props.create(ImageMetaInfoActor.class, "src/test/resources/image/image.jpg");
		final TestActorRef<ImageMetaInfoActor> imageMetaInfoActorRef = TestActorRef.create(system, imageMetaInfoActorProps, "ImageMetaInfoActor");
		final CompletableFuture<Object> imageMetaInfoActorFuture = Patterns.ask(imageMetaInfoActorRef, "test", Duration.ofMillis(3000))
				.toCompletableFuture();
		ImageMetaInfoVO imageMetaInfoResult = (ImageMetaInfoVO) imageMetaInfoActorFuture.get();
		logger.info(imageMetaInfoResult.toString());
		
		ImageProfileVO vo = new ImageProfileVO();
		vo.setImageMetaInfo(imageMetaInfoResult);
		
		final Props imageStatisticsActorProps = Props.create(ImageStatisticsActor.class, "src/test/resources/image/image.jpg", 0);
		final TestActorRef<ImageStatisticsActor> imageStatisticsActorRef = TestActorRef.create(system, imageStatisticsActorProps, "ImageStatisticsActor");
		final CompletableFuture<Object> imageStatisticsActorFuture = Patterns.ask(imageStatisticsActorRef, "test", Duration.ofMillis(3000))
				.toCompletableFuture();
		ImageStatisticsVO imageStatisticsResult = (ImageStatisticsVO) imageStatisticsActorFuture.get();
		vo.setImageStatistics(imageStatisticsResult);
		
		logger.info(vo.toString());
		
		final Props imageHistogramActorProps = Props.create(ImageHistogramActor.class, "src/test/resources/image/image.jpg", 0);
		final TestActorRef<ImageStatisticsActor> imageHistogramActorRef = TestActorRef.create(system, imageHistogramActorProps, "ImageHistogramActor");
		final CompletableFuture<Object> imageHistogramActorFuture = Patterns.ask(imageHistogramActorRef, "test", Duration.ofMillis(3000))
				.toCompletableFuture();
		Map<String, Double[]> imageHistogramResult = (Map<String, Double[]>) imageHistogramActorFuture.get();
		vo.setImageHistogram(imageHistogramResult);
		
		SqlSession session = MybatisSessionFactory.getSqlSessionFactory().openSession(true);
		ImageProfile mapper = session.getMapper(ImageProfile.class);
		mapper.insertProfile(vo);
		logger.info(vo.toString());
		mapper.insertHistogram(vo);
	}
}
