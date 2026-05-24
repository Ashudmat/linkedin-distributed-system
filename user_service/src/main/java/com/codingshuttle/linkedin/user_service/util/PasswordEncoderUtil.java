package com.codingshuttle.linkedin.user_service.util;


import org.springframework.context.annotation.Configuration;

import static org.mindrot.jbcrypt.BCrypt.*;

@Configuration
public class PasswordEncoderUtil {

    public static String hash(String s){
        return hashpw(s,gensalt());
    }

    public static boolean matches(String textPassword, String HashPassword){
        return checkpw(textPassword,HashPassword);
    }
}
