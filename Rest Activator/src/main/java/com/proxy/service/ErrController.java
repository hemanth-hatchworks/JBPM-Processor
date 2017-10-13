package com.proxy.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proxy.service.conf.ErrorJson;

@RestController
public class ErrController implements ErrorController {

	private static final String PATH = "/error";

	@Value("${debug}")
	private boolean debug;

	@Autowired
	private ErrorAttributes errorAttributes;

	// Appropriate HTTP response code is automatically set by Spring.
	@RequestMapping(value = PATH)
	ErrorJson error(HttpServletRequest request, HttpServletResponse response) {
		// Here we just define response body.
		return new ErrorJson(response.getStatus(),
				errorAttributes.getErrorAttributes(new ServletRequestAttributes(request), true));
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}

}