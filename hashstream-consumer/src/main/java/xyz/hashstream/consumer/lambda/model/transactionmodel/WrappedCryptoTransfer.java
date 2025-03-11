package xyz.hashstream.consumer.lambda.model.transactionmodel;

public record WrappedCryptoTransfer(
    String account,
    long amount,
    boolean isApproval) {
}
