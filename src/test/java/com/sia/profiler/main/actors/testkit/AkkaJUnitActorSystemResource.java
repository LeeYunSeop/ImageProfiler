package com.sia.profiler.main.actors.testkit;

import org.junit.rules.ExternalResource;

import com.typesafe.config.Config;

import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;

public class AkkaJUnitActorSystemResource extends ExternalResource {
	private ActorSystem system = null;
	private final String name;
	private final Config config;

	private ActorSystem createSystem(String name, Config config) {
		try {
			return ActorSystem.create(name, config);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public AkkaJUnitActorSystemResource(String name, Config config) {
		this.name = name;
		this.config = config;
	}

	@Override
	protected void before() throws Throwable {
		if (system == null) {
			system = createSystem(name, config);
		}
	}

	@Override
	protected void after() {
		TestKit.shutdownActorSystem(system);
		system = null;
	}

	public ActorSystem getSystem() {
		if (system == null) {
			system = createSystem(name, config);
		}
		return system;
	}
}