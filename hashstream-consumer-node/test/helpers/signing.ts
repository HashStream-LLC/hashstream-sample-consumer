import {
  createPrivateKey,
  createSign,
  generateKeyPairSync,
  KeyObject,
} from "node:crypto";

import { Jwk } from "../../src/jwks-cache";

export interface TestKey {
  jwk: Jwk;
  privateKey: KeyObject;
  kid: string;
}

export function generateTestKey(kid: string, status: Jwk["status"] = "active"): TestKey {
  const { publicKey, privateKey } = generateKeyPairSync("ec", { namedCurve: "P-256" });
  const publicJwk = publicKey.export({ format: "jwk" });
  const jwk: Jwk = {
    kty: "EC",
    crv: "P-256",
    x: publicJwk.x ?? "",
    y: publicJwk.y ?? "",
    kid,
    use: "sig",
    alg: "ES256",
    status,
  };
  return { jwk, privateKey: createPrivateKey(privateKey.export({ format: "pem", type: "pkcs8" })), kid };
}

export function signBody(privateKey: KeyObject, timestampMs: number, body: string): string {
  const signer = createSign("SHA256");
  signer.update(`${timestampMs}.${body}`);
  signer.end();
  return signer.sign(privateKey).toString("base64");
}

export function buildSignedHeaders(
  key: TestKey,
  body: string,
  timestampMs: number = Date.now(),
): Record<string, string> {
  return {
    "x-hashstream-signature": signBody(key.privateKey, timestampMs, body),
    "x-hashstream-signature-timestamp": String(timestampMs),
    "x-hashstream-signature-key-id": key.kid,
    "x-hashstream-signature-version": "v1",
  };
}
