package com.ibus.connectivity;

import java.io.IOException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


public class RetryRedisOperation implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Exception ex = null;
		for(int i = 0;i < 3;i++){
			try{
				return invocation.proceed();	
			}catch(redis.clients.jedis.exceptions.JedisException e){
				ex = e;
				try {
					Object target = invocation.getThis();
					((IReconnectable)target).reconnect();
				} catch (IOException e1) {
					throw e;
				}
			}
		}
		throw ex;
	}

}
