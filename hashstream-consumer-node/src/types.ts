export interface NotificationRuleMetadata {
  id: string;
  name: string;
  type: number;
  predicateValue: string;
  chain: string;
}

export interface NotificationMetadata {
  rule: NotificationRuleMetadata;
  network: string;
  sentinelTimestamp: string;
  timeSinceConsensus: string;
}

export interface WrappedTransactionMetadata {
  consensusTimestamp: string;
  chargedTxFee: number;
  maxFee: number;
  memo: string;
  node?: string;
  nonce: number;
  parentConsensusTimestamp?: string;
  scheduled: boolean;
  transactionHash: string;
  transactionId: string;
  transactionType: string;
  payerAccountId: string;
  validDurationSeconds?: number;
  validStartTimestamp: string;
}

export interface WrappedExchangeRate {
  centEquiv: number;
  hbarEquiv: number;
  expirationTime?: number;
}

export interface WrappedExchangeRateSet {
  nextRate?: WrappedExchangeRate;
  currentRate?: WrappedExchangeRate;
}

export interface WrappedReceipt {
  status: string;
  accountId?: string;
  fileId?: string;
  contractId?: string;
  scheduledTransactionId?: string;
  scheduleId?: string;
  tokenId?: string;
  serialNumbers?: number[];
  newTotalSupply?: number;
  exchangeRate?: WrappedExchangeRateSet;
}

export interface WrappedCryptoTransfer {
  account: string;
  amount: number;
  isApproval: boolean;
}

export interface WrappedFungibleTransfer {
  account: string;
  tokenId: string;
  amount: number;
  isApproval: boolean;
}

export interface WrappedNftTransfer {
  receiverAccountId: string;
  senderAccountId: string;
  serialNumber: number;
  tokenId: string;
  isApproval: boolean;
}

export interface WrappedTransfers {
  crypto: WrappedCryptoTransfer[];
  tokens: WrappedFungibleTransfer[];
  nfts: WrappedNftTransfer[];
}

export interface WrappedContractCallLogInfo {
  contractId: string;
  bloom: string;
  data: string;
  topic: string[];
}

export interface WrappedContractCallResult {
  contractId: string;
  gas: number;
  amount: number;
  functionParameters: string;
  gasUsed: number;
  bloom: string;
  contractCallResult: string;
  errorMessage: string;
  evmAddress?: string;
  senderId?: string;
  logInfo: WrappedContractCallLogInfo[];
}

export interface WrappedTransactionModel {
  metadata: WrappedTransactionMetadata;
  receipt: WrappedReceipt;
  transfers: WrappedTransfers;
  contractCall?: WrappedContractCallResult;
}

export interface NotificationPayload {
  metadata: NotificationMetadata;
  content: WrappedTransactionModel;
}
