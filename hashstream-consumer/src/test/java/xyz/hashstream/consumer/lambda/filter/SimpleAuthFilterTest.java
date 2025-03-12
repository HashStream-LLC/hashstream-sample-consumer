package xyz.hashstream.consumer.lambda.filter;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;

class SimpleAuthFilterTest {

  private static final String VALID_API_KEY = "test-api-key";

  @Mock
  private HttpServletRequest httpRequest;

  @Mock
  private HttpServletResponse httpResponse;

  @Mock
  private FilterChain filterChain;

  @Mock
  private ServletRequest nonHttpRequest;

  private SimpleAuthFilter simpleAuthFilter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    simpleAuthFilter = new SimpleAuthFilter(VALID_API_KEY);
    when(httpRequest.getRequestURI()).thenReturn("/hashstream-webhook");
  }

  @Test
  void testDoFilter_WithValidApiKey_ShouldContinueChain() throws IOException, ServletException {
    // Arrange
    when(httpRequest.getHeader("x-api-key")).thenReturn(VALID_API_KEY);

    // Act
    simpleAuthFilter.doFilter(httpRequest, httpResponse, filterChain);

    // Assert
    verify(filterChain).doFilter(httpRequest, httpResponse);
    verifyNoInteractions(httpResponse);
  }

  @Test
  void testDoFilter_WithMissingApiKey_ShouldReturn401() throws IOException, ServletException {
    // Arrange
    when(httpRequest.getHeader("x-api-key")).thenReturn(null);

    // Act
    simpleAuthFilter.doFilter(httpRequest, httpResponse, filterChain);

    // Assert
    verify(httpResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "x-api-key header is required");
    verifyNoInteractions(filterChain);
  }

  @Test
  void testDoFilter_WithInvalidApiKey_ShouldReturn403() throws IOException, ServletException {
    // Arrange
    when(httpRequest.getHeader("x-api-key")).thenReturn("invalid-key");

    // Act
    simpleAuthFilter.doFilter(httpRequest, httpResponse, filterChain);

    // Assert
    verify(httpResponse).sendError(HttpServletResponse.SC_FORBIDDEN, "invalid authentication");
    verifyNoInteractions(filterChain);
  }

  @Test
  void testDoFilter_WithoutApiKey_OnHealthCheck_ShouldContinueChain() throws IOException, ServletException {
    // Arrange
    when(httpRequest.getRequestURI()).thenReturn("/health-check");

    // Act
    simpleAuthFilter.doFilter(httpRequest, httpResponse, filterChain);

    // Assert
    verify(filterChain).doFilter(httpRequest, httpResponse);
    verifyNoInteractions(httpResponse);
  }

  @Test
  void testDoFilter_WithNonHttpRequest_ShouldThrowException() {
    // Act & Assert
    ServletException exception = assertThrows(ServletException.class, () -> {
      simpleAuthFilter.doFilter(nonHttpRequest, httpResponse, filterChain);
    });

    assertTrue(exception.getMessage().contains("Expected HTTP Server request"));
    verifyNoInteractions(filterChain);
  }

  @Test
  void testInitAndDestroy_ShouldNotThrowException() throws ServletException {
    // These methods are empty but should be tested for coverage
    assertDoesNotThrow(() -> simpleAuthFilter.init(null));
    assertDoesNotThrow(() -> simpleAuthFilter.destroy());
  }
}
