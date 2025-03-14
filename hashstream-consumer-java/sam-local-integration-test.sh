set -e

mkdir -p output
output_file="output/same-local-integration-out.json"

sam build
sam local invoke HashstreamConsumerLambda --event events/apigateway_event.json > $output_file
echo "Received output from lambda:"
cat $output_file
echo ""
if ! jq -e '. | select(.statusCode == 200)' $output_file > /dev/null; then
  echo "Error: statusCode is not 200"
  exit 1
fi
if ! jq -e '.body | fromjson | select(.status == "success")' $output_file > /dev/null; then
  echo "Error: Unexpected response body. status should be success"
  exit 1
fi

echo "Lambda output is as valid"
