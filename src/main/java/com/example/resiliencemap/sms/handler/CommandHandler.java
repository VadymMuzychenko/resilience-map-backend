package com.example.resiliencemap.sms.handler;

public interface CommandHandler {
    String getTriggerCommand();

    String handle(String phone, String rawSmsText);
}
