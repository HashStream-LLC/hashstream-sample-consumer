package xyz.hashstream.consumer.lambda.model.transactionmodel;

import java.util.List;
import org.springframework.lang.Nullable;

public record WrappedReceipt(
    String status,
    @Nullable String accountId,
    @Nullable String fileId,
    @Nullable String contractId,
    @Nullable String scheduledTransactionId,
    @Nullable String scheduleId,
    @Nullable String tokenId,
    @Nullable List<Long> serialNumbers,
    @Nullable Long newTotalSupply,
    @Nullable WrappedExchangeRateSet exchangeRate) {
}
