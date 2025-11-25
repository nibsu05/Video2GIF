package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }


    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$") && !hashedPassword.startsWith("$2b$")) {
            if (plainPassword.equals(hashedPassword)) {
                return true;
            }
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    public static void main(String[] args) {
		System.out.println(PasswordUtil.hashPassword("123456"));
	}
}