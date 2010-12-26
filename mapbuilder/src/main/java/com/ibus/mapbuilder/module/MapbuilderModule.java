package com.ibus.mapbuilder.module;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.ibus.connectivity.Retrieable;
import com.ibus.connectivity.RetryRedisOperation;
import com.ibus.mapbuilder.Mapbuilder;
import com.ibus.mapbuilder.db.IBuilderDB;
import com.ibus.mapbuilder.db.SimpleDBRedisBuilderDB;

public class MapbuilderModule extends AbstractModule {

	private String awsKey;
	private String awsSecret;
	private String redisHost;
	private Integer redisPort;
	private static Injector injector;
	private static MapbuilderModule mb = null;
	private static Mapbuilder builder;
	
	public MapbuilderModule(){
	}
	
	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}


	public void setRedisPort(Integer redisPort) {
		this.redisPort = redisPort;
	}

	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}

	public void setAwsSecret(String awsSecret) {
		this.awsSecret = awsSecret;
	}

	@Override
	protected void configure() {
		bind(IBuilderDB.class).to(SimpleDBRedisBuilderDB.class).asEagerSingleton();
		bind(String.class).annotatedWith(Names.named("REDIS HOST")).toInstance(redisHost);
		bind(Integer.class).annotatedWith(Names.named("REDIS PORT")).toInstance(redisPort);
 
		bind(String.class).annotatedWith(Names.named("AWS USER KEY")).toInstance(awsKey);
		bind(String.class).annotatedWith(Names.named("AWS SECRET KEY")).toInstance(awsSecret);

		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Retrieable.class), 
		        new RetryRedisOperation());
	}

	
	public static synchronized void initialize(String host, int port, String awsKey, String awsSecret){
		if(mb == null){
			mb = new MapbuilderModule();
			mb.setRedisHost(host);
			mb.setRedisPort(port);
			mb.setAwsKey(awsKey);
			mb.setAwsSecret(awsSecret);
			injector = Guice.createInjector(mb);
			builder = injector.getInstance(Mapbuilder.class);
		}
	}
	
	public static Mapbuilder getMapbuilder(){
		return builder;
	}
	

 }
