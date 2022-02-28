package bank.local;

import bank.Account;
import bank.Bank;
import bank.InactiveException;
import bank.OverdrawException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ConprBank implements Bank {

    private final Map<String, ConprAccount> accounts = new ConcurrentHashMap<>();

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
            synchronized (a) {
                if (a.getBalance() != 0 || !a.isActive()) {
                    return false;
                }
                a.passivate();
                return true;
            }
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
        Account[] as;
        if (from.getNumber().compareTo(to.getNumber()) < 0) {
            as = new Account[]{from, to};
        } else if (from.getNumber().compareTo(to.getNumber()) > 0) {
            as = new Account[]{to, from};
        } else {
            throw new IllegalArgumentException("Transfer within the same Account is nonsense");
        }

        synchronized (as[0]) {
            synchronized (as[1]) {
                if (!from.isActive() || !to.isActive()) {
                    throw new InactiveException();
                }
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }
}
