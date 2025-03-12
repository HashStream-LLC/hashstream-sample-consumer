package xyz.hashstream.consumer.lambda.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

@Configuration
public class LambdaConfig {

  @Value("${api.key.ssm.path}")
  private String apiKeyPath;

  @Bean
  @Lazy
  public String apiKey(SsmClient ssmClient) {
     if (apiKeyPath.isEmpty()) {
      throw new IllegalStateException("API_KEY_SSM_PATH environment variable or property not set");
    }
    GetParameterRequest request = GetParameterRequest.builder()
        .name(apiKeyPath)
        .withDecryption(true)
        .build();

    GetParameterResponse response = ssmClient.getParameter(request);
    return response.parameter().value();
  }
}
