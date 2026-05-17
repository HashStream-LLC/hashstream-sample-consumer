import { createPublicKey, createVerify, KeyObject } from "node:crypto";

import { Jwk, Jwks, JwksCache, parseMaxAgeSeconds } from "./jwks-cache";

const SIGNATURE_MAX_SKEW_MS = 5 * 60 * 1000;

export interface SignatureHeaders {
  signature: string;
  timestampMs: number;
  keyId: string;
  version: string;
}

export type VerifyResult =
  | { ok: true }
  | { ok: false; reason: string };

export function extractSignatureHeaders(
  headers: Record<string, string | undefined>,
): SignatureHeaders | null {
  const signature = headers["x-hashstream-signature"];
  const timestamp = headers["x-hashstream-signature-timestamp"];
  const keyId = headers["x-hashstream-signature-key-id"];
  const version = headers["x-hashstream-signature-version"];
  if (!signature || !timestamp || !keyId || !version) return null;
  const timestampMs = Number(timestamp);
  if (!Number.isFinite(timestampMs)) return null;
  return { signature, timestampMs, keyId, version };
}

export interface Verifier {
  verify(rawBody: string, headers: Record<string, string | undefined>): Promise<VerifyResult>;
}

export function createVerifier(
  jwksCache: JwksCache,
  now: () => number = Date.now,
): Verifier {
  return {
    async verify(rawBody, headers) {
      const sigHeaders = extractSignatureHeaders(headers);
      if (!sigHeaders) {
        return { ok: false, reason: "missing or malformed signature headers" };
      }

      if (sigHeaders.version !== "v1") {
        return { ok: false, reason: `unsupported signature version: ${sigHeaders.version}` };
      }

      if (Math.abs(now() - sigHeaders.timestampMs) > SIGNATURE_MAX_SKEW_MS) {
        return { ok: false, reason: "signature timestamp outside acceptable skew window" };
      }

      const jwk = await jwksCache.getKey(sigHeaders.keyId);
      if (!jwk) {
        return { ok: false, reason: `unknown signing key id: ${sigHeaders.keyId}` };
      }

      const publicKey = jwkToPublicKey(jwk);
      const verifier = createVerify("SHA256");
      verifier.update(`${sigHeaders.timestampMs}.${rawBody}`);
      verifier.end();
      const sigBytes = Buffer.from(sigHeaders.signature, "base64");
      const valid = verifier.verify(publicKey, sigBytes);
      return valid ? { ok: true } : { ok: false, reason: "signature did not verify" };
    },
  };
}

function jwkToPublicKey(jwk: Jwk): KeyObject {
  return createPublicKey({ key: jwk as unknown as Record<string, unknown>, format: "jwk" });
}

export async function fetchJwks(
  url: string,
): Promise<{ jwks: Jwks; cacheControlMaxAgeSeconds: number | null }> {
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Failed to fetch JWKS from ${url}: ${response.status} ${response.statusText}`);
  }
  const jwks = (await response.json()) as Jwks;
  return {
    jwks,
    cacheControlMaxAgeSeconds: parseMaxAgeSeconds(response.headers.get("cache-control")),
  };
}
