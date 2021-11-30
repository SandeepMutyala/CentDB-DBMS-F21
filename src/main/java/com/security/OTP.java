package com.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;


public class OTP {
    int digits ;
    Properties properties;
    private static final String from = "dalhtest@gmail.com";
    private static final String pass = "Daltest@123";
    String to ="";

    public Properties setProperties() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        return properties;
    }

    private static final int[] DP = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    OTP(String to){
        this.digits =6;
        this.properties = setProperties();
        this.to = to;
    }




    public  void generateOTP(byte[] key, long movingFactor, int truncationOffset) {
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(from, pass);

            }

        });
        String result = null;

        byte[] texts = new byte[8];

        for (int i = texts.length - 1; i >= 0; i--) {

                texts[i] = (byte) (movingFactor & 0xff);
                movingFactor >>= 8;
            }

            // compute hmac hash
            byte[] hash = hmac_sha1(key, texts);

            // put selected bytes into result int
        assert hash != null;
        int offset = hash[hash.length - 1] & 0xf;
            if ((0 <= truncationOffset) &&
                    (truncationOffset < (hash.length - 4))) {
                offset = truncationOffset;
            }

            int binary =
                    ((hash[offset] & 0x7f) << 24)
                            | ((hash[offset + 1] & 0xff) << 16)
                            | ((hash[offset + 2] & 0xff) << 8)
                            | (hash[offset + 3] & 0xff);

            int otp = binary % DP[digits];

            result = Integer.toString(otp);

            while (result.length() < digits) {
                result = "0" + result;
            }
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Now set the actual message
            message.setText("This is your OTP" + result);

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
        }


        /**
         * This method uses the JCE to provide the HMAC-SHA-1 algorithm.
         * HMAC computes a Hashed Message Authentication Code and
         * in this case SHA1 is the hash algorithm used.
         *
         * @param keyBytes   the bytes to use for the HMAC-SHA-1 key
         * @param text       the message or text to be authenticated.
         *
         * @throws NoSuchAlgorithmException if no provider makes
         *       either HmacSHA1 or HMAC-SHA-1
         *       digest algorithms available.
         * @throws InvalidKeyException
         *       The secret provided was not a valid HMAC-SHA-1 key.
         *
         */
        private static byte[] hmac_sha1( byte[] keyBytes, byte[] text)
        {
            Mac hmacSha1;
            try {
                try
                {
                    hmacSha1 = Mac.getInstance("HmacSHA1");
                }
                catch (NoSuchAlgorithmException nsae)
                {
                    hmacSha1 = Mac.getInstance("HMAC-SHA-1");
                }

                SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
                hmacSha1.init(macKey);
                return hmacSha1.doFinal(text);
            } catch (NoSuchAlgorithmException | InvalidKeyException nsae) {
            return null;
        }
    }
}