package xyz.hashstream.consumer.lambda.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Simple auth filter that requires a specific API key to be present in the
 * x-api-key header of the request. This filter
 * is registered in the
 * {@link xyz.hashstream.consumer.StreamLambdaHandler}
 * class.
 */
@Component
public class SimpleAuthFilter implements Filter {
  private static Logger log = LoggerFactory.getLogger(SimpleAuthFilter.class);

  private final String apiKey;

  // Constructor injection of apiKey bean
  public SimpleAuthFilter(String apiKey) {
    this.apiKey = apiKey;
  }

  @Override
  public void init(FilterConfig filterConfig)
      throws ServletException {
    // nothing to do in init
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {

    if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
      HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      log.info("Request received for path: " + httpServletRequest.getRequestURI());

      String requestApiKey = httpServletRequest.getHeader("x-api-key");

      if (requestApiKey == null) {
        log.warn("x-api-key header is required");
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "x-api-key header is required");
        return;

      }

      if (requestApiKey != apiKey) {
        log.warn("API Key is not valid");
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "invalid authentication");
        return;
      }

      filterChain.doFilter(servletRequest, servletResponse);
      return;
    }

    throw new ServletException(
        "Expected HTTP Server request, actually received: " + servletRequest.getClass().getName());
  }

  @Override
  public void destroy() {
    // nothing to do in destroy
  }
}
