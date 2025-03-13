set -e

sam build
sam local invoke HashstreamConsumerLambda --event events/apigateway_event.json > lambda_output.json
echo "Received output from lambda:"
cat lambda_output.json
echo ""
if ! jq -e '. | select(.statusCode == 200)' lambda_output.json > /dev/null; then
  echo "Error: statusCode is not 200"
  exit 1
fi
if ! jq -e '.body | fromjson | select(.status == "success")' lambda_output.json > /dev/null; then
  echo "Error: Unexpected response body. status should be success"
  exit 1
fi

echo "Lambda output is as valid"
