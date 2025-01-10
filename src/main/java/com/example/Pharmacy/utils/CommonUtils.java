package com.example.Pharmacy.utils;

import java.util.Random;


public class CommonUtils {

    /**
     * Generate a random ID
     *
     * @return
     */
    public static int generateID() {
        Random random = new Random();
        // Generate a random number with length up to 7 digits
        int maxDigits = 7;
        int upperLimit = (int) Math.pow(10, maxDigits) - 1;
        return random.nextInt(upperLimit + 1); // Generate a random number between 0 and 10^7 - 1
    }
}
