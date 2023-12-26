package cn.sdack.go.auth;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@SpringBootTest
class AuthorizationApplicationTests {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        String s = "4ca2c1406483031a7c4df8407c";
        String encode = passwordEncoder.encode(s);
        System.out.println(encode);
    }

    void test1() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] code = new byte[128];
        secureRandom.nextBytes(code);
        String verifier = Base64.encodeBase64URLSafeString(code);
        System.out.println(verifier);
        byte[] verifierByte = verifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(verifierByte);
        String challenge = Base64.encodeBase64URLSafeString(digest);
        System.out.println(challenge);
    }
}
