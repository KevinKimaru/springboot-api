package com.microfundit.listener;

import com.microfundit.model.Transaction;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Component
@RepositoryEventHandler(Transaction.class)
public class TransactionEventHandler {
    @HandleBeforeCreate
    private void setDefaultsBeforeAddingTransaction(Transaction transaction) {

    }
}
