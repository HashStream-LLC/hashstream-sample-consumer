package xyz.hashstream.consumer.lambda.model.transactionmodel;

import java.util.Optional;

public record WrappedExchangeRateSet(
    Optional<WrappedExchangeRate> nextRate,
    Optional<WrappedExchangeRate> currentRate) {

}
