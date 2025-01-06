package com.example.Pharmacy.utils;

import java.util.Random;


public class CommonUtils {
    public static int generateRandomNumber(int length) throws Exception {
        if (length < 3 || length > 16) {
            throw new Exception("");
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // Generate a random digit from 0 to 9
            int digit = random.nextInt(10);
            sb.append(digit);
        }
        return Integer.parseInt(sb.toString());
    }


}
