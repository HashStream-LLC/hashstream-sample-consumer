import type { LambdaFunctionURLEvent } from "aws-lambda";

import { buildSignedHeaders, generateTestKey, TestKey } from "./helpers/signing";

const KEY_ID = "test-key";
let testKey: TestKey;

beforeAll(() => {
  testKey = generateTestKey(KEY_ID);
});

beforeEach(() => {
  jest.resetModules();
  process.env.HASHSTREAM_NETWORK = "testnet";

  global.fetch = jest.fn(async () => ({
    ok: true,
    status: 200,
    statusText: "OK",
    headers: new Headers({ "cache-control": "public, max-age=3600" }),
    json: async () => ({ keys: [testKey.jwk] }),
  })) as unknown as typeof fetch;
});

function buildEvent(rawBody: string, headers: Record<string, string>): LambdaFunctionURLEvent {
  return {
    version: "2.0",
    routeKey: "$default",
    rawPath: "/",
    rawQueryString: "",
    headers,
    requestContext: {
      accountId: "123456789012",
      apiId: "x",
      domainName: "x",
      domainPrefix: "x",
      http: {
        method: "POST",
        path: "/",
        protocol: "HTTP/1.1",
        sourceIp: "127.0.0.1",
        userAgent: "test",
      },
      requestId: "x",
      routeKey: "$default",
      stage: "$default",
      time: "now",
      timeEpoch: 0,
    },
    body: rawBody,
    isBase64Encoded: false,
  };
}

const samplePayloadBody = JSON.stringify({
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
  },
});

describe("handler", () => {
  it("returns 200 for a valid signed payload", async () => {
    const { handler } = await import("../src/index");
    const headers = buildSignedHeaders(testKey, samplePayloadBody);
    const result = await handler(buildEvent(samplePayloadBody, headers));

    expect(result.statusCode).toBe(200);
    expect(JSON.parse(result.body as string)).toEqual({ status: "success" });
  });

  it("returns 401 when signature headers are missing", async () => {
    const { handler } = await import("../src/index");
    const result = await handler(buildEvent(samplePayloadBody, {}));

    expect(result.statusCode).toBe(401);
    expect(JSON.parse(result.body as string).status).toBe("rejected");
  });

  it("returns 401 when the body has been tampered", async () => {
    const { handler } = await import("../src/index");
    const headers = buildSignedHeaders(testKey, samplePayloadBody);
    const result = await handler(buildEvent(samplePayloadBody + "x", headers));

    expect(result.statusCode).toBe(401);
  });

  it("returns 400 when the body is not JSON", async () => {
    const { handler } = await import("../src/index");
    const body = "not json";
    const headers = buildSignedHeaders(testKey, body);
    const result = await handler(buildEvent(body, headers));

    expect(result.statusCode).toBe(400);
  });

  it("throws if required env vars are missing", async () => {
    delete process.env.HASHSTREAM_NETWORK;
    const { handler } = await import("../src/index");
    const headers = buildSignedHeaders(testKey, samplePayloadBody);

    await expect(handler(buildEvent(samplePayloadBody, headers))).rejects.toThrow(
      /HASHSTREAM_NETWORK/,
    );
  });

  it("throws if HASHSTREAM_NETWORK is not a known network", async () => {
    process.env.HASHSTREAM_NETWORK = "previewnet";
    const { handler } = await import("../src/index");
    const headers = buildSignedHeaders(testKey, samplePayloadBody);

    await expect(handler(buildEvent(samplePayloadBody, headers))).rejects.toThrow(
      /HASHSTREAM_NETWORK/,
    );
  });
});
