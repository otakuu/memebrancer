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

public class RoundshotController extends Controller {

	private final WSClient ws;

	private static final Logger.ALogger LOGGER = Logger.of(RoundshotController.class);

	@Inject
	RoundshotController(WSClient ws) {
		this.ws = ws;

	}

	public Result image(String domain, String path) {
		WSRequest request = ws.url("https://" + domain + ".roundshot.com/" + path + "/structure.json");
		WSRequest complexRequest = request.setRequestTimeout(Duration.of(6000, ChronoUnit.MILLIS));
		JsonNode returnBody = complexRequest.get().thenApply(response -> response.asJson()).toCompletableFuture()
				.join();
		return ok(returnBody).withHeader("Access-Control-Allow-Origin", "*");
	}

}