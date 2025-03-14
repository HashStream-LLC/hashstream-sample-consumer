package xyz.hashstream.consumer.lambda.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import xyz.hashstream.consumer.lambda.model.NotificationPayload;
import xyz.hashstream.consumer.lambda.model.transactionmodel.WrappedContractCallLogInfo;
import xyz.hashstream.consumer.lambda.model.transactionmodel.WrappedContractCallResult;
import xyz.hashstream.consumer.lambda.model.transactionmodel.WrappedTransactionMetadata;
import xyz.hashstream.consumer.lambda.model.transactionmodel.WrappedTransactionModel;

@Service
public class HashStreamModelV1Adapter {

  private final Base64Decoder base64Decoder;

  public HashStreamModelV1Adapter(Base64Decoder base64Decoder) {
    this.base64Decoder = base64Decoder;
  }

  public NotificationPayload adaptNotificationPayload(NotificationPayload payload) {
    var transaction = payload.content();
    var transactionMetadata = transaction.metadata();

    // Decode transaction hash
    String transactionHash = base64Decoder.decodeBase64AsHex(transactionMetadata.transactionHash());

    // Create metadata with decoded transaction hash
    WrappedTransactionMetadata adaptedMetadata = new WrappedTransactionMetadata(
        transactionMetadata.consensusTimestamp(),
        transactionMetadata.chargedTxFee(),
        transactionMetadata.maxFee(),
        transactionMetadata.memo(),
        transactionMetadata.node(),
        transactionMetadata.nonce(),
        transactionMetadata.parentConsensusTimestamp(),
        transactionMetadata.scheduled(),
        transactionHash,
        transactionMetadata.transactionId(),
        transactionMetadata.transactionType(),
        transactionMetadata.payerAccountId(),
        transactionMetadata.validDurationSeconds(),
        transactionMetadata.validStartTimestamp());

    // Handle contract call if it exists
    WrappedContractCallResult adaptedContractCall = null;
    if (transaction.contractCall() != null) {
      adaptedContractCall = adaptV1ContractCall(transaction.contractCall());
    }

    // Create and return the wrapped model
    return new NotificationPayload(
        payload.metadata(),
        new WrappedTransactionModel(
            adaptedMetadata,
            transaction.receipt(),
            transaction.transfers(),
            adaptedContractCall));
  }

  private WrappedContractCallResult adaptV1ContractCall(WrappedContractCallResult contractCallResult) {
    // Decode function parameters if present
    String functionParameters = null;
    if (contractCallResult.functionParameters() != null) {
      functionParameters = base64Decoder.decodeBase64AsHex(contractCallResult.functionParameters());
    }

    // Decode bloom if present
    String bloom = null;
    if (contractCallResult.bloom() != null) {
      bloom = base64Decoder.decodeBase64AsHex(contractCallResult.bloom());
    }

    // Decode contract call result if present
    String contractCallResultStr = null;
    if (contractCallResult.contractCallResult() != null) {
      contractCallResultStr = base64Decoder.decodeBase64AsHex(contractCallResult.contractCallResult());
    }

    // Decode EVM address if present
    String evmAddress = null;
    if (contractCallResult.evmAddress() != null) {
      evmAddress = base64Decoder.decodeBase64AsHex(contractCallResult.evmAddress());
    }

    // Process log info list
    List<WrappedContractCallLogInfo> adaptedLogInfo;
    if (contractCallResult.logInfo() != null) {
      adaptedLogInfo = contractCallResult.logInfo().stream()
          .map(this::adaptV1ContractCallLogInfo)
          .toList();
    } else {
      adaptedLogInfo = Collections.emptyList();
    }

    // Create result with all parameters in correct order
    return new WrappedContractCallResult(
        contractCallResult.contractId(),
        contractCallResult.gas(),
        contractCallResult.amount(),
        functionParameters,
        contractCallResult.gasUsed(),
        bloom,
        contractCallResultStr,
        contractCallResult.errorMessage(),
        evmAddress,
        contractCallResult.senderId(),
        adaptedLogInfo);
  }

  private WrappedContractCallLogInfo adaptV1ContractCallLogInfo(WrappedContractCallLogInfo logInfo) {
    // Decode bloom if present
    String bloom = null;
    if (logInfo.bloom() != null) {
      bloom = base64Decoder.decodeBase64AsHex(logInfo.bloom());
    }

    // Decode data if present
    String data = null;
    if (logInfo.data() != null) {
      data = base64Decoder.decodeBase64AsHex(logInfo.data());
    }

    // Process topics
    List<String> decodedTopics;
    if (logInfo.topic() != null) {
      decodedTopics = logInfo.topic().stream()
          .map(base64Decoder::decodeBase64AsHex)
          .toList();
    } else {
      decodedTopics = Collections.emptyList();
    }

    // Create log info with all parameters in correct order
    return new WrappedContractCallLogInfo(
        logInfo.contractId(),
        bloom,
        data,
        decodedTopics);
  }
}
