package xyz.hashstream.consumer.lambda.model.transactionmodel;

import org.springframework.lang.Nullable;

public record WrappedExchangeRateSet(
    @Nullable WrappedExchangeRate nextRate,
    @Nullable WrappedExchangeRate currentRate) {

}
