# HashStream Consumer Java

## Overview

This sample application demonstrates how to integrate with HashStream's Hedera smart contract streaming service using webhooks. Built with Spring Boot and AWS Lambda, it provides a ready-to-use template for receiving real-time blockchain notifications and processing smart contract events from the Hedera network.

## Features

- **Ready-to-deploy AWS Lambda function** with API Gateway integration
- **Secure webhook authentication** via API keys
- **Spring Boot framework** for easy customization
- **Complete deployment automation** via AWS SAM

## Quick Start

### Prerequisites

- [Java 21+](https://adoptium.net/)
- [AWS CLI](https://aws.amazon.com/cli/) configured with valid credentials
- [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)

### Build and Deploy

1. **Build the application**

   ```bash
   ./gradlew build
   ```

2. **Deploy to AWS**

   ```bash
   sam build
   sam deploy
   ```

3. **Get your webhook URL**

   ```bash
   echo "https://$(aws cloudformation describe-stack-resources --stack-name hashstream-consumer --logical-resource-id HashstreamConsumerApi --query 'StackResources[0].PhysicalResourceId' --output text).execute-api.$(aws configure get region).amazonaws.com/Prod/hashstream-webhook"
   ```

4. **Get your API key**

   ```bash
   aws apigateway get-api-key --api-key $(aws cloudformation describe-stack-resources --stack-name hashstream-consumer --logical-resource-id HashstreamConsumerApiApiKey --query 'StackResources[0].PhysicalResourceId' --output text) --include-value --query value
   ```

## Connecting to HashStream

### UI (coming soon)

1. Log into your HashStream account
2. Create a new webhook rule
3. Enter your webhook URL from step 3 above
4. Add a custom header `x-api-key` with the value from step 4
5. Enter the smart contract rule
6. Save your rule and start receiving real-time blockchain events!

### API

POST a new Rule to the HashStream Rules API.

```sh
URL: https://mainnet.streams-api.hashstream.xyz/rules or https://testnet.streams-api.hashstream.xyz/rules
Method: POST
Headers: { "Authorization": "<Your HashStream API Key>", "Content-Type": "application/json" },
BODY:
{
  "chain": "hedera",
  "ruleType": 4,
  "predicateValue": "<Your smart contract ID, 0.0.xxxx>",
  "ruleName": "HashStream Consumer Tester (Hbar/HbarX LP)",
  "actionWebhookUrl": "<The Value from Step 3 Above>",
  "actionWebhookCustomHeaders":
  {
    "x-api-key": "<The Value From Step 4 Above>"
  }
}

```

For more information on the Rules API, check out the [API Documentation](https://mainnet.streams-api.hashstream.xyz/docs).

## Customizing Event Processing

The sample code includes a basic handler for webhook events. To process events according to your business logic:

1. Examine the `HashStreamController` class
2. Modify the event handling logic to suit your requirements
3. Rebuild and redeploy

## Local Development

Run unit tests:

```bash
./gradlew test
```

Test with AWS SAM locally:

```bash
./gradlew build
sam build
sam local invoke HashstreamConsumerLambda --event events/apigateway_event.json
```

on Mac, you may need to include an environment variable for DOCKER_HOST

```bash
./gradlew build
sam build
DOCKER_HOST=unix://$HOME/.docker/run/docker.sock sam local invoke HashstreamConsumerLambda --event events/apigateway_event.json
```

## Tear down

`sam delete`

## License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.
