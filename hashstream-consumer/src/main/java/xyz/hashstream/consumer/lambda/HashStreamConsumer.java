package xyz.hashstream.consumer.lambda;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.Timer;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hashstream.consumer.lambda.filter.SimpleAuthFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;

public class HashStreamConsumer implements RequestStreamHandler {
  private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
  private static final Logger log = LoggerFactory.getLogger(HashStreamConsumer.class);
  static {
    try {
      handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);

      // we use the onStartup method of the handler to register our custom filter
      handler.onStartup(servletContext -> {
        FilterRegistration.Dynamic registration = servletContext.addFilter("SimpleAuthFilter",
            SimpleAuthFilter.class);
        registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
      });
    } catch (ContainerInitializationException e) {
      // if we fail here. We re-throw the exception to force another cold start
      log.error("Error initializing hashstream consumer", e);
      throw new RuntimeException("Could not initialize Spring Boot application", e);
    }
  }

  public HashStreamConsumer() {
    // we enable the timer for debugging. This SHOULD NOT be enabled in production.
    Timer.enable();
  }

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
      throws IOException {
    handler.proxyStream(inputStream, outputStream, context);
  }
}
