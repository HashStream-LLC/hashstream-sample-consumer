package xyz.hashstream.consumer.lambda.model.transactionmodel;

import java.util.Optional;

public record WrappedTransactionMetadata(
    String consensusTimestamp,
    long chargedTxFee,
    long maxFee,
    String memo,
    Optional<String> node,
    int nonce,
    Optional<String> parentConsensusTimestamp,
    boolean scheduled,
    String transactionHash,
    String transactionId,
    String transactionType,
    String payerAccountId,
    Optional<Long> validDurationSeconds,
    String validStartTimestamp) {
}
