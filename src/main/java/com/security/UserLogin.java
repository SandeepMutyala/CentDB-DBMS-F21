package com.security;

import utils.Constants;
import dao.UserDaoImpl;
import model.User;
import utils.GlobalSessionDetails;

import java.util.HashMap;
import java.util.Scanner;

public class UserLogin {
    static Scanner sc = new Scanner(System.in);
    public static Boolean userLogin() {
        UserDaoImpl udi = new UserDaoImpl();
        System.out.println(Constants.USERNAME_FIELD);
        String username = Utils.encodeWithMD5(Utils.validateUserInput("Username", sc));
        System.out.println(Constants.PASSWORD_FIELD);
        String password = Utils.encodeWithMD5(Utils.validateUserInput("Password", sc));
        String userId = username + password;

        if (!Utils.checkIfUserAlreadyExists(userId, udi)) {
            System.out.println(Constants.USER_CREDENTIALS_NOT_FOUND);
            return false;
        } else {
            if(validateUserLogin(userId, udi)){
                GlobalSessionDetails.loggedInUsername=username.trim();
                return true;
            }
            return false;
        }
    }

    private static Boolean validateUserLogin(String userId, UserDaoImpl udi) {
        HashMap<String, Object> userInfo = Utils.fetchUserHashMap(udi);
        User user = (User) userInfo.get(userId);
        System.out.println(Constants.USER_LOGIN_ANSWER_QUESTIONS_MESSAGE);
        System.out.println(user.getSecurity_question1());
        if (!user.getSecurity_answer1().equalsIgnoreCase(Utils.validateUserInput("Security Answer 1", sc))) {
            System.out.println(Constants.USER_LOGIN_FAILED);
            System.out.println(Constants.USER_INCORRECT_SECURITY_ANSWER_MESSAGE);
            return false;
        }
        System.out.println(user.getSecurity_question2());
        if (!user.getSecurity_answer2().equalsIgnoreCase(Utils.validateUserInput("Security Answer 2", sc))) {
            System.out.println(Constants.USER_LOGIN_FAILED);
            System.out.println(Constants.USER_INCORRECT_SECURITY_ANSWER_MESSAGE);
            return false;
        }
        System.out.println(user.getSecurity_question3());
        if (!user.getSecurity_answer3().equalsIgnoreCase(Utils.validateUserInput("Security Answer 3", sc))) {
            System.out.println(Constants.USER_LOGIN_FAILED);
            System.out.println(Constants.USER_INCORRECT_SECURITY_ANSWER_MESSAGE);
            return false;
        }
        System.out.println(Constants.USER_LOGIN_SUCCESS);
        return true;
    }
}
