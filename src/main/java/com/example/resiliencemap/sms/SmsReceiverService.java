package com.example.resiliencemap.sms;

import com.example.resiliencemap.sms.model.InboundSmsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsReceiverService {

    private final SmsDispatcher smsDispatcher;

    @RabbitListener(queues = "sms.inbound")
    public void receiveSms(InboundSmsDto sms) {
        log.info("Spring отримав SMS від {}: {}", sms.getFrom(), sms.getText());
        try {
            smsDispatcher.dispatch(sms.getFrom(), sms.getText());
        } catch (Exception e) {
            log.error("Error processing message from {}", sms.getFrom(), e);
        }
    }
}
