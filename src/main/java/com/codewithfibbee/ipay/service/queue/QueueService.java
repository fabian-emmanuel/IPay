package com.codewithfibbee.ipay.service.queue;

import com.codewithfibbee.ipay.payloads.request.BankTransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueService {
    private final JmsTemplate jmsTemplate;

    public void enqueueTransaction(BankTransferDto request) {
        enqueue("TRANSACTION_QUEUE", request);
    }

    @Async
    public void enqueue(final String queue, Object o) {
        jmsTemplate.convertAndSend(queue, o);
    }
}
