package com.jtravan.services;

import com.jtravan.model.TransactionNotification;

/**
 * Created by johnravan on 1/11/17.
 */
public interface TransactionNotificationHandler {
    void handleTransactionNotification(TransactionNotification transactionNotification);
}
