package com.example.hemankita.myrxproject.stages;

import android.util.Base64;
import android.util.Log;

import com.example.hemankita.myrxproject.Crypto;
import com.example.hemankita.myrxproject.WebHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Hemankita on 8/7/2016.
 */
public class SendMessage implements Func1<Integer, Observable<String>> {
    Object messageReference;
    PublicKey recipientKey;
    String sender;
    String recipient;
    String subjectLine;
    String body;
    Long bornOnDate;
    Long timeToLive;

    public SendMessage(Object messageReference,
                            PublicKey recipientKey,
                            String sender,
                            String recipient,
                            String subjectLine,
                            String body,
                            Long bornOnDate,
                            Long timeToLive){
        this.messageReference=messageReference;
        Log.d("recipienttt"," "+recipientKey);
        this. recipientKey=recipientKey;
        Log.d("recipienttt"," "+sender);
        this.sender=sender;
        Log.d("recipienttt"," "+recipient);
        this.recipient=recipient;
        Log.d("recipienttt"," "+subjectLine);
        this.subjectLine=subjectLine;
        Log.d("recipienttt"," "+body);
        this.body=body;
        Log.d("recipienttt"," "+bornOnDate);
        this.bornOnDate=bornOnDate;
        Log.d("recipienttt"," "+timeToLive);
        this.timeToLive=timeToLive;
    }

    public Observable<String> call(Integer integer) {
        try {
            String status;


            SecretKey aesKey = Crypto.createAESKey();
            byte[] aesKeyBytes = aesKey.getEncoded();
            if(aesKeyBytes==null){Log.d("tag","AES key failed (this should never happen)");
                //sendSendMessageFailed(messageReference,"AES key failed");
                // return;
            }
            Log.d("tag"," aes keyyy "+aesKeyBytes+"  reciepent keyy "+ recipientKey);
            String base64encryptedAESKey =
                    Base64.encodeToString(Crypto.encryptRSA(aesKeyBytes,recipientKey),
                            Base64.NO_WRAP);

            JSONObject userDetails = new JSONObject();
            userDetails.put( "aes-key", base64encryptedAESKey);
            Log.i("aes-key",base64encryptedAESKey);
            userDetails.put("sender",  base64AESEncrypted(sender, aesKey));
            Log.i("sender", base64AESEncrypted(sender, aesKey));
            userDetails.put("recipient",  base64AESEncrypted(recipient, aesKey));
            Log.i("recipient", base64AESEncrypted(recipient, aesKey));
            userDetails.put("subject-line",  base64AESEncrypted(subjectLine, aesKey));
            Log.i("subject-line", base64AESEncrypted(subjectLine, aesKey));
            userDetails.put("body",  base64AESEncrypted(body, aesKey));
            Log.i("body",base64AESEncrypted(body, aesKey));
            userDetails.put("born-on-date",  base64AESEncrypted(bornOnDate.toString(), aesKey));
            Log.i("born-on-date",base64AESEncrypted(bornOnDate.toString(), aesKey));
            userDetails.put("time-to-live",  base64AESEncrypted(timeToLive.toString(), aesKey));
            Log.i("time-to-live",base64AESEncrypted(timeToLive.toString(), aesKey));

            JSONObject response = WebHelper.JSONPut("http://129.115.27.54:25666"+"/send-message/"+this.recipient,userDetails);

            if (response.getString("status").equals("ok")) {
                status="success";
                Log.d("Success","message sent successfully");
            } else {
                status="failed";
                Log.d("Failed","message not  sent ");
            }
            return Observable.just(status);
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }


    }


    private String base64AESEncrypted(String clearText, SecretKey aesKey){
        try {
            return Base64.encodeToString(Crypto.encryptAES(clearText.getBytes("UTF-8"),aesKey), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }



}
