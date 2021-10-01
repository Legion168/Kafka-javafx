package com.github.client.configuration;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

@Configuration
public class ClientTwitter {
	@Value("${twitter.consumer.key}")
	private String consumerKey;

	@Value("${twitter.consumer.secret}")
	private String consumerSecret;

	@Value("${twitter.token}")
	private String token;

	@Value("${twitter.secret}")
	private String secret;

	private final List<String> terms = List.of("bitcoin", "sport", "pizza", "alien");

	public Client createClient(final BlockingQueue<String> msgQueue) {
		final Hosts host = new HttpHosts(Constants.STREAM_HOST);
		final StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint().trackTerms(terms);

		final Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);

		final ClientBuilder builder = new ClientBuilder()
				.name("Hosebird-Client-01")
				.hosts(host)
				.authentication(auth)
				.endpoint(endpoint)
				.processor(new StringDelimitedProcessor(msgQueue));

		return builder.build();
	}
}
