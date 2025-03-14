package xyz.hashstream.consumer.lambda.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.springframework.stereotype.Service;

@Service
public class Base64Decoder {
  private final Decoder decoder = Base64.getDecoder();

  public String decodeBase64AsString(String base64String) {
    byte[] decodedBytes = decoder.decode(base64String);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  public String decodeBase64AsHex(String base64String) {
    byte[] decodedBytes = decoder.decode(base64String);
    StringBuilder hexString = new StringBuilder(2 + decodedBytes.length * 2);
    hexString.append("0x");
    for (byte b : decodedBytes) {
      hexString.append(Character.forDigit((b >> 4) & 0xF, 16));
      hexString.append(Character.forDigit((b & 0xF), 16));
    }
    return hexString.toString();

  }
}
