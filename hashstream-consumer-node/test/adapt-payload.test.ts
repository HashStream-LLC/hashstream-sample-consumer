import { decodeBase64AsHex, adaptNotificationPayload } from "../src/adapt-payload";
import { NotificationPayload } from "../src/types";

const samplePayload: NotificationPayload = {
  metadata: {
    rule: {
      id: "rule-id",
      name: "rule-name",
      type: 4,
      predicateValue: "0.0.4652955",
      chain: "hedera",
    },
    network: "testnet",
    sentinelTimestamp: "1739459546.991503206",
    timeSinceConsensus: "4.35517296",
  },
  content: {
    metadata: {
      consensusTimestamp: "1739459542.955985910",
      chargedTxFee: 43776000,
      maxFee: 60302952,
      memo: "",
      node: "0.0.3",
      nonce: 0,
      scheduled: false,
      transactionHash: "GRUy87gAl9WR63Z1x0ZylpCdtuFZNYkB/WmDzM3tonOik+NJFXDqKdC8LkJmhg70",
      transactionId: "0.0.5492024-1739459523-807109752",
      transactionType: "CONTRACTCALL",
      payerAccountId: "0.0.5492024",
      validDurationSeconds: 120,
      validStartTimestamp: "1739459523.807109752",
    },
    receipt: { status: "SUCCESS", contractId: "0.0.4652955" },
    transfers: { crypto: [], tokens: [], nfts: [] },
    contractCall: {
      contractId: "0.0.4652955",
      gas: 0,
      amount: 0,
      functionParameters: "AAAB",
      gasUsed: 1152000,
      bloom: "AAAB",
      contractCallResult: "AAAB",
      errorMessage: "",
      evmAddress: "oR6X1Ed2SpimCzAJcEN3TrZuVs0=",
      logInfo: [
        {
          contractId: "0.0.15057",
          bloom: "AAAB",
          data: "AAAB",
          topic: ["AAAB"],
        },
      ],
    },
  },
};

describe("decodeBase64AsHex", () => {
  it("prefixes with 0x and decodes payload", () => {
    expect(decodeBase64AsHex("AAAB")).toBe("0x000001");
  });

  it("returns 0x for empty input", () => {
    expect(decodeBase64AsHex("")).toBe("0x");
  });

  it("matches the known transaction hash sample", () => {
    expect(
      decodeBase64AsHex("GRUy87gAl9WR63Z1x0ZylpCdtuFZNYkB/WmDzM3tonOik+NJFXDqKdC8LkJmhg70"),
    ).toBe(
      "0x191532f3b80097d591eb7675c7467296909db6e159358901fd6983cccdeda273a293e3491570ea29d0bc2e4266860ef4",
    );
  });
});

describe("adaptNotificationPayload", () => {
  it("decodes the transaction hash for v1 payloads", () => {
    const adapted = adaptNotificationPayload(samplePayload, "v1");
    expect(adapted.content.metadata.transactionHash).toBe(
      "0x191532f3b80097d591eb7675c7467296909db6e159358901fd6983cccdeda273a293e3491570ea29d0bc2e4266860ef4",
    );
  });

  it("defaults to v1 when no version header is present", () => {
    const adapted = adaptNotificationPayload(samplePayload, undefined);
    expect(adapted.content.metadata.transactionHash.startsWith("0x")).toBe(true);
  });

  it("decodes contract call fields", () => {
    const adapted = adaptNotificationPayload(samplePayload, "v1");
    expect(adapted.content.contractCall?.evmAddress).toBe("0xa11e97d447764a98a60b30097043774eb66e56cd");
    expect(adapted.content.contractCall?.bloom).toBe("0x000001");
    expect(adapted.content.contractCall?.logInfo[0]?.data).toBe("0x000001");
    expect(adapted.content.contractCall?.logInfo[0]?.topic[0]).toBe("0x000001");
  });

  it("passes v2 payloads through untouched", () => {
    const adapted = adaptNotificationPayload(samplePayload, "v2");
    expect(adapted).toBe(samplePayload);
  });

  it("throws on unknown versions", () => {
    expect(() => adaptNotificationPayload(samplePayload, "v99")).toThrow(/Unsupported version/);
  });

  it("preserves unrelated metadata", () => {
    const adapted = adaptNotificationPayload(samplePayload, "v1");
    expect(adapted.metadata).toEqual(samplePayload.metadata);
    expect(adapted.content.receipt).toEqual(samplePayload.content.receipt);
  });
});
