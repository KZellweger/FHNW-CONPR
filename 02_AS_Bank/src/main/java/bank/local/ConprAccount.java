package bank.local;

import bank.Account;
import bank.InactiveException;
import bank.OverdrawException;

class ConprAccount implements Account {
    private static int id = 0;

    private final Object lock = new Object();

    private final String number;
    private final String owner;
    private double balance;
    private boolean active = true;

    ConprAccount(String owner) {
        this.owner = owner;
        this.number = "CONPR_ACC_" + incrementId();
    }

    private static synchronized int incrementId() {
        return id++;
    }

    @Override
    public double getBalance() {
        synchronized (lock) {
            return balance;
        }
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public boolean isActive() {
        synchronized (lock) {
            return active;
        }
    }

    void passivate() {
        synchronized (lock) {
            active = false;
        }
    }

    @Override
    public void deposit(double amount) throws InactiveException {
        synchronized (lock) {
            if (!active)
                throw new InactiveException("account not active");
            if (amount < 0)
                throw new IllegalArgumentException("negative amount");
            balance += amount;
        }
    }

    @Override
    public void withdraw(double amount) throws InactiveException, OverdrawException {
        synchronized (lock) {
            if (!active)
                throw new InactiveException("account not active");
            if (amount < 0)
                throw new IllegalArgumentException("negative amount");
            if (balance - amount < 0)
                throw new OverdrawException("account cannot be overdrawn");
            balance -= amount;
        }
    }

}