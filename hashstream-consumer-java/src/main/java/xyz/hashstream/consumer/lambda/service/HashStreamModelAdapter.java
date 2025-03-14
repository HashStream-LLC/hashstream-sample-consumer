package xyz.hashstream.consumer.lambda.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import xyz.hashstream.consumer.lambda.model.NotificationPayload;

@Service
public class HashStreamModelAdapter {
  private static final String DEFAULT_VERSION = "v1";

  private final HashStreamModelV1Adapter v1Adapter;

  public HashStreamModelAdapter(HashStreamModelV1Adapter v1Adapter) {
    this.v1Adapter = v1Adapter;
  }

  public NotificationPayload adaptNotificationPayload(NotificationPayload payload,
      String dataVersion) {
    return switch (dataVersion == null ? DEFAULT_VERSION: dataVersion) {
      case DEFAULT_VERSION -> v1Adapter.adaptNotificationPayload(payload);
      case "v2" -> payload;
      default -> throw new IllegalArgumentException("Unsupported version: " + dataVersion);
    };
  }
}
