package com.jan.web.security.verification;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.eq;

public class EmailServiceTest
{
    public static final String TEMPLATE_LOCATION = "random_template_location";

    @Test
    public void whenSendMailIsCalled_thenEmailIsConstructed()
    {
        ITemplateEngine templateEngine = Mockito.mock(ITemplateEngine.class);
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        EmailService emailService = new EmailService(templateEngine, mailSender);
        MimeMessage message = Mockito.mock(MimeMessage.class);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(message);
        IContext context = Mockito.mock(IContext.class);
        EmailContext emailContext = constructEmailContext(context);

        emailService.sendEmail(emailContext);

        Mockito.verify(templateEngine, Mockito.times(1)).process(eq(TEMPLATE_LOCATION), eq(context));
        Mockito.verify(mailSender, Mockito.times(1)).send(message);
    }

    @Test
    public void whenExceptionOccurred_thenRuntimeExceptionIsThrown() throws MessagingException
    {
        ITemplateEngine templateEngine = Mockito.mock(ITemplateEngine.class);
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        EmailService emailService = new EmailService(templateEngine, mailSender);
        MimeMessage message = Mockito.mock(MimeMessage.class);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(message);
        IContext context = Mockito.mock(IContext.class);
        EmailContext emailContext = constructEmailContext(context);

        Mockito.doThrow(new MessagingException("Random exception.")).when(message).setSubject(Mockito.anyString());

        Assertions.assertThatThrownBy(() -> emailService.sendEmail(emailContext)).hasMessage("Exception occurred while sending email!");
    }

    private EmailContext constructEmailContext(IContext context)
    {
        EmailContext emailContext = new EmailContext();
        emailContext.setTemplateLocation(TEMPLATE_LOCATION);
        emailContext.setTo("user@test.com");
        emailContext.setSubject("Random subject");
        emailContext.setFrom("application@test.com");
        emailContext.setContext(context);
        return emailContext;
    }
}