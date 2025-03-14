package xyz.hashstream.consumer.lambda.model.transactionmodel;

import org.springframework.lang.Nullable;

import java.util.List;

public record WrappedContractCallResult(
    String contractId,
    long gas,
    long amount,
    String functionParameters,
    long gasUsed,
    String bloom,
    String contractCallResult,
    String errorMessage,
    @Nullable String evmAddress,
    @Nullable String senderId,
    List<WrappedContractCallLogInfo> logInfo) {
}
