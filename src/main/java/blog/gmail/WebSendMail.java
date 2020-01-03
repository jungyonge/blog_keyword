package blog.gmail;

import blog.jsoup.Volleyball;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

public class WebSendMail
{

    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final String SMTP_PORT = "465";
    private static final String emailMsgTxt = "Gmail SMTP 서버를 사용한 JavaMail 테스트";
    private static final String emailSubjectTxt = "Gmail SMTP 테스트";
    private static final String emailFromAddress = "jungyongee@gmail.com";
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String[] sendTo = { "jungyongee@gmail.com"};


    public void sendSSLMessage(String recipients[], String subject,
                               String message, String from) throws MessagingException, UnsupportedEncodingException {
        boolean debug = true;

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("jungyongee@gmail.com", "ghzkrp153");
                    }
                }
        );

        session.setDebug(debug);

        Message msg = new MimeMessage(session);
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);

//  /-텍스트만 전송하는 경우 아래의 2라인만 추가하면 된다.
//                * 그러나 텍스트와 첨부파일을 함께 전송하는 경우에는 아래의 2라인을 제거하고
//  * 대신에 그 아래의 모든 문장을 추가해야 한다.
//                *-/

        //msg.setContent(message, "text/plain;charset=KSC5601");
        //Transport.send(msg);

//  /- 텍스트와 첨부파일을 함께 전송하는 경우에는 위의 2라인을 제거하고 아래의 모든 라인을 추가한다.*-
//                 Create the message part

        BodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setText("테스트용 메일의 내용입니다.");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
        File file = new File("/Users/imc053/Desktop/xmlFile/위메프 상품수집 수 201912010.xlsx");
        FileDataSource fds = new FileDataSource(file);
        messageBodyPart.setDataHandler(new DataHandler(fds));
        messageBodyPart.setFileName(MimeUtility.encodeText(fds.getName(),"UTF-8","B"));
        multipart.addBodyPart(messageBodyPart);

        // Put parts in message
        msg.setContent(multipart);

        // Send the message
        Transport.send(msg);

        System.out.println("E-mail successfully sent!!");
    }

    public static void main(String[] args) {
        WebSendMail webSendMail = new WebSendMail();
        String[] recipients = {"qjsro1204@naver.com","jungyong_e@naver.com"};
        try {
            webSendMail.sendSSLMessage(recipients, "test", "test", "jungyongee@gmail.com");
        }
         catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}


