package xyz.hashstream.consumer.lambda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import xyz.hashstream.consumer.lambda.model.NotificationPayload;

public class HashStreamModelV1AdapterTest {
  private static NotificationPayload testPayload;
  private final HashStreamModelV1Adapter testObject = new HashStreamModelV1Adapter(new Base64Decoder());

  @BeforeAll
  static void setUp() throws Exception {
    try (InputStream is = HashStreamModelV1Adapter.class
        .getClassLoader()
        .getResourceAsStream("json/notification-payload-v1.json")) {
      testPayload = new ObjectMapper().readValue(is, NotificationPayload.class);
    }
  }

  @Test
  public void testAdaptNotificationPayload_Base64Decoded_ContenetMetadataTransactionHash() {
    var actual = testObject.adaptNotificationPayload(testPayload);

    assertEquals("0x191532f3b80097d591eb7675c7467296909db6e159358901fd6983cccdeda273a293e3491570ea29d0bc2e4266860ef4",
        actual.content().metadata().transactionHash());
  }

  /**
   * Note: This field often doesn't exist. This field was faked in the data for
   * the sake of testing.
   */
  @Test
  void testContractCallResult_Base64Decoded_EvmAddress() {
    var actual = testObject.adaptNotificationPayload(testPayload);

    assertEquals("0xa11e97d447764a98a60b30097043774eb66e56cd",
        actual.content().contractCall().evmAddress());
  }

  @Test
  void testContractCallResult_Base64Decoded_FunctionParameters() {
    var actual = testObject.adaptNotificationPayload(testPayload);

    assertEquals(
        "0xb1dc65a400014cdb0bb46b565ce4e8431cddf234992433fcb7135b8a858077e4b8153fbf0000000000000000000000000000000000000000000000000000000001650001e3e98ef12b83a18dbe0e9d3f58187e28aa8796da956c5bd465a19e33a6895c6a00000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000002200000000000000000000000000000000000000000000000000000000000000280000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001200000000000000000000000000000000000000000000000000000000067d45e74030100020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000012d45bc00000000000000000000000000000000000000000000000000000000012d467100000000000000000000000000000000000000000000000000000000012d4f8000000000000000000000000000000000000000000000000000000000012d4f8000000000000000000000000000000000000000000000000000000000000000025e0cdaa8665fae1fea8e49c67b0764cb2bc5d04d1a494efd8af07176d073d31a22309479d580cf7b43ae0611abee62a6c99a160db584b2c49b5b748a46bfc72e00000000000000000000000000000000000000000000000000000000000000025b9f92bfd26eb27272f83614ceb3592ec244fbf88b9def70830032110783413122c97143597e9ca225f0446acafd40340e8e887f63f9eef0bbe1801837720c88",
        actual.content().contractCall().functionParameters());
  }

  @Test
  void testContractCallResult_Base64Decoded_Bloom() {
    var actual = testObject.adaptNotificationPayload(testPayload);

    assertEquals(
        "0x002002000000000000010200800800000010000000000000000000000000000000080000000000000000000000000000080000000000020008000000400000020000008000080100000000000000102020000008000000000000000000000080000000000a0040000000000040080802000040000000084000000040000000000200000000000001000000100200000000004000008601880000104000000000000000000000000000000100000000000002000000000000000000000000000000000000000000000000000000000000000000000000001000000000400020000000000000004200000000000000100000000000000000100000022000000000",
        actual.content().contractCall().bloom());
  }

