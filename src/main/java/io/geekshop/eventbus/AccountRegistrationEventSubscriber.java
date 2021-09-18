/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.eventbus;

import io.geekshop.email.EmailDetails;
import io.geekshop.email.EmailSender;
import io.geekshop.email.PebbleTemplateService;
import io.geekshop.entity.AuthenticationMethodEntity;
import io.geekshop.eventbus.events.AccountRegistrationEvent;
import io.geekshop.exception.InternalServerError;
import io.geekshop.exception.email.SendEmailException;
import io.geekshop.exception.email.TemplateException;
import io.geekshop.options.ConfigOptions;
import io.geekshop.service.UserService;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 用户注册发送邮件确认事件
 * Created on Nov, 2020 by @author bobo
 */
@Component
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class AccountRegistrationEventSubscriber {
    final static String VERIFICATION_EMAIL_TEMPLATE = "email-verification";
    final static String VERIFICATION_EMAIL_SUBJECT = "Please verify your email address";

    private final ConfigOptions configOptions;
    private final PebbleTemplateService pebbleTemplateService;
    private final UserService userService;
    private final EventBus eventBus;
    private final EmailSender emailSender;

    @PostConstruct
    void init() {
        System.out.println("AccountRegistrationEventSubscriber register");
        eventBus.register(this);
    }

    @Subscribe
    public void onEvent(AccountRegistrationEvent event) {
        log.info("onEvent called event = " + event);

        AuthenticationMethodEntity nativeAuthMethod = null;
        try {
            nativeAuthMethod = userService.getNativeAuthMethodEntityByUserId(event.getUserEntity().getId());
        } catch (InternalServerError ise) {
            log.warn("Error to get native auth method, userId = { " + event.getUserEntity().getId() + " }", ise);
            return;
        }

        if (StringUtils.isEmpty(nativeAuthMethod.getIdentifier())) {
            log.warn("Missing identifier in native auth method, userId = { " + event.getUserEntity().getId() + " }");
            return;
        }

        EmailDetails emailDetails = new EmailDetails(
                event,
                event.getUserEntity().getIdentifier(),
                this.configOptions.getEmailOptions().getDefaultFromEmail());

        emailDetails.setSubject(VERIFICATION_EMAIL_SUBJECT);

        Map<String, Object> model = ImmutableMap.of(
                "verificationToken", nativeAuthMethod.getVerificationToken(),
                "verifyEmailAddressUrl", configOptions.getEmailOptions().getVerifyEmailAddressUrl());

        try {
            String body = this.pebbleTemplateService.mergeTemplateIntoString(VERIFICATION_EMAIL_TEMPLATE, model);
            emailDetails.setBody(body);
        } catch (TemplateException te) {
            log.error("The template file cannot be processed", te);
            throw new SendEmailException("Error while processing the template file with the given model object.", te);
        }
        emailDetails.getModel().putAll(model);// 仅方便测试用

        try {
            emailSender.send(emailDetails);
        } catch (Exception ex) {
            log.error("Exception to send the email", ex);
            throw new SendEmailException("Exception while sending the email.", ex);
        }
    }
}
