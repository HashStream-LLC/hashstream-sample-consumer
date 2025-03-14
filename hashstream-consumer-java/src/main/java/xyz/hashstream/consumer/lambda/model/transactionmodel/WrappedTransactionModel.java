package xyz.hashstream.consumer.lambda.model.transactionmodel;

import org.springframework.lang.Nullable;

public record WrappedTransactionModel(
    WrappedTransactionMetadata metadata,
    WrappedReceipt receipt,
    WrappedTransfers transfers,
    @Nullable WrappedContractCallResult contractCall) {
}
