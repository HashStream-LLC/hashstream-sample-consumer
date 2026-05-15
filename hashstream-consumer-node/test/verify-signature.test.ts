import { JwksCache, Jwks } from "../src/jwks-cache";
import { createVerifier } from "../src/verify-signature";

import { buildSignedHeaders, generateTestKey } from "./helpers/signing";

const FIXED_NOW_MS = 1_700_000_000_000;
const now = () => FIXED_NOW_MS;

function makeCacheWithKeys(keys: { jwk: import("../src/jwks-cache").Jwk }[]): JwksCache {
  const fetcher = jest.fn().mockResolvedValue({
    jwks: { keys: keys.map((k) => k.jwk) } as Jwks,
    cacheControlMaxAgeSeconds: 3600,
  });
  return new JwksCache(fetcher, now);
}

describe("createVerifier", () => {
  it("verifies a well-formed signature", async () => {
    const key = generateTestKey("kid-1");
    const cache = makeCacheWithKeys([key]);
    const verifier = createVerifier(cache, now);
    const body = JSON.stringify({ hello: "world" });
    const headers = buildSignedHeaders(key, body, FIXED_NOW_MS);

    const result = await verifier.verify(body, headers);

    expect(result.ok).toBe(true);
  });

  it("rejects tampered bodies", async () => {
    const key = generateTestKey("kid-1");
    const cache = makeCacheWithKeys([key]);
    const verifier = createVerifier(cache, now);
    const body = JSON.stringify({ hello: "world" });
    const headers = buildSignedHeaders(key, body, FIXED_NOW_MS);

    const result = await verifier.verify(body + "tampered", headers);

    expect(result.ok).toBe(false);
    if (!result.ok) expect(result.reason).toMatch(/did not verify/);
  });

  it("rejects unknown kids even after refresh", async () => {
    const known = generateTestKey("known");
    const unknown = generateTestKey("unknown");
    const cache = makeCacheWithKeys([known]);
    const verifier = createVerifier(cache, now);
    const body = "x";
    const headers = buildSignedHeaders(unknown, body, FIXED_NOW_MS);

    const result = await verifier.verify(body, headers);

    expect(result.ok).toBe(false);
    if (!result.ok) expect(result.reason).toMatch(/unknown signing key id/);
  });

  it("rejects stale timestamps", async () => {
    const key = generateTestKey("kid-1");
    const cache = makeCacheWithKeys([key]);
    const verifier = createVerifier(cache, now);
    const body = "x";
    const stale = FIXED_NOW_MS - 10 * 60 * 1000;
    const headers = buildSignedHeaders(key, body, stale);

    const result = await verifier.verify(body, headers);

    expect(result.ok).toBe(false);
    if (!result.ok) expect(result.reason).toMatch(/skew window/);
  });

  it("rejects missing signature headers", async () => {
    const key = generateTestKey("kid-1");
    const cache = makeCacheWithKeys([key]);
    const verifier = createVerifier(cache, now);

    const result = await verifier.verify("x", {});

    expect(result.ok).toBe(false);
    if (!result.ok) expect(result.reason).toMatch(/missing or malformed/);
  });

  it("rejects unsupported signature versions", async () => {
    const key = generateTestKey("kid-1");
    const cache = makeCacheWithKeys([key]);
    const verifier = createVerifier(cache, now);
    const body = "x";
    const headers = {
      ...buildSignedHeaders(key, body, FIXED_NOW_MS),
      "x-hashstream-signature-version": "v999",
    };

    const result = await verifier.verify(body, headers);

    expect(result.ok).toBe(false);
    if (!result.ok) expect(result.reason).toMatch(/unsupported signature version/);
  });

  it("accepts a retired key during rotation overlap", async () => {
    const retired = generateTestKey("old", "retired");
    const active = generateTestKey("new", "active");
    const cache = makeCacheWithKeys([active, retired]);
    const verifier = createVerifier(cache, now);
    const body = "x";
    const headers = buildSignedHeaders(retired, body, FIXED_NOW_MS);

    const result = await verifier.verify(body, headers);

    expect(result.ok).toBe(true);
  });
});
