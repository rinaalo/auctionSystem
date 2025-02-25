import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

public class ServerResponse implements Serializable {
    private String message;
    private byte[] signatureArray;

    public ServerResponse(String message, PrivateKey privateKey) {
        this.message = message;
        try {            
			Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            this.signatureArray = signature.sign();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(message.getBytes());
            byte[] digest = md.digest();
            //System.out.println("digest: " + bytesToHex(digest));
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			System.exit(-1);
		}
    }

    public String bytesToHex(byte[] bytes) {
        StringBuilder out = new StringBuilder();
        for (byte b : bytes) {
            out.append(String.format("%02X", b));
        }
        return out.toString();
    }

    public String getMessage() {
        return message;
    }

    public byte[] getSignature() {
        return signatureArray;
    }
}
