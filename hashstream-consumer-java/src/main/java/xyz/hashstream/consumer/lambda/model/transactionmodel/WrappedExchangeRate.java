package xyz.hashstream.consumer.lambda.model.transactionmodel;

import org.springframework.lang.Nullable;

public record WrappedExchangeRate(
    int centEquiv,
    int hbarEquiv,
    @Nullable Long expirationTime) {
}
