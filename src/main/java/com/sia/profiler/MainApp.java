package com.sia.profiler;

import java.util.concurrent.CompletionStage;

import org.apache.ibatis.session.SqlSession;

import com.sia.profiler.config.mybatis.MybatisSessionFactory;
import com.sia.profiler.main.routes.ImageProfileRoute;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

public class MainApp {

	public static void main(String[] args) throws Exception {
		ActorSystem system = ActorSystem.create("routes");

		final Http http = Http.get(system);
		final ActorMaterializer materializer = ActorMaterializer.create(system);
		
		SqlSession session = MybatisSessionFactory.getSqlSessionFactory().openSession(true);
		ImageProfileRoute route = new ImageProfileRoute(session);

		final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = route.createRoute().flow(system, materializer);
		final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
				ConnectHttp.toHost("localhost", 8080), materializer);

		System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
		System.in.read();

		binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
	}
}
