package controllers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;

public class EpaperController extends Controller {

	private final WSClient ws;

	private static final Logger.ALogger LOGGER = Logger.of(EpaperController.class);

	@Inject
	EpaperController(WSClient ws) {
		this.ws = ws;

	}

	public Result proxy(String domain, String path) {
		JsonNode json = request().body().asJson();
		LOGGER.info("post payload: " + json);
		WSRequest request = ws.url("https://" + domain + "/" + path);
		WSRequest complexRequest = request.setRequestTimeout(Duration.of(6000, ChronoUnit.MILLIS)).addHeader(ACCEPT,
				"application/json");
		JsonNode returnBody;
		try {
			returnBody = complexRequest.post(json).thenApply(response -> response.asJson()).toCompletableFuture()
					.join();
		} catch (Exception e) {
			return ok().withHeader("Access-Control-Allow-Origin", "*").withHeader("Access-Control-Allow-Headers",
					"Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
		}
		return ok(returnBody).withHeader("Access-Control-Allow-Origin", "*").withHeader("Access-Control-Allow-Headers",
				"Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");

	}

}