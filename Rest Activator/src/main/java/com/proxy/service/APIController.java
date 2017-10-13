package com.proxy.service;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class APIController extends Base {

	/**
	 * For all POST method endpoints
	 *
	 * @param path
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{pathparam:.*}", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> marshalToProcessServerPost(@PathVariable("pathparam") String path,
			@RequestBody String body) {
		System.out.println(path);

		return marshalToProcessServer(path, body);

	}

	/**
	 * For all GET method endpoints - Path reflect the process server process
	 * instance id
	 *
	 * @param path
	 * @return
	 */
	@RequestMapping(value = "/{pathparam:.*}", produces = "application/json")
	public ResponseEntity<String> marshalToProcessServerGet(@PathVariable("pathparam") String path) {
		System.out.println(path);

		return marshalToProcessServer(path, null);
	}

	HttpHeaders createHeaders(String username, String password) {
		return new HttpHeaders() {
			private static final long serialVersionUID = 1L;

			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}

	private ResponseEntity<String> marshalToProcessServer(String path, String body) {
		HttpEntity<String> entity = null;

		log.info("Path: " + path);
		log.info("RequestBody: " + body);

		URI targetUrl = UriComponentsBuilder.fromUriString(TGT_URL).path(environment.getProcessURL()).path(path)
				.path("/start").build().toUri();
		log.info(targetUrl);

		// build the entity - this is applicable only for POST
		HttpHeaders headers = createHeaders("admin", "password1!");
		headers.setContentType(MediaType.APPLICATION_JSON);
		entity = new HttpEntity<String>(body, headers);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> exchangedResponse;
		try {
			exchangedResponse = restTemplate.exchange(targetUrl,  HttpMethod.POST,
					entity, String.class);
		} catch (final HttpClientErrorException e) {
			exchangedResponse = ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
					.body(e.getResponseBodyAsString());
		}
		return exchangedResponse;

	}

}
