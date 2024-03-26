package srt.cloud.framework.dbswitch.pgwriter.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringUtils {

  private static Charset utf8Charset = StandardCharsets.UTF_8;

  private StringUtils() {
  }

  public static boolean isNullOrWhiteSpace(String input) {
    return input == null || input.trim().length() == 0;
  }

  public static byte[] getUtf8Bytes(String value) {
    return value.getBytes(utf8Charset);
  }

  public static String removeNullCharacter(String data) {
    if (data == null) {
      return data;
    }

    return data.replaceAll("\u0000", "");
  }
}
