package com.example.entrymanagement;

import android.content.Context;
import android.os.AsyncTask;

import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailAsyncTask extends AsyncTask<Void,Void,Void> {

    private Context context;
    private Session session;
    private String email,subject,message;

    public GMailAsyncTask(Context context, String email, String subject, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... voids) {


        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");

        session = Session.getDefaultInstance(properties,null);
        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.setSubject(subject);
            mm.setText(message);

            Transport transport =session.getTransport("smtp");
            transport.connect("smtp.gmail.com",GMailConfiguration.EMAIL,GMailConfiguration.PASSWORD);
            transport.sendMessage(mm,mm.getAllRecipients());

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();

    }
}
