package xyz.hashstream.consumer.lambda.model.transactionmodel;

import java.util.Optional;

public record WrappedExchangeRate(
    int centEquiv,
    int hbarEquiv,
    Optional<Long> expirationTime) {
}
