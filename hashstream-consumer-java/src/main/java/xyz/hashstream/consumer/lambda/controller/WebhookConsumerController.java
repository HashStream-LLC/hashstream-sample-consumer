package xyz.hashstream.consumer.lambda.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import xyz.hashstream.consumer.lambda.model.NotificationPayload;
import xyz.hashstream.consumer.lambda.model.WebhookResponse;

@RestController
@EnableWebMvc
public class WebhookConsumerController {
  Logger logger = LoggerFactory.getLogger(WebhookConsumerController.class);

  @RequestMapping(path = "/hashstream-webhook", method = RequestMethod.POST)
  public WebhookResponse handleWebhook(@RequestBody NotificationPayload payload) {
    logger.info("Received webhook payload: {}", payload);
    return new WebhookResponse("success");
  }
}
