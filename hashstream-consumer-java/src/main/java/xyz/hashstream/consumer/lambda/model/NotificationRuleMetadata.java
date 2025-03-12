package xyz.hashstream.consumer.lambda.model;

public record NotificationRuleMetadata(String id, String name, int type, String predicateValue, String chain) {
}
