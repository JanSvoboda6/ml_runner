package com.jan.web.security.verification;

import org.thymeleaf.context.IContext;

/**
 * Class providing the basic information when sending an email.
 */
public class EmailContext
{
    private String from;
    private String to;
    private String subject;
    private String templateLocation;
    private IContext context;

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getTemplateLocation()
    {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation)
    {
        this.templateLocation = templateLocation;
    }

    public IContext getContext()
    {
        return context;
    }

    public void setContext(IContext context)
    {
        this.context = context;
    }
}