package xyz.hashstream.consumer.lambda.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import xyz.hashstream.consumer.lambda.model.NotificationPayload;
import xyz.hashstream.consumer.lambda.model.WebhookResponse;
import xyz.hashstream.consumer.lambda.service.HashStreamModelAdapter;

@RestController
public class WebhookConsumerController {
  private static final String HASHSTREAM_DATA_VERSION_HEADER = "hashstream-data-version";
  private final Logger logger = LoggerFactory.getLogger(WebhookConsumerController.class);

  @Autowired
  private HashStreamModelAdapter hashStreamModelAdapter;

  @RequestMapping(path = "/hashstream-webhook", method = RequestMethod.POST)
  public WebhookResponse handleWebhook(@RequestBody NotificationPayload payload,
      @RequestHeader HttpHeaders headers) {
    logger.info("Received webhook payload: {}", payload);

    var adaptedPayload = hashStreamModelAdapter.adaptNotificationPayload(payload,
        headers.getFirst(HASHSTREAM_DATA_VERSION_HEADER));
    logger.info("Adapted webhook payload: {}", adaptedPayload);

    return new WebhookResponse("success");
  }
}
