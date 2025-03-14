package xyz.hashstream.consumer.lambda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import xyz.hashstream.consumer.lambda.model.NotificationPayload;

public class HashStreamModelAdapterTest {

  private HashStreamModelV1Adapter mockV1Adapter;
  private HashStreamModelAdapter testObject;
  private NotificationPayload testPayload;
  private NotificationPayload adaptedPayload;

  @BeforeEach
  void setUp() {
    mockV1Adapter = mock(HashStreamModelV1Adapter.class);
    testObject = new HashStreamModelAdapter(mockV1Adapter);
    testPayload = mock(NotificationPayload.class);
    adaptedPayload = mock(NotificationPayload.class);

    when(mockV1Adapter.adaptNotificationPayload(testPayload)).thenReturn(adaptedPayload);
  }

  @Test
  void testAdaptNotificationPayload_WithV1ExplicitVersion() {
    NotificationPayload result = testObject.adaptNotificationPayload(testPayload, "v1");

    verify(mockV1Adapter).adaptNotificationPayload(testPayload);
    assertEquals(adaptedPayload, result);
  }

  @Test
  void testAdaptNotificationPayload_WithDefaultVersion() {
    NotificationPayload result = testObject.adaptNotificationPayload(testPayload, null);

    verify(mockV1Adapter).adaptNotificationPayload(testPayload);
    assertEquals(adaptedPayload, result);
  }

  @Test
  void testAdaptNotificationPayload_WithV2Version() {
    NotificationPayload result = testObject.adaptNotificationPayload(testPayload,"v2");

    verifyNoInteractions(mockV1Adapter);
    assertEquals(testPayload, result);
  }

  @ParameterizedTest
  @ValueSource(strings = { "v3", "invalid", "unknown" })
  void testAdaptNotificationPayload_WithUnsupportedVersion(String version) {
    assertThrows(
        IllegalArgumentException.class,
        () -> testObject.adaptNotificationPayload(testPayload, version));

    verifyNoInteractions(mockV1Adapter);
  }
}
