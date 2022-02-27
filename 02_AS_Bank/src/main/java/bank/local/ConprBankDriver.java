/*
 * Copyright (c) 2019 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved.
 */

package bank.local;

/* Simple Server -- not thread safe */

import bank.Bank;

public class ConprBankDriver implements bank.BankDriver {
    private ConprBank bank = null;

    @Override
    public void connect(String[] args) {
        bank = new ConprBank();
    }

    @Override
    public void disconnect() {
        bank = null;
    }

    @Override
    public Bank getBank() {
        return bank;
    }
}


