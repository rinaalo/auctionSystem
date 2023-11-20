import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

public class ServerResponse {
    private String message;
    private byte[] signature;

    public ServerResponse(String message, PrivateKey privateKey) {
        this.message = message;
        try {            
			Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            this.signature = signature.sign();
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			System.exit(-1);
		}
    }
}
