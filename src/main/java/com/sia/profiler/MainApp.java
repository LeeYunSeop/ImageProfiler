package com.sia.profiler;

import java.util.concurrent.CompletionStage;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sia.profiler.config.mybatis.MybatisSessionFactory;
import com.sia.profiler.main.routes.ImageProfileRoute;

import akka.Main;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

public class MainApp {
	
	private final static Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) throws Exception {
		// 프로그램 시작 시 opencv 라이브러리를 불러온다.
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		
		ActorSystem system = ActorSystem.create("routes");
		final Http http = Http.get(system);
		final Materializer materializer = Materializer.matFromSystem(system);
		
		SqlSession session = MybatisSessionFactory.getSqlSessionFactory().openSession(true);
		ImageProfileRoute route = new ImageProfileRoute(session);

		final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = route.createRoute().flow(system, materializer);
		final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
				ConnectHttp.toHost("localhost", 8080), materializer);
		log.info("Start Server! http://localhost:8080/\nPress RETURN to stop....");
		System.in.read();
		binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
	}
}
