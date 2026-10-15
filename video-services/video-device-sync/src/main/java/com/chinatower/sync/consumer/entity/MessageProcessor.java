package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageProcessor {
    private final List<String> messages = new ArrayList<>();
    private final Runnable processor;

    public MessageProcessor(Runnable processor) {
        this.processor = processor;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public void processIfNotEmpty() {
        if (!messages.isEmpty()) {
            processor.run();
        }
    }
}