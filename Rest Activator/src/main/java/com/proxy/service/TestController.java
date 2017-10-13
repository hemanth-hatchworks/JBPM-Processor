package com.proxy.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.proxy.service.conf.EnvironmentSettings;

//@RestController
//@RequestMapping("/test")
public class TestController extends Base {
	@Value("${security.oauth2.client.clientId}")
	private String clienId;
	@Value("${security.oauth2.client.clientSecret}")
	private String clienSecret;
	@Value("${security.oauth2.resource.userInfoUri}")
	private String userURI;
	@Value("${security.user.name}")
	private String userName;
	@Value("${security.user.password}")
	private String password;

	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	private ApplicationContext context;

	@Autowired
	private EnvironmentSettings environment;
	
	// Special handler to get required testing calls
	private final RequestMappingHandlerMapping handlerMapping;
	
	private static final String CALLS = "/calls";


	@RequestMapping("/login")
	public String testLogin(HttpServletRequest request) {
		try {
			JsonParserFactory.getJsonParser().parseMap(login(request));

			return green();
		} catch (Exception e) {
			return red();
		}

	}

	@RequestMapping(value = "/accountCreation", produces = MediaType.TEXT_HTML_VALUE)
	public String testCreateAccount(HttpServletRequest request) {
		return createAccount(request, "static/partner-req.json");

	}

	private String createAccount(HttpServletRequest request, String file) {
		String targetUrl = getURI(request, "/cloudmgr/api/partners?format=json");

		RestTemplate restTemplate = new RestTemplate();

		try {
			Resource resource = new ClassPathResource(file);
			InputStream in = resource.getInputStream();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>(read(in));

			String json = login(request);

			Map<String, Object> map = JsonParserFactory.getJsonParser().parseMap(json);
			if (map != null && map.get("access_token") != null) {
				List<String> list = new ArrayList<String>();
				list.add("Bearer " + (String) map.get("access_token"));
				headers.put("Authorization", list);
			}

			String resp = restTemplate.postForObject(targetUrl, entity, String.class);
			JsonParserFactory.getJsonParser().parseMap(resp);

			return green();

		} catch (Exception e) {
			return red();
		}
	}

	private String login(HttpServletRequest request) {

		String plainCreds = clienId + ":" + clienSecret;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = getURI(request, "/cloudmgr/oauth/token");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "password");
		map.add("client_id", clienId);
		map.add("client_secret", clienSecret);
		map.add("username", userName);
		map.add("password", password);
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", "Basic " + base64Creds);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		String fetchedResponse = restTemplate.postForObject(targetUrl, entity, String.class);

		return fetchedResponse;
	}

	private static String read(InputStream input) throws IOException {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}

	private static String getURI(HttpServletRequest request, String path) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
	}

	@Autowired
	public TestController(RequestMappingHandlerMapping handlerMapping) {
		this.handlerMapping = handlerMapping;
	}

	@RequestMapping(value = CALLS, produces = MediaType.APPLICATION_JSON_VALUE)
	public LinkedList<String> getTestCalls() {
		List<RequestMappingInfo> requestMappings = this.handlerMapping.getHandlerMethods().entrySet().stream().map(map -> map.getKey()).collect(Collectors.toList());
		LinkedList<String> list = new LinkedList<String>();
		for (RequestMappingInfo info : requestMappings) {
			if (info.getPatternsCondition().toString().contains("test") && !info.getPatternsCondition().toString().contains(CALLS)) {
				list.addAll(info.getPatternsCondition().getPatterns().stream().collect(Collectors.toList()));
			}
		}
		return list;
	}

}
