import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    static String input_token="";
    static int index;
    static String str;

    static void next_token(){
        if (index >= str.length()){
            input_token = "$";
        } else {
            while(Character.isSpaceChar(str.charAt(index)))
                index++;
            if(Character.isLetter(str.charAt(index)))
            {
                input_token = String.valueOf(str.charAt(index++));
                while (index < str.length() && (Character.isDigit(str.charAt(index)) || str.charAt(index) == '_'))
                {
                    int x = index++;
                    input_token += String.valueOf(str.charAt(x));
                }
            }else {
                input_token = String.valueOf(str.charAt(index++));
            }
        }
    }

    static boolean match(char expected_token){
        if (input_token.charAt(0) != expected_token ){

            return false;
        }
        next_token();
        return true;
    }

    public static boolean parser(String sen){
        str = sen;
        index = 0;
        next_token();

        if (!exp()){
            return false;
        }
        return match('$');
    }

    // E -> T E'
    static boolean exp(){
        if (!term()){
            return false;
        }
        if (!exp_prime()){
            return false;
        }
        return true;
    }

    // E' -> + T E' | - T E' | e
    static boolean exp_prime(){
        switch (input_token){
            case "+":
            case "-":
                next_token();
                if (!term()){
                    return false;
                }
                if (!exp_prime()){
                    return false;
                }
                return true;
            case "$":
            case ")":
                return true;
            default: return false;
        }
    }

    // T -> F T'
    static boolean term(){
        if (!factor()){
            return false;
        }
        if (!term_prime()){
            return false;
        }
        return true;
    }

    // T' -> * F T' | / F T' | e
    static boolean term_prime(){
        switch (input_token){
            case "*":
                // case '/':
                next_token();
                if (!factor()){
                    return false;
                }
                if (!term_prime()){
                    return false;
                }
                return true;
            case "+":
            case "-":
            case ")":
            case "$":
                return true;
            default: return false;
        }
    }

    //     F -> 0 | 1 | ... | 9 | (E) | -F | +F
    static boolean factor() {
        if (input_token.length() == 1)
            if ((input_token.charAt(0) >= 'a' && input_token.charAt(0) <= 'z') || (input_token.charAt(0) >= 'A' && input_token.charAt(0) <= 'Z') || input_token.charAt(0) == '_') {
                next_token();
                return true;
            } else if (input_token.charAt(0) >= '0' && input_token.charAt(0) <= '9') {
                next_token();
                return true;
            } else if (input_token.charAt(0) == '-' || input_token.charAt(0) == '+') {
                next_token();
                if(!factor())
                    return false;
                return true;
            } else if (input_token.charAt(0) == '(') {
                next_token();
                if (!exp()) {
                    return false;
                }
                return match(')');
            } else {
                return false;
            }
        else {
            String regex = "^[a-z]+(\\_)?([0-9]*$)";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input_token);
            if (matcher.matches()) {
                next_token();
                return true;
            } else {
                return false;
            }

        }
    }
}