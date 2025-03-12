package xyz.hashstream.consumer.lambda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
public class SsmClientWrapper {
  @Bean
  @Lazy
  public SsmClient ssmClient() {
    return SsmClient.builder().build();
  }

}
