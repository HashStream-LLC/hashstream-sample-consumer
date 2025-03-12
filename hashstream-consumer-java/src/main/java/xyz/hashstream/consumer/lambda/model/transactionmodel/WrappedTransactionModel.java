package xyz.hashstream.consumer.lambda.model.transactionmodel;

import java.util.Optional;

public record WrappedTransactionModel(
    WrappedTransactionMetadata metadata,
    WrappedReceipt receipt,
    WrappedTransfers transfers,
    Optional<WrappedContractCallResult> contractCall) {
}
