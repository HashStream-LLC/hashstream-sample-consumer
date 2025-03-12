package xyz.hashstream.consumer.lambda.model.transactionmodel;

public record WrappedFungibleTransfer(
    String account,
    String tokenId,
    long amount,
    boolean isApproval) implements WrappedTokenTransfer {

}
