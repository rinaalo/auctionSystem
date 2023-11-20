import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class SignatureVerifier {

    private static SignatureVerifier INSTANCE = null;
    
    private SignatureVerifier() {

    }

    static boolean verify(PublicKey publicKey, ServerResponse response) {
        try {            
			Signature verifySignature = Signature.getInstance("SHA256withRSA");
            verifySignature.initVerify(publicKey);
            verifySignature.update(response.getMessage().getBytes());
            return verifySignature.verify(response.getSignature());
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			System.exit(-1);
		}
        return false;
    }


    public static SignatureVerifier getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SignatureVerifier();
        }
        return INSTANCE;
    }
}