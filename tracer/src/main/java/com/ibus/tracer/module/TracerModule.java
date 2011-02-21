package com.ibus.tracer.module;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.ibus.connectivity.Retrieable;
import com.ibus.connectivity.RetryRedisOperation;
import com.ibus.tracer.BusPositionTracer;
import com.ibus.tracer.SessionManager;
import com.ibus.tracer.Tracer;
import com.ibus.tracer.db.ISessionDB;
import com.ibus.tracer.db.ITracingDB;
import com.ibus.tracer.db.RedisSessionDB;
import com.ibus.tracer.db.RedisTracingDB;

public class TracerModule extends AbstractModule {

	private String redisHost;
	private Integer redisPort;
	private static Injector injector;
	private static TracerModule tm = null;
	private static Tracer tr = null;
	private static SessionManager sm = null;
	private static BusPositionTracer bpt = null;
	
	public TracerModule(){
	}
	
	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}


	public void setRedisPort(Integer redisPort) {
		this.redisPort = redisPort;
	}


	@Override
	protected void configure() {
		bind(ISessionDB.class).to(RedisSessionDB.class).asEagerSingleton();
		bind(String.class).annotatedWith(Names.named("REDIS HOST")).toInstance(redisHost);
		bind(Integer.class).annotatedWith(Names.named("REDIS PORT")).toInstance(redisPort);
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Retrieable.class), 
		        new RetryRedisOperation());
		bind(ITracingDB.class).to(RedisTracingDB.class).asEagerSingleton();
	}

	
	public static synchronized void initialize(String host, int port){
		if(tm == null){
			tm = new TracerModule();
			tm.setRedisHost(host);
			tm.setRedisPort(port);
			injector = Guice.createInjector(tm);
			tr = injector.getInstance(Tracer.class);
			sm = injector.getInstance(SessionManager.class);
			bpt = injector.getInstance(BusPositionTracer.class);
		}
	}
	

	public static Tracer getTracer(){
		return tr;
	}
	
	public static SessionManager getSessionManager(){
		return sm;
	}
	
	public static BusPositionTracer getBusPositionTracer(){
		return bpt;
	}
}
