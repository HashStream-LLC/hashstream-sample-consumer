package xyz.hashstream.consumer.lambda.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import xyz.hashstream.consumer.lambda.model.HealthCheckResponse;

@RestController
public class HealthCheckController {

  @RequestMapping(path = "/health-check", method = RequestMethod.GET)
  public HealthCheckResponse healthCheck() {
    return new HealthCheckResponse("success");
  }
}
