/*
 * Web-Snapshot
 * Copyright (C) 2008-2014  Open-S Company
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact us by mail: open-s AT open-s DOT com
 */
package org.opens.websnapshot.service.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;

/**
 *
 * @author alingua
 */
public class ServiceCallAbuse {

    private int maxNumberOfRequestByInterval = 10;
    public void setMaxNumberOfRequestByInterval(int maxNumberOfRequestByInterval) {
        this.maxNumberOfRequestByInterval = maxNumberOfRequestByInterval;
    }

    private long interval = 60000;
    public void setInterval(long interval) {
        this.interval = interval;
    }
    
    private List<Long> serviceCalls = new LinkedList<Long>();
    private EmailSender emailSender = new EmailSender();
    
    private Collection<String> emails;
    public void setEmails(String emails) {
        this.emails = Arrays.asList(emails.split(";"));
    }
            
    /*
     * Cette méthode est appelée à chaque fois (et avant) qu'une méthode du
     * package org.opens.tanaguru.service.* est interceptée
     */
    public void imageCreationEntry(JoinPoint joinPoint) {
        Logger.getLogger(this.getClass()).info("imageCreationEntry " + joinPoint.getKind());
        Logger.getLogger(this.getClass()).info("imageCreationEntry " + joinPoint.toLongString());
        Logger.getLogger(this.getClass()).info("imageCreationEntry " + joinPoint.getSignature().toLongString());
        Logger.getLogger(this.getClass()).info("imageCreationEntry " + joinPoint.getArgs());
    }

    /*
     * Cette méthode est appelée à chaque fois (et après) qu'une méthode du
     * package org.opens.tanaguru.service.* est interceptée.
     * Elle reçoit en argument 'result' qui est le retour de la méthode
     * interceptée.
     */
    public void imageCreationExit(JoinPoint joinPoint, StaticPart staticPart, Object result) {
        Logger.getLogger(this.getClass()).info("imageCreationExit " + joinPoint.getKind());
        Logger.getLogger(this.getClass()).info("imageCreationExit " + joinPoint.toLongString());
        Logger.getLogger(this.getClass()).info("imageCreationExit " + joinPoint.getSignature().toLongString());
        Logger.getLogger(this.getClass()).info("imageCreationExit " + joinPoint.getArgs());
        Logger.getLogger(this.getClass()).info("imageCreationExit " + result.getClass());
        long currentTime = System.currentTimeMillis();
        serviceCalls.add(currentTime);
        cleanUpServiceCall(currentTime);
        if (serviceCalls.size() >= maxNumberOfRequestByInterval) {
            sendEmail();
        }
    }

    /**
     *
     * @param currentTime
     */
    private void cleanUpServiceCall(long currentTime) {
        Iterator<Long> iter = serviceCalls.iterator();
        while (iter.hasNext()) {
            if ((iter.next() - currentTime) > interval) {
                iter.remove();
            } else {
                break;
            }
        }
    }

    /**
     * 
     */
    private void sendEmail() {
        emailSender.sendEmail(
                "websnaphot-service@tanaguru.com", 
                emails, 
                "[Tanaguru] WebSnapshot service abuse", 
                serviceCalls.size() + " image creation requests have been "
                + "received during the last "+ interval +" ms."
                + "This may reflect an abusing usage of the service. "
                + "Please take a look");
    }

    /**
     * 
     */
    private class EmailSender {

        private final Logger logger = Logger.getLogger(EmailSender.class);
        private static final String SMTP_HOST = "localhost";
        private static final String CONTENT_TYPE_KEY = "Content-Type";
        private static final String FULL_CHARSET_KEY = "text/plain; charset=UTF-8";
        private static final String CHARSET_KEY = "UTF-8";
        private String smtpHost = SMTP_HOST;
        private String userName = "";
        private String password = "";

        /**
         *
         * @param emailFrom
         * @param emailToSet
         * @param emailSubject
         * @param emailContent
         */
        public void sendEmail(String emailFrom, Collection<String> emailToSet,
                String emailSubject, String emailContent) {
            boolean debug = false;

            // Set the host smtp address
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            // create some properties and get the default Session
            Session session = Session.getInstance(props);
            session.setDebug(debug);
            try {
                Transport t = session.getTransport("smtp");
                t.connect(smtpHost, userName, password);

                // create a message
                MimeMessage msg = new MimeMessage(session);

                // set the from and to address
//                InternetAddress addressFrom;
                try {
                    // Used default from address is passed one is null or empty or
                    // blank
                    msg.setFrom(new InternetAddress(emailFrom));
                    Address[] recipients = new InternetAddress[emailToSet.size()];
                    int i = 0;
                    for (String emailTo : emailToSet) {
                        recipients[i] = new InternetAddress(emailTo);
                        i++;
                    }
                    msg.setRecipients(Message.RecipientType.TO, recipients);

                    // Setting the Subject
                    msg.setSubject(emailSubject, CHARSET_KEY);

                    // Setting content and charset (warning: both declarations of
                    // charset are needed)
                    msg.setHeader(CONTENT_TYPE_KEY, FULL_CHARSET_KEY);
                    logger.debug("emailContent " + emailContent);
                    msg.setContent(emailContent, FULL_CHARSET_KEY);
                    try {
                        logger.debug("emailContent from message object "
                                + msg.getContent().toString());
                    } catch (IOException ex) {
                        logger.warn(ex);
                    } catch (MessagingException ex) {
                        logger.warn(ex);
                    }

                    t.sendMessage(msg, msg.getAllRecipients());
                } catch (AddressException ex) {
                    logger.warn(ex.getMessage());
                }
            } catch (MessagingException e) {
                logger.warn(e.getMessage());
            }
        }
    }
}