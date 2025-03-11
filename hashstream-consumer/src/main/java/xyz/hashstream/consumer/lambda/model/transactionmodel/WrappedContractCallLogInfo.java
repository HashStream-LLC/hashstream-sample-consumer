package xyz.hashstream.consumer.lambda.model.transactionmodel;

import java.util.List;

public record WrappedContractCallLogInfo(
    String contractId,
    String bloom,
    String data,
    List<String> topic) {
}
