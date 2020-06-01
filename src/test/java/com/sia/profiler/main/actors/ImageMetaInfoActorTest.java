package com.sia.profiler.main.actors;

import static org.junit.Assert.assertTrue;

import java.time.Duration;
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
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.testkit.TestActorRef;

public class ImageMetaInfoActorTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ImageMetaInfoActorTest.class);
	
	@ClassRule
	public static AkkaJUnitActorSystemResource actorSystemResource = new AkkaJUnitActorSystemResource("ImageMetaInfoActorTest",
			ConfigFactory.parseString("akka.loggers = [akka.testkit.TestEventListener]"));

	private final ActorSystem system = actorSystemResource.getSystem();
	
	// 이미지 메타정보 테스트
	@Test
	public void imageMetaInfoTest() throws InterruptedException, ExecutionException {

		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

		final Props props = Props.create(ImageMetaInfoActor.class, "src/test/resources/image/image.jpg");
		final TestActorRef<ImageMetaInfoActor> ref = TestActorRef.create(system, props, "ImageMetaInfoActor");
		final CompletableFuture<Object> future = Patterns.ask(ref, "test", Duration.ofMillis(3000))
				.toCompletableFuture();
		assertTrue(future.isDone());
		ImageMetaInfoVO result = (ImageMetaInfoVO) future.get();
		logger.info(result.toString());
		
		ImageProfileVO vo = new ImageProfileVO();
		vo.setImageMetaInfo(result);
		
		SqlSession session = MybatisSessionFactory.getSqlSessionFactory().openSession(true);
		ImageProfile mapper = session.getMapper(ImageProfile.class);
		int test = mapper.insertProfile(vo);
		System.out.println(test);
	}
}
