package org.ctc.util;


import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.util.Properties;

import static org.ctc.costant.Constance.RANDON_STRING;

public class JavaMail {

    // 1.step 資料 1.寄件者 2.寄件者電子信箱密碼()  3.收件者 4.信件標題 5.內容
    // GMail user name (just the part before "@gmail.com")
    private String USER_NAME = "s80532s@gmail.com";
    private String PASSWORD = "qdmn pgcx syjr qazt"; // GMail password
    private String RECIPIENT ="s80532s@gmail.com";
    //private static String RECIPIENT = "s80532ss@gmail.com";
    private String SUBJECT ="重設密碼驗證信";
    //private static String SUBJECT = "test";
    private String TXT="test";
    //private static String TXT = "testjavamail";


    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    public static void main(String[] args) {
        JavaMail javaMail=new JavaMail();
        javaMail.sendMail();
    }


    public String getRECIPIENT() {
        return RECIPIENT;
    }

    public void setRECIPIENT(String rECIPIENT) {
        RECIPIENT = rECIPIENT;
    }



    public String getTXT() {
        return TXT;
    }

    public  void setTXT(String tXT) {
        TXT = tXT;
    }

    public void sendMail() {

        Properties prop = System.getProperties();
// 外寄郵件 (SMTP) 伺服器
// smtp.gmail.com
//
// 需要安全資料傳輸層 (SSL)：是
//
// 需要傳輸層安全性 (TLS)：是 (如果可用)
//
// 需要驗證：是
//
// 安全資料傳輸層 (SSL) 通訊埠：465
        //設定連線方式為stmp
        prop.setProperty("mail.transport.protocol", "smtp");
        //設定為gmail
        prop.setProperty("mail.host", "smtp.gmail.com");
        //gmail port為465
        prop.setProperty("mail.smtp.port","465");
        //連線是否需要驗證
        prop.setProperty("mail.smtp.auth","true");
        //設定安全資料傳輸
        prop.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        //設定安全資料傳輸port
        prop.setProperty("mail.smtp.socketFactory.port","465");

        prop.setProperty("mail.debug", "true");

        Session session=Session.getDefaultInstance(prop, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // TODO Auto-generated method stub
                return new PasswordAuthentication(USER_NAME, PASSWORD);
            }

        });

        MimeMessage message=new MimeMessage(session);

        try {
            message.setSender(new InternetAddress(USER_NAME));
            message.setRecipient(RecipientType.TO, new InternetAddress(RECIPIENT));
            message.setSubject(SUBJECT);
            message.setContent(TXT, "text/html;charset=utf-8");

            Transport transport=session.getTransport();
            transport.send(message);
            transport.close();




        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




    }
    public static String genAuthCode() {
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<7;i++) {
            stringBuffer.append(RANDON_STRING[(int)(Math.random()*60+1)]);
        }
        return stringBuffer.toString();
    }



}