package com.ibus.connectivity;

import java.io.IOException;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class AbstractRedisStorage implements IReconnectable{

	protected Jedis myJedis;
	protected JedisPool jedisPool = null;
	protected String host;
	protected int port;

	public AbstractRedisStorage(String redisHost, int redisPort) {
		Config conf = new Config();
		conf.testOnBorrow = true;
		conf.maxActive = -1;
		this.jedisPool = new JedisPool(conf, redisHost,redisPort);
		this.host = redisHost;
		this.port = redisPort;
	}

	public AbstractRedisStorage(Jedis jedis) {
		this.myJedis = jedis;
	}

	@Override
	public void reconnect() throws IOException {
		if(myJedis != null){
			this.myJedis = new Jedis(host, port);
			myJedis.connect();
		}else{
			Config conf = new Config();
			conf.testOnBorrow = true;
			conf.maxActive = -1;
			this.jedisPool = new JedisPool(conf, host,port);
			System.err.println("JedisPool reconnected!!!!!!!!");
		}
	}

	protected Jedis getJedis() {
		if(myJedis != null){
			return myJedis;
		}
		Jedis ret = jedisPool.getResource();
		return ret;
	}

	protected void returnJedis(Jedis jedis) {
		if(myJedis == null){
			jedisPool.returnResource(jedis);
		}
	}

}
