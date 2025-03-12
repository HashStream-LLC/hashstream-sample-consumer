package xyz.hashstream.consumer.lambda.model.transactionmodel;

// Sealed interface for token transfers
public sealed interface WrappedTokenTransfer permits WrappedFungibleTransfer, WrappedNftTransfer {
  String tokenId();
}
