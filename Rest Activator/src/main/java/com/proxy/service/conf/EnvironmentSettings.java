package com.proxy.service.conf;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix="target")
public class EnvironmentSettings {

     @NotNull
     @Valid
     private String hostname;

     @NotNull
     @Valid
     private String port;
     

     @NotNull
     @Valid
     private String processURL;
     
     
     public String getHostname() {
          return hostname;
     }

     public void setHostname(String hostname) {
          this.hostname = hostname;
     }

     public String getPort() {
          return port;
     }

     public void setPort(String port) {
          this.port = port;
     }

	public String getProcessURL() {
		return processURL;
	}

	public void setProcessURL(String processURL) {
		this.processURL = processURL;
	}
     
     

}
