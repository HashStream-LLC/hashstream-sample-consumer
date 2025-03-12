package xyz.hashstream.consumer.lambda.model;

import xyz.hashstream.consumer.lambda.model.transactionmodel.WrappedTransactionModel;

public record NotificationPayload(NotificationMetadata metadata, WrappedTransactionModel content) {
}
