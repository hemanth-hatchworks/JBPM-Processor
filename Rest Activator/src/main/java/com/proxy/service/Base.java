package com.proxy.service;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.proxy.service.conf.EnvironmentSettings;

public class Base {
	protected static Log log = LogFactory.getLog(Base.class);
	@Autowired
	protected EnvironmentSettings environment;
	protected String TGT_URL = ""; //target invoker url
	
	@PostConstruct
	private void postConstructor() {
		TGT_URL = "http://" + environment.getHostname() + ":" + environment.getPort();
	}

	public static String green() {
		return "<i class='fa fa-thumbs-up' style='color:green'></i>";
	}

	public static String red() {
		return "<i class='fa fa-thumbs-down' style='color:red'></i>";
	}
	

}
