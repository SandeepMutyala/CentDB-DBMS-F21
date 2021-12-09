package com.security;
import utils.Constants;
import dao.UserDaoImpl;
import model.User;
import java.util.Scanner;

public class UserRegistration {
    public static Boolean registerUser() {
        Scanner sc = new Scanner(System.in);
        UserDaoImpl udi = new UserDaoImpl();
        User user = new User();
        System.out.println(Constants.USERNAME_FIELD);
        user.setUsername(Utils.encodeWithMD5(Utils.validateUserInput("Username", sc)));
        System.out.println(Constants.PASSWORD_FIELD);
        user.setPassword(Utils.encodeWithMD5(Utils.validateUserInput("Password", sc)));
        System.out.println("Enter user Email");
        user.setPassword(Utils.encodeWithMD5(Utils.validateUserInput("Email", sc)));
        System.out.println(Constants.SECURITY_Q1_FIELD);
        user.setSecurity_question1(Utils.validateUserInput("Security Question 1", sc));
        System.out.println(Constants.SECURITY_A1_FIELD);
        user.setSecurity_answer1(Utils.validateUserInput("Security Answer 1", sc));
        System.out.println(Constants.SECURITY_Q2_FIELD);
        user.setSecurity_question2(Utils.validateUserInput("Security Question 2", sc));
        System.out.println(Constants.SECURITY_A2_FIELD);
        user.setSecurity_answer2(Utils.validateUserInput("Security Answer 2", sc));
        System.out.println(Constants.SECURITY_Q3_FIELD);
        user.setSecurity_question3(Utils.validateUserInput("Security Question 3", sc));
        System.out.println(Constants.SECURITY_A3_FIELD);
        user.setSecurity_answer3(Utils.validateUserInput("Security Answer 3", sc));
        user.setUser_id(user.getUsername() + user.getPassword());
        if (!Utils.checkIfUserAlreadyExists(user.getUser_id(), udi)) {
            udi.append(user);
            System.out.println(Constants.USER_REGISTRATION_SUCCESS_MESSAGE);
            System.out.println(Constants.USER_POST_REGISTRATION_LOGIN_MESSAGE);
            return true;
        }
        System.out.println(Constants.USER_ALREADY_EXISTS_MESSAGE);
        return false;
    }
}
