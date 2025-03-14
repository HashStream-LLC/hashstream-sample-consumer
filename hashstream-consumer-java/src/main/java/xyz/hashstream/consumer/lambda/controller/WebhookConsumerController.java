package xyz.hashstream.consumer.lambda.controller;

import org.springframework.http.HttpHeaders;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import xyz.hashstream.consumer.lambda.model.NotificationPayload;
import xyz.hashstream.consumer.lambda.model.WebhookResponse;
import xyz.hashstream.consumer.lambda.service.HashStreamModelAdapter;

@RestController
@EnableWebMvc
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
