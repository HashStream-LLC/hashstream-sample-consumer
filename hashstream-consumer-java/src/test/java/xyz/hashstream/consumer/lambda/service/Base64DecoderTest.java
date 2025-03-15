package xyz.hashstream.consumer.lambda.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Base64DecoderTest {

  private final Base64Decoder base64Decoder = new Base64Decoder();

  @Test
  public void testDecodeValidBase64AsString() {
    String original = "Hello World!";
    // Encode the original string in Base64 to simulate input
    String base64Encoded = java.util.Base64.getEncoder()
        .encodeToString(original.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    String result = base64Decoder.decodeBase64AsString(base64Encoded);
    assertEquals(original, result, "The decoded string should match the original string.");
  }

  @Test
  public void testDecodeEmptyStringAsString() {
    String result = base64Decoder.decodeBase64AsString("");
    assertEquals("", result, "Decoding an empty string should return an empty string.");
  }

  @Test
  public void testDecodeInvalidBase64ThrowsException() {
    String invalidBase64 = "!!! Not Base64 !!!";
    assertThrows(IllegalArgumentException.class, () -> {
      base64Decoder.decodeBase64AsString(invalidBase64);
    }, "Decoding an invalid Base64 string should throw an IllegalArgumentException.");
  }

  @Test
  public void testDecodeBase64AsHex() {
    String expectedTransactionHash = "0x191532f3b80097d591eb7675c7467296909db6e159358901fd6983cccdeda273a293e3491570ea29d0bc2e4266860ef4";
    String encodedTransactionHash = "GRUy87gAl9WR63Z1x0ZylpCdtuFZNYkB/WmDzM3tonOik+NJFXDqKdC8LkJmhg70";
    String result = base64Decoder.decodeBase64AsHex(encodedTransactionHash);
    assertEquals(expectedTransactionHash, result, "The decoded string should match the original hex string.");
  }

  @Test
  public void testDecodeEmptyStringAsHex() {
    String result = base64Decoder.decodeBase64AsHex("");
    assertEquals("0x", result, "Decoding an empty string should return '0x'.");
  }

  @Test
  public void testDecodeInvalidBase64AsHexThrowsException() {
    String invalidBase64 = "!!! Not Base64 !!!";
    assertThrows(IllegalArgumentException.class, () -> {
      base64Decoder.decodeBase64AsHex(invalidBase64);
    }, "Decoding an invalid Base64 string should throw an IllegalArgumentException.");
  }
}
