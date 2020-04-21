import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.util.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalKmsTest {

  private static AWSKMS kmsClient;

  @BeforeAll
  static void init() {

    // EndpointにローカルKMSを指定.RegionはどこでもOK
    AwsClientBuilder.EndpointConfiguration endpointConfig =
        new AwsClientBuilder.EndpointConfiguration("http://localhost:8080/", "ap-northeast-1");

    kmsClient = AWSKMSClientBuilder.standard().withEndpointConfiguration(endpointConfig).build();
  }

  /** 暗号化する文字列 */
  private static final String PLAIN_TEXT = "nikusube!";

  @Test
  void encrypt() {

    EncryptRequest req =
        new EncryptRequest()
            .withKeyId("bc436485-5092-42b8-92a3-0aa8b93536dc")
            .withPlaintext(ByteBuffer.wrap(PLAIN_TEXT.getBytes()));

    ByteBuffer ciphertextBlob = kmsClient.encrypt(req).getCiphertextBlob();

    // 暗号化した文字列をBase64にエンコード
    // UTF-8やISO-8859だと複合できなくなるので注意
    //
    // また、暗号化された文字列は毎回変わるためAssertできない
    String encryptText = Base64.encodeAsString(ciphertextBlob.array());

    // 暗号化した文字列を複合化して、元の値とassert
    DecryptRequest decryptRequest =
        new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(Base64.decode(encryptText)));
    // UTF-8でデコード
    String decryptText =
        StandardCharsets.UTF_8
            .decode(kmsClient.decrypt(decryptRequest).getPlaintext())
            .toString();

    assertEquals(PLAIN_TEXT, decryptText);

    // 前もって生成しておいた暗号化文字列を復号することも可能
    encryptText =
        "S2Fybjphd3M6a21zOmV1LXdlc3QtMjoxMTExMjIyMjMzMzM6a2V5L2JjNDM2NDg1LTUwOTItNDJiOC05MmEzLTBhYThiOTM1MzZkYwAAAAAg1ydEo02IztvvGzC7xHRvFTyLdDpXHTLsXMUB3c7cimIYsZ2V";
    decryptRequest =
        new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(Base64.decode(encryptText)));
    decryptText =
        StandardCharsets.ISO_8859_1
            .decode(kmsClient.decrypt(decryptRequest).getPlaintext())
            .toString();
    assertEquals(PLAIN_TEXT, decryptText);
  }
}
