import type {
  APIGatewayProxyStructuredResultV2,
  LambdaFunctionURLEvent,
} from "aws-lambda";

import { adaptNotificationPayload } from "./adapt-payload";
import { JwksCache } from "./jwks-cache";
import { NotificationPayload } from "./types";
import { createVerifier, fetchJwks, Verifier } from "./verify-signature";

const DATA_VERSION_HEADER = "hashstream-data-version";

let verifier: Verifier | undefined;

function getVerifier(): Verifier {
  if (verifier) return verifier;
  const jwksUrl = requireEnv("JWKS_URL");
  const cache = new JwksCache(() => fetchJwks(jwksUrl));
  verifier = createVerifier(cache);
  return verifier;
}

export const handler = async (
  event: LambdaFunctionURLEvent,
): Promise<APIGatewayProxyStructuredResultV2> => {
  const headers = normalizeHeaders(event.headers);
  const rawBody = decodeBody(event);

  const result = await getVerifier().verify(rawBody, headers);
  if (!result.ok) {
    console.warn("Rejected webhook: %s", result.reason);
    return jsonResponse(401, { status: "rejected", reason: result.reason });
  }

  let payload: NotificationPayload;
  try {
    payload = JSON.parse(rawBody) as NotificationPayload;
  } catch (e) {
    const message = e instanceof Error ? e.message : "unknown JSON parse error";
    console.warn("Rejected webhook: invalid JSON body — %s", message);
    return jsonResponse(400, { status: "rejected", reason: "invalid JSON body" });
  }

  console.log("Received webhook payload: %j", payload);
  const adapted = adaptNotificationPayload(payload, headers[DATA_VERSION_HEADER]);
  console.log("Adapted webhook payload: %j", adapted);

  return jsonResponse(200, { status: "success" });
};

function normalizeHeaders(
  raw: LambdaFunctionURLEvent["headers"],
): Record<string, string | undefined> {
  const normalized: Record<string, string | undefined> = {};
  for (const [key, value] of Object.entries(raw ?? {})) {
    if (value != null) normalized[key.toLowerCase()] = value;
  }
  return normalized;
}

function decodeBody(event: LambdaFunctionURLEvent): string {
  if (!event.body) return "";
  return event.isBase64Encoded
    ? Buffer.from(event.body, "base64").toString("utf-8")
    : event.body;
}

function jsonResponse(statusCode: number, body: unknown): APIGatewayProxyStructuredResultV2 {
  return {
    statusCode,
    headers: { "content-type": "application/json" },
    body: JSON.stringify(body),
  };
}

function requireEnv(name: string): string {
  const value = process.env[name];
  if (!value) {
    throw new Error(`Required environment variable not set: ${name}`);
  }
  return value;
}
