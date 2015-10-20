package com.tr.batch.jobs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.tr.batch.EmailCompletion;

public class EmailItemProcessor implements ItemProcessor<EmailCompletion, EmailCompletion> {

    private static final Logger log = LoggerFactory.getLogger(EmailItemProcessor.class);

    @Override
    public EmailCompletion process(final EmailCompletion EmailCompletion) throws Exception {
        final String firstName = EmailCompletion.getFirstName().toUpperCase();
        final String lastName = EmailCompletion.getLastName().toUpperCase();

        final EmailCompletion transformedEmailCompletion = new EmailCompletion(firstName, lastName);

        log.info("Converting (" + EmailCompletion + ") into (" + transformedEmailCompletion + ")");

        return transformedEmailCompletion;
    }

}