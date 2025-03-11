package xyz.hashstream.consumer.lambda.model.transactionmodel;

import java.util.List;
import java.util.Optional;

public record WrappedReceipt(
    String status,
    Optional<String> accountId,
    Optional<String> fileId,
    Optional<String> contractId,
    Optional<String> scheduledTransactionId,
    Optional<String> scheduleId,
    Optional<String> tokenId,
    Optional<List<Long>> serialNumbers,
    Optional<Long> newTotalSupply,
    Optional<String> topicId,
    Optional<Long> topicSequenceNumber,
    Optional<String> topicRunningHash,
    Optional<Long> topicRunningHashVersion,
    Optional<WrappedExchangeRateSet> exchangeRate) {
}
