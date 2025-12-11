package com.example.resiliencemap.sms;

import com.example.resiliencemap.sms.model.OutboundSmsDto;
import com.example.resiliencemap.sms.handler.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SmsDispatcher {

    private final Map<String, CommandHandler> handlers;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.android-hash}")
    private String androidHash;

    public SmsDispatcher(List<CommandHandler> handlerList, RabbitTemplate rabbitTemplate) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        h -> h.getTriggerCommand().toUpperCase(),
                        h -> h
                ));
        this.rabbitTemplate = rabbitTemplate;
    }

    public void dispatch(String phone, String smsText) {
        String responsePayload;
        try {
            String[] parts = smsText.trim().split("\\|");
            String cmd = parts[0].toUpperCase();
            CommandHandler handler = handlers.get(cmd);
            if (handler != null) {
                responsePayload = handler.handle(phone, smsText);
                String finalSms = String.format("<#> %s %s", responsePayload, androidHash);
                rabbitTemplate.convertAndSend("sms.outbound", new OutboundSmsDto("id", phone, finalSms));
            } else {
                log.warn("unknown sms command: " + cmd);
            }
        } catch (Exception e) {
            log.error("error", e);
        }
    }

}
