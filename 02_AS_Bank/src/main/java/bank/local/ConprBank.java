package bank.local;

import bank.Account;
import bank.Bank;
import bank.InactiveException;
import bank.OverdrawException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ConprBank implements Bank {

    private Map<String, ConprAccount> accounts = new HashMap<String, ConprAccount>();

    @Override
    public Set<String> getAccountNumbers() {
        Set<String> activeAccountNumbers = new HashSet<>();
        for (ConprAccount acc : accounts.values()) {
            if (acc.isActive()) {
                activeAccountNumbers.add(acc.getNumber());
            }
        }
        return activeAccountNumbers;
    }

    @Override
    public String createAccount(String owner) {
        final ConprAccount a = new ConprAccount(owner);
        accounts.put(a.getNumber(), a);
        return a.getNumber();
    }

    @Override
    public boolean closeAccount(String number) {
        final ConprAccount a = accounts.get(number);
        if (a != null) {
            if (a.getBalance() != 0 || !a.isActive()) {
                return false;
            }
            a.passivate();
            return true;
        }
        return false;
    }

    @Override
    public Account getAccount(String number) {
        return accounts.get(number);
    }

    @Override
    public void transfer(Account from, Account to, double amount)
            throws IOException, InactiveException, OverdrawException {
        //TODO: May not be synchronized but must be Concurrent
        from.withdraw(amount);
        to.deposit(amount);
    }
}
