package latch;

import java.util.concurrent.CountDownLatch;

public class Restaurant {

    public static void main(String[] args) {
        int nrGuests = 20;
        int nrCooks = 4;
        CountDownLatch cookingLatch = new CountDownLatch(nrCooks);
        CountDownLatch eatingLatch = new CountDownLatch(nrGuests);


        for (int i = 0; i < nrCooks; i++) {
            new Cook(cookingLatch).start();
        }
        for (int i = 0; i < nrGuests; i++) {
            new Guest(cookingLatch, eatingLatch).start();
        }

        new DishWasher(eatingLatch).start();
    }


    static class Cook extends Thread {
        CountDownLatch latch;

        public Cook(CountDownLatch cookingLatch) {
            latch = cookingLatch;
        }

        @Override
        public void run() {
            System.out.println("Start Cooking..");
            try {
                sleep((int) (Math.random() * 5000));
            } catch (InterruptedException e) {
            }
            System.out.println("Meal is ready");
            latch.countDown();
        }
    }
    
    static class Guest extends Thread {

        CountDownLatch cookingLatch;
        CountDownLatch eatingLatch;

        public Guest(CountDownLatch cookingLatch, CountDownLatch eatingLatch) {
            this.cookingLatch = cookingLatch;
            this.eatingLatch = eatingLatch;
        }

        @Override
        public void run() {
            try {
                sleep(1000);
                System.out.println("Entering restaurant and placing order.");
                cookingLatch.await();
                System.out.println("Enjoying meal.");
                sleep((int) (Math.random() * 1000));
                System.out.println("Meal was excellent!");
                eatingLatch.countDown();
            } catch (InterruptedException e) {
            }
        }
    }


    static class DishWasher extends Thread {
        CountDownLatch latch;

        public DishWasher(CountDownLatch eatingLatch) {
            latch = eatingLatch;
        }

        @Override
        public void run() {
            try {
                System.out.println("Waiting for dirty dishes.");
                latch.await();
                System.out.println("Washing dishes.");
                sleep(0);
            } catch (InterruptedException e) {
            }
        }
    }
}
