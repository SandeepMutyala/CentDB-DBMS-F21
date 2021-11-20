package security;

public class UserRegistration {
    public static Boolean registerUser() {
        checkIfUserAlreadyExists();
        return true;
    }
    public static Boolean checkIfUserAlreadyExists() {
        return false;
    }
}
