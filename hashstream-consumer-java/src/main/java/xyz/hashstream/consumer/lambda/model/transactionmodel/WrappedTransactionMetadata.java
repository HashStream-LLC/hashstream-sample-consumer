package xyz.hashstream.consumer.lambda.model.transactionmodel;

import org.springframework.lang.Nullable;

public record WrappedTransactionMetadata(
    String consensusTimestamp,
    long chargedTxFee,
    long maxFee,
    String memo,
    @Nullable String node,
    int nonce,
    @Nullable String parentConsensusTimestamp,
    boolean scheduled,
    String transactionHash,
    String transactionId,
    String transactionType,
    String payerAccountId,
    @Nullable Long validDurationSeconds,
    String validStartTimestamp) {
}
