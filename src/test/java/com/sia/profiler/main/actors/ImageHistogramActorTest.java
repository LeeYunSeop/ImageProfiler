package com.sia.profiler.main.actors;

import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sia.profiler.main.actors.testkit.AkkaJUnitActorSystemResource;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.testkit.TestActorRef;

public class ImageHistogramActorTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ImageMetaInfoActorTest.class);
	
	@ClassRule
	public static AkkaJUnitActorSystemResource actorSystemResource = new AkkaJUnitActorSystemResource("ImageHistogramActorTest",
			ConfigFactory.parseString("akka.loggers = [akka.testkit.TestEventListener]"));

	private final ActorSystem system = actorSystemResource.getSystem();
	
	// 히스토그램 테스트
	@Test
	public void calcHistTest() throws InterruptedException, ExecutionException {
		
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		
		final Props props = Props.create(ImageHistogramActor.class, "src/test/resources/image/image.jpg", 0);
		final TestActorRef<ImageHistogramActor> ref = TestActorRef.create(system, props, "ImageHistogramActor");
		final CompletableFuture<Object> future = Patterns.ask(ref, "test", Duration.ofMillis(3000))
				.toCompletableFuture();
		assertTrue(future.isDone());
		Map<String, Double[]> result = (Map<String, Double[]>) future.get();
		logger.info(Arrays.toString(result.get("gray")));
	}
}
