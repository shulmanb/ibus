package com.ibus.navigation.module;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.ibus.connectivity.Retrieable;
import com.ibus.connectivity.RetryRedisOperation;
import com.ibus.navigation.map.LinesMapTest;
import com.ibus.navigation.map.LinesMapFactory;
import com.ibus.navigation.map.MapQuery;
import com.ibus.navigation.map.Navigator;
import com.ibus.navigation.map.db.IMapDBLoader;
import com.ibus.navigation.map.db.IMapQueryDB;
import com.ibus.navigation.map.db.SimpleDBMapLoader;
import com.ibus.navigation.map.db.SimpleDBMapQuery;

public class NavigationModule extends AbstractModule {

	private String awsKey;
	private String awsSecret;
	private static Injector injector;
	private static NavigationModule nm = null;
	private static MapQuery mapquery;
	private static Navigator navigator;
	
	public NavigationModule(){
	}
	
	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}

	public void setAwsSecret(String awsSecret) {
		this.awsSecret = awsSecret;
	}

	@Override
	protected void configure() {
		bind(IMapDBLoader.class).to(SimpleDBMapLoader.class).asEagerSingleton();
		bind(IMapQueryDB.class).to(SimpleDBMapQuery.class).asEagerSingleton();
		bind(LinesMapFactory.class);
		bind(String.class).annotatedWith(Names.named("AWS USER KEY")).toInstance(awsKey);
		bind(String.class).annotatedWith(Names.named("AWS SECRET KEY")).toInstance(awsSecret);
	}

	
	public static synchronized void initialize(String awsKey, String awsSecret){
		if(nm == null){
			nm = new NavigationModule();
			nm.setAwsKey(awsKey);
			nm.setAwsSecret(awsSecret);
			injector = Guice.createInjector(nm);
			mapquery = injector.getInstance(MapQuery.class);
			navigator = injector.getInstance(Navigator.class);
		}
	}
	
	public static Navigator getNavigator(){
		return navigator;
	}
	
	public static MapQuery getMapQuery(){
		return mapquery;
	}
	

}
