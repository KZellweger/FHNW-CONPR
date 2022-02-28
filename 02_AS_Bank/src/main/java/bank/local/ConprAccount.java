package bank.local;

import bank.Account;
import bank.InactiveException;
import bank.OverdrawException;

class ConprAccount implements Account {
    private static int id = 0;

    private final Object readLock = new Object();
    private final Object writeLock = new Object();

    private String number;
    private String owner;
    private double balance;
    private boolean active = true;

    ConprAccount(String owner) {
        this.owner = owner;
        this.number = "CONPR_ACC_" + id++;
    }

    @Override
    public double getBalance() {
        synchronized (readLock) {
            return balance;
        }
    }

    @Override
    public String getOwner() {
        synchronized (readLock) {
            return owner;
        }
    }

    @Override
    public String getNumber() {
        synchronized (readLock) {
            return number;
        }
    }

    @Override
    public boolean isActive() {
        synchronized (readLock) {
            return active;
        }
    }

    void passivate() {
        synchronized (writeLock) {
            active = false;
        }
    }

    @Override
    public void deposit(double amount) throws InactiveException {
        synchronized (writeLock) {
            if (!active)
                throw new InactiveException("account not active");
            if (amount < 0)
                throw new IllegalArgumentException("negative amount");
            balance += amount;
        }
    }

    @Override
    public void withdraw(double amount) throws InactiveException, OverdrawException {
        synchronized (writeLock) {
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