# HashStream Consumer (Node / TypeScript)

A sample AWS Lambda demonstrating how to receive and verify HashStream webhooks
in TypeScript. The Lambda is fronted by a Lambda Function URL and verifies
every inbound webhook against HashStream's published JWKS (JSON Web Key Set)
before processing it.

This is a TypeScript port of the [Java sample](../hashstream-consumer-java) with
two differences:

- **Signed webhooks (asymmetric)** — verifies the ECDSA P-256 signature on
  each request instead of trusting a shared API key. See
  [Webhook signatures](#how-webhook-verification-works) below.
- **Lambda Function URL** — uses the built-in Function URL instead of API
  Gateway. One less service to manage; the endpoint is publicly accessible
  but every request is rejected unless it carries a valid HashStream
  signature.

## Quick Start

### Prerequisites

- [Node.js 24+](https://nodejs.org/) (matches the Lambda runtime)
- [AWS CLI](https://aws.amazon.com/cli/) configured with valid credentials
- [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)

### Build and deploy

1. **Install dependencies and build**

   ```bash
   npm install
   npm run build
   ```

2. **Deploy to AWS**

   ```bash
   sam build
   sam deploy --guided
   ```

   On the first deploy you will be prompted for one parameter:

   | Parameter | Value |
   | --- | --- |
   | `HashstreamNetwork` | `mainnet` or `testnet` (Lambda derives the JWKS URL from this) |

3. **Get your webhook URL**

   ```bash
   aws cloudformation describe-stacks \
     --stack-name hashstream-consumer-node \
     --query "Stacks[0].Outputs[?OutputKey=='WebhookUrl'].OutputValue" \
     --output text
   ```

## Connecting to HashStream

POST a new rule to the HashStream Rules API. Note that you no longer need to
attach an API key as a custom header — HashStream signs the request, and this
sample verifies the signature on your behalf.

```sh
URL: https://mainnet.streams-api.hashstream.xyz/rules
Method: POST
Headers:
  Authorization: <your HashStream API key>
  Content-Type: application/json
Body:
{
  "chain": "hedera",
  "ruleType": 4,
  "predicateValue": "<your smart contract id, 0.0.xxxx>",
  "ruleName": "HashStream Consumer (Node) sample",
  "actionWebhookUrl": "<the Function URL from step 3 above>"
}
```

See the [Rules API docs](https://mainnet.streams-api.hashstream.xyz/docs) for
the full schema.

## How webhook verification works

Every outbound HashStream webhook carries four headers:

| Header | Purpose |
| --- | --- |
| `x-hashstream-signature` | Base64-encoded ECDSA P-256 signature (DER) |
| `x-hashstream-signature-timestamp` | Unix milliseconds at signing time |
| `x-hashstream-signature-key-id` | `kid` of the public key that should verify it |
| `x-hashstream-signature-version` | Currently `v1` |

The signed bytes are `${timestampMs}.${rawBody}`. This sample:

1. Loads the JWKS from `https://keys.hashstream.xyz/networks/<network>/.well-known/jwks.json` (public, unauthenticated; network comes from `HASHSTREAM_NETWORK`).
2. Caches it in memory, respecting the response `cache-control: max-age` (with
   a 1-hour fallback). See `src/jwks-cache.ts`.
3. Re-fetches the JWKS exactly once if a webhook arrives with an unknown
   `kid` — this is the rotation signal.
4. Rejects any request whose timestamp is more than 5 minutes from now.
5. Returns `401` to anything missing, expired, or invalid. **Fail-closed.**

See `src/verify-signature.ts` for the verification, and
[stream-notifications-lambdas/docs/webhook-signatures.md](https://github.com/HashStream-LLC/hedera-streams/blob/main/node-projects/apps/stream-notifications-lambdas/docs/webhook-signatures.md)
in the main HashStream repo for the protocol spec.

> **Only the body is authenticated.** Other `x-hashstream-*` headers (event
> id, request id, retry balance) are unsigned advisory metadata. For any
> security or routing decision — which rule this is, which network it came
> from — use values parsed from the verified body (e.g. `metadata.rule.id`,
> `metadata.network`), not request headers.

## Customising payload handling

`src/index.ts` is the Lambda entry point. After signature verification the
sample:

1. Parses the JSON body as a `NotificationPayload`.
2. Adapts the payload (base64 → hex decoding for v1 data — see
   `src/adapt-payload.ts`).
3. Logs both the raw and adapted payloads.
4. Returns `200 { "status": "success" }`.

To plug in your own business logic, replace the body of `handler` after the
verification block.

## Local development

Run the unit tests:

```bash
npm test
```

Type-check only:

```bash
npm run lint
```

Bundle for Lambda:

```bash
sam build
sam local invoke HashstreamConsumerLambda \
  --event events/functionurl_event.json \
  --parameter-overrides HashstreamNetwork=testnet
```

> **Note:** `events/functionurl_event.json` has placeholder signature headers,
> so `sam local invoke` will return a 401. To exercise the happy path locally,
> generate a signature with your own ECDSA P-256 key and host a JWKS at a URL
> the Lambda can reach.

## Tear down

```bash
sam delete --stack-name hashstream-consumer-node
```

## License

This project is licensed under the MIT License — see the [LICENSE](../LICENSE)
file for details.
