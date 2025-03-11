package xyz.hashstream.consumer.lambda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

@Configuration
public class LambdaConfig {

  @Bean
  public SsmClient ssmClient() {
    return SsmClient.builder().build();
  }

  @Bean
  public String apiKeyPath() {
    String apiKeyPath = System.getenv("API_KEY_SSM_PATH");
    if (apiKeyPath == null) {
      throw new IllegalStateException("API_KEY_SSM_PATH environment variable not set");
    }
    return apiKeyPath;
  }

  @Bean
  @Lazy
  public String apiKey(SsmClient ssmClient, String apiKeyPath) {
    GetParameterRequest request = GetParameterRequest.builder()
        .name(apiKeyPath)
        .withDecryption(true)
        .build();

    GetParameterResponse response = ssmClient.getParameter(request);
    return response.parameter().value();
  }
}
