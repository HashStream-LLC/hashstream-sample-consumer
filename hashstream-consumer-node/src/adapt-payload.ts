import {
  NotificationPayload,
  WrappedContractCallLogInfo,
  WrappedContractCallResult,
} from "./types";

const DEFAULT_VERSION = "v1";

export function decodeBase64AsHex(base64: string): string {
  return "0x" + Buffer.from(base64, "base64").toString("hex");
}

export function adaptNotificationPayload(
  payload: NotificationPayload,
  dataVersion: string | undefined,
): NotificationPayload {
  const version = dataVersion ?? DEFAULT_VERSION;
  switch (version) {
    case "v1":
      return adaptV1(payload);
    case "v2":
      return payload;
    default:
      throw new Error(`Unsupported version: ${version}`);
  }
}

function adaptV1(payload: NotificationPayload): NotificationPayload {
  const { content } = payload;
  return {
    metadata: payload.metadata,
    content: {
      ...content,
      metadata: {
        ...content.metadata,
        transactionHash: decodeBase64AsHex(content.metadata.transactionHash),
      },
      contractCall: content.contractCall
        ? adaptV1ContractCall(content.contractCall)
        : undefined,
    },
  };
}

function adaptV1ContractCall(
  call: WrappedContractCallResult,
): WrappedContractCallResult {
  return {
    ...call,
    functionParameters: call.functionParameters
      ? decodeBase64AsHex(call.functionParameters)
      : call.functionParameters,
    bloom: call.bloom ? decodeBase64AsHex(call.bloom) : call.bloom,
    contractCallResult: call.contractCallResult
      ? decodeBase64AsHex(call.contractCallResult)
      : call.contractCallResult,
    evmAddress: call.evmAddress ? decodeBase64AsHex(call.evmAddress) : call.evmAddress,
    logInfo: (call.logInfo ?? []).map(adaptV1LogInfo),
  };
}

function adaptV1LogInfo(
  log: WrappedContractCallLogInfo,
): WrappedContractCallLogInfo {
  return {
    contractId: log.contractId,
    bloom: log.bloom ? decodeBase64AsHex(log.bloom) : log.bloom,
    data: log.data ? decodeBase64AsHex(log.data) : log.data,
    topic: (log.topic ?? []).map(decodeBase64AsHex),
  };
}
