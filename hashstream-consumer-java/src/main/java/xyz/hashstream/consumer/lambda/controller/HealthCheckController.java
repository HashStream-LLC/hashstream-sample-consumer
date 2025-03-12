package xyz.hashstream.consumer.lambda.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import xyz.hashstream.consumer.lambda.model.HealthCheckResponse;

@RestController
@EnableWebMvc
public class HealthCheckController {

  @RequestMapping(path = "/health-check", method = RequestMethod.GET)
  public HealthCheckResponse healthCheck() {
    return new HealthCheckResponse("success");
  }
}
