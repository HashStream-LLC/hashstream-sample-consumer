package xyz.hashstream.consumer.lambda.model.transactionmodel;

public record WrappedNftTransfer(
    String receiverAccountId,
    String senderAccountId,
    long serialNumber,
    String tokenId,
    boolean isApproval) implements WrappedTokenTransfer {

}
