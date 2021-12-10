package com.security;
import dao.UserDaoImpl;
import utils.GlobalSessionDetails;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

public class Utils {
    public static Boolean checkIfUserAlreadyExists(String userId, UserDaoImpl udi) {
        HashMap<String, Object> userInfo = fetchUserHashMap(udi);
        if (!userInfo.isEmpty() && userInfo.containsKey(userId)) {
            return true;
        }
        return false;
    }
    public static String validateUserInput(String fieldName, Scanner sc) {
        String field;
        while((field = sc.nextLine()).isEmpty()) {
            System.out.println(fieldName + " is required!");
        }
        if(fieldName.equals("Username")) {
            GlobalSessionDetails.setLoggedInUsername(field);
        }
        return field;
    }
    public static String encodeWithMD5 (String fieldToBeEncoded) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (byte b : md.digest(fieldToBeEncoded.getBytes())) {
                sb.append(String.format("%02x", b));
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    public static HashMap<String, Object> fetchUserHashMap (UserDaoImpl udi) {
        return udi.readfile("User_Profile.txt");
    }
}
