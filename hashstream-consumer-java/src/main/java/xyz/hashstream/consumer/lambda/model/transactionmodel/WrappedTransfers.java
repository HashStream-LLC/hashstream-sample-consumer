package xyz.hashstream.consumer.lambda.model.transactionmodel;

import java.util.List;

public record WrappedTransfers(
    List<WrappedCryptoTransfer> crypto,
    List<WrappedFungibleTransfer> tokens,
    List<WrappedNftTransfer> nfts) {

}