  @Test
  void testContractCallResult_Base64Decoded_ContractCallResult() {
    var actual = testObject.adaptNotificationPayload(testPayload);

    assertEquals(
        "0x0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000003e800000000000000000000000000000000000000000000000000000000000002d800000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000104",
        actual.content().contractCall().contractCallResult());
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0x00000000000000000000000000000000000000000000000000000000000003e8",
      "3, 0x00000000000000000000000000000000000000000000000000000000000002d8",
      "14, 0x0000000000000000000000000000000000000000000000000000000000000103"
  })
  void testContractCallAResultLogs_Base64Decoded_Data(int index, String expected) {
    var actual = testObject.adaptNotificationPayload(testPayload);

    assertEquals(
        expected, actual.content().contractCall().logInfo().get(index).data());
  }

  @ParameterizedTest
  @CsvSource({
      "1, 0x00000000000000000001000000080000001000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000000000000000000000040000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000200000000000000100000000000000000100000000000000000",
      "4, 0x00000000000000000000000080000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000080000000040000000000000000000000000000000000000000000000000000200000000000000000000080000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000",
      "12, 0x00000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000010000000000000000000000000000000008000000000000000000000000000000002000000000000000000000040000000000000000000000001000000000000000000004000008400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000100000000000000000"
  })
  void testContractCallAResultLogs_Base64Decoded_Bloom(int index, String expected) {
    var actual = testObject.adaptNotificationPayload(testPayload);

    assertEquals(
        expected, actual.content().contractCall().logInfo().get(index).bloom());
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0x831ac82b07fb396dafef0077cea6e002235d88e63f35cbd5df2c065107f1e74a",
      "0, 2, 0x000000000000000000000000000000000000000000000000000000000053cd38",
      "7, 0, 0x1c411e9a96e071241c2f21f7726b17ae89e3cab4c78be50e062b03a9fffbbad1",
      "14, 2, 0x000000000000000000000000000000000000000000000000000000000053cd38"
  })
  void testContractCallAResultLogs_Topics(int logIndex, int topicIndex, String expected) {
    var actual = testObject.adaptNotificationPayload(testPayload);

    assertEquals(
        expected, actual.content().contractCall().logInfo().get(logIndex).topic().get(topicIndex));
  }

  @Test
  void testAdaptNotificationPayload_RemainingFieldsPreserved() {
    var actual = testObject.adaptNotificationPayload(testPayload);

    // Metadata fields
    assertEquals(testPayload.metadata(), actual.metadata(),
        "Notification metadata should be preserved");

    // Transaction metadata fields
    assertEquals(testPayload.content().metadata().consensusTimestamp(),
        actual.content().metadata().consensusTimestamp(),
        "consensusTimestamp should be preserved");
    assertEquals(testPayload.content().metadata().chargedTxFee(),
        actual.content().metadata().chargedTxFee(),
        "chargedTxFee should be preserved");
    assertEquals(testPayload.content().metadata().maxFee(),
        actual.content().metadata().maxFee(),
        "maxFee should be preserved");
    assertEquals(testPayload.content().metadata().memo(),
        actual.content().metadata().memo(),
        "memo should be preserved");
    assertEquals(testPayload.content().metadata().node(),
        actual.content().metadata().node(),
        "node should be preserved");
    assertEquals(testPayload.content().metadata().nonce(),
        actual.content().metadata().nonce(),
        "nonce should be preserved");
    assertEquals(testPayload.content().metadata().parentConsensusTimestamp(),
        actual.content().metadata().parentConsensusTimestamp(),
        "parentConsensusTimestamp should be preserved");
    assertEquals(testPayload.content().metadata().scheduled(),
        actual.content().metadata().scheduled(),
        "scheduled should be preserved");
    assertEquals(testPayload.content().metadata().transactionId(),
        actual.content().metadata().transactionId(),
        "transactionId should be preserved");
    assertEquals(testPayload.content().metadata().transactionType(),
        actual.content().metadata().transactionType(),
        "transactionType should be preserved");
    assertEquals(testPayload.content().metadata().payerAccountId(),
        actual.content().metadata().payerAccountId(),
        "payerAccountId should be preserved");
    assertEquals(testPayload.content().metadata().validDurationSeconds(),
        actual.content().metadata().validDurationSeconds(),
        "validDurationSeconds should be preserved");
    assertEquals(testPayload.content().metadata().validStartTimestamp(),
        actual.content().metadata().validStartTimestamp(),
        "validStartTimestamp should be preserved");

    // Receipt and transfers
    assertEquals(testPayload.content().receipt(),
        actual.content().receipt(),
        "receipt should be preserved");
    assertEquals(testPayload.content().transfers(),
        actual.content().transfers(),
        "transfers should be preserved");

    // Contract call fields
    assertEquals(testPayload.content().contractCall().contractId(),
        actual.content().contractCall().contractId(),
        "contractId should be preserved");
    assertEquals(testPayload.content().contractCall().gas(),
        actual.content().contractCall().gas(),
        "gas should be preserved");
    assertEquals(testPayload.content().contractCall().amount(),
        actual.content().contractCall().amount(),
        "amount should be preserved");
    assertEquals(testPayload.content().contractCall().gasUsed(),
        actual.content().contractCall().gasUsed(),
        "gasUsed should be preserved");
    assertEquals(testPayload.content().contractCall().errorMessage(),
        actual.content().contractCall().errorMessage(),
        "errorMessage should be preserved");
    assertEquals(testPayload.content().contractCall().senderId(),
        actual.content().contractCall().senderId(),
        "senderId should be preserved");

    // Log info fields (for the first log entry)
    var originalFirstLog = testPayload.content().contractCall().logInfo().get(0);
    var actualFirstLog = actual.content().contractCall().logInfo().get(0);
    assertEquals(originalFirstLog.contractId(), actualFirstLog.contractId(),
        "log contractId should be preserved");
  }
}
