package xyz.hashstream.consumer.lambda.model;

public record NotificationMetadata(
    NotificationRuleMetadata rule,
    String network,
    String sentinelTimestamp,
    String timeSinceConsensus
) {}

/*
"metadata": {
    "rule": {
      "id": "43f15b14-2a1b-4aaa-8e06-9db748b5a7d4",
      "name": "Testnet HBAR/Sauce Pool (LWorks)",
      "type": 4,
      "predicateValue": "0.0.1414040",
      "chain": "hedera"
    },
    "network": "testnet",
    "sentinelTimestamp": "1739388160.584747050",
    "timeSinceConsensus": "3.120289827"
  }
 */
