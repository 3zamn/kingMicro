package com.king.common.utils.security.crypto;

public class Sha256Hash extends SimpleHash {

    //TODO - complete JavaDoc

    public static final String ALGORITHM_NAME = "SHA-256";

    public Sha256Hash() {
        super(ALGORITHM_NAME);
    }

    public Sha256Hash(Object source) {
        super(ALGORITHM_NAME, source);
    }

    public Sha256Hash(Object source, Object salt) {
        super(ALGORITHM_NAME, source, salt);
    }

    public Sha256Hash(Object source, Object salt, int hashIterations) {
        super(ALGORITHM_NAME, source, salt, hashIterations);
    }

    public static Sha256Hash fromHexString(String hex) {
        Sha256Hash hash = new Sha256Hash();
        hash.setBytes(Hex.decode(hex));
        return hash;
    }

    public static Sha256Hash fromBase64String(String base64) {
        Sha256Hash hash = new Sha256Hash();
        hash.setBytes(Base64.decode(base64));
        return hash;
    }
}
