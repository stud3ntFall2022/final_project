import java.sql.Array;
import java.util.*;
import java.util.regex.*;
import static java.lang.System.exit;

public class Tokenizer {
    static String token_expression = "";

        static void checkStorage(String str, ArrayList<Object> token, LinkedHashMap<String, Integer> pair )
    {
        boolean flag = false;
        for (Map.Entry<String, Integer> entry : pair.entrySet()) {
            if(entry.getKey().equals(str.toString())) {
                token.add(entry.getValue());
                token_expression += entry.getValue().toString();
                flag = true;
            }
            if (flag)
                break;
        }
    }
    static int precedenceCheck(ArrayList<Object> input)
    {
        for ( int i = 0; i < input.size(); i ++ )
            if (input.get(i).equals('*') || input.get(i).equals('/'))
                return i;
        return -1;
    }
    static ArrayList<Object> precedenceCompute(ArrayList<Object> resultSet, int opIndex)
    {
        ArrayList<Object> arrayTemp = new ArrayList<Object>();

        while (precedenceCheck( resultSet ) != -1)
            resultSet = finalResultSetCompute(resultSet, precedenceCheck(resultSet));

        return resultSet;
    }
    static ArrayList<Object> finalResultSetCompute(ArrayList<Object> resultSet, int opIndex)
    {
        ArrayList<Object> arrayTemp = new ArrayList<Object>();
        int hold = (int) resultSet.get(opIndex - 1) * (int) resultSet.get(opIndex + 1);
        Character operator = (Character) resultSet.get(opIndex);

            for ( int i = 0; i < resultSet.size(); i ++ )
               if ( i == ( opIndex - 1 ) )
                    arrayTemp.add(hold);
               else if ( i == ( opIndex + 1 ) || i == ( opIndex ) )
                   continue;
               else
                   arrayTemp.add(resultSet.get(i));

        return arrayTemp;
    }
    static int computeResult(ArrayList<Object> token)
    {
        int val = 0, sign = 0;
        ArrayList<Object> finalSet = new ArrayList<Object>();
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~token_expression: "+ token_expression);
        finalSet = precedenceCompute(token, precedenceCheck(token));


        if ( finalSet.size() == 1 ) {
            return (int) finalSet.get(0);
        }
        else
            for (int i = 0; i < finalSet.size(); i ++ )
            {
                if (finalSet.get(i) instanceof Character && finalSet.get(i).equals('-'))
                    sign = 1;
                else if(finalSet.get(i) instanceof Integer && sign == 1) {
                    val -= (int) finalSet.get(i);
                    sign = 0;
                }else if( finalSet.get(i) instanceof Integer && sign == 0 )
                    val += (int) finalSet.get(i);
            }

        return val;
    }
    static boolean checkSign(String s, int strIndex)
    {
        int index = 0;
        String sign = s.substring( index, strIndex );
        int minus_cnt = 0; index = 0;

        while (index < sign.length()) {
            if (sign.charAt(index) == '-')
                minus_cnt += 1;
            index++;
        }
        if (minus_cnt % 2 != 0)
            return true;

        return false;
    }

    static int computeAssignment(String s, LinkedHashMap<String, Integer> pair)
    {
        ArrayList<Object> token = new ArrayList<Object>();
        int ind = 0, cnt_minus = 0, cnt_plus = 0;
        boolean sign_op = false;
        StringBuilder str = new StringBuilder();

        while ( ind < s.length() ) {
            if( Character.isDigit(s.charAt(ind)) ) {
                int value = 0;

                while (Character.isDigit(s.charAt(ind))) {
                    value = 10 * value + s.charAt(ind) - '0';
                        ind++;
                }
                //token_expression += value;
                token.add(value);
                ind--;
            }else if (Character.isLetterOrDigit(s.charAt(ind)) || s.charAt(ind) == '_') {
                while (Character.isLetterOrDigit(s.charAt(ind)) || s.charAt(ind) == '_') {
                    str.append(s.charAt(ind));
                    ind++;
                }
                ind--;
                checkStorage(str.toString(), token, pair);
                str.setLength(0);
            }else if(s.charAt(ind) == '('){
                token_expression += s.charAt(ind);
                ind += 1;
                //System.out.println("Lolo1, ind: "+ind+" s.indexOf(')')"+s.indexOf(')'));
                String subS = s.substring(ind, s.length());//indexOf(')')+1);
                //System.out.println("Lolo2, ind: "+ind+" subS.indexOf(')')"+subS.indexOf(')'));
                int output = computeAssignment(subS, pair);

                token_expression += ')';
                //System.out.println("~~~~~~~~~~~~~~out of recurs computeAssignement: "+token_expression );
                token.add(output);
                //ind = s.indexOf(')', ind);
                ind = ind + subS.length()-1;
            }else if ( s.charAt(ind) == '+' || s.charAt(ind) == '-' || s.charAt(ind) == '*' ) {
                while (s.charAt(ind) == '+' || s.charAt(ind) == '-' || s.charAt(ind) == '*') {
                    if ( s.charAt(ind) == '-' && ind == 0 ) {
                        int endIndex = s.indexOf('(');
                        String sign = s.substring(0, endIndex);
                        sign_op = checkSign(s, endIndex);
                        ind = endIndex;
                    }else if (s.charAt(ind) == '+') {
                        token_expression += s.charAt(ind);
                        cnt_plus++;
                    }else if (s.charAt(ind) == '-') {
                        token_expression += s.charAt(ind);
                        cnt_minus++;
                    }else if (s.charAt(ind) == '*') {
                        token_expression += s.charAt(ind);
                        token.add((char) s.charAt(ind));
                    }
                    ind++;
                }
                ind --;
                if ( cnt_minus %2 != 0)
                {
                    char tm = (char) s.charAt(ind);
                    token.add(tm);
                }
                cnt_minus = 0;
            }
            ind++;
        }

        int compResult = computeResult(token);

        if(sign_op)
            compResult = (~(compResult - 1));

        return compResult;
    }

    static int process( String s, LinkedHashMap<String, Integer> keyValuePair )
    {
        ArrayList<Object> token = new ArrayList<Object>();

            int result = computeAssignment(s, keyValuePair);
                //System.out.println("PPPPPPPPPPPPPP result"+ result+" token_assignment: "+token_expression);
           parse_bstree parseTree = new parse_bstree();
           parseTree.createTree(token_expression);

        return result;
    }
    static boolean validateVariable(String s){
        int i = 0;
        String regex = "^[a-z]*(\\_)?([0-9]*$)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);

        if (!matcher.matches())
        {
            System.out.println("error");
            exit(0);
        }

        return true;
    }
    static boolean validateRegexPattern(String s)
    {
        String regex = "[1-9][0-9]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    static int validateValue(String s){
        int i = 0;
        int val = 0;

        if (!validateRegexPattern(s))
            return -1;

        while (i < s.length())
        {
            if ( Character.isDigit(s.charAt(i)) )
                val = 10*val + s.charAt(i) - '0';
            i++;
        }
        return val;
    }

    static LinkedHashMap<String, Integer> validateInput(String s)
    {
        LinkedHashMap<String, Integer> keyValue = new LinkedHashMap<String, Integer>();
        int index = 0, i = 0;

        while( i < s.length() ) {

            int result = s.indexOf("=", index );
            int strIndex_v = result + 1;
            int endIndex_v = s.indexOf(";", strIndex_v );
            String value = s.substring( strIndex_v, endIndex_v + 1 );

            String variable_input = s.substring( index, result );

            String procVariable = variable_input.trim();
            String procValue = value.trim();

            validateVariable(procVariable);

            int finalValue = validateValue(procValue);

            if( finalValue == -1) {
                String validate_exp = procValue.substring(0, procValue.length() - 1);
                //System.out.println("Expression before parser: "+ validate_exp);
                if (!Parser.parser(validate_exp)){//.substring(0, procValue.length()-1)))
                    System.out.println("Expression is invalid");
                    exit(0);
                }
                /*else{
                    System.out.println("Expression is invalid");
                    exit(0);*/
                //}
                   // System.out.println("Kulla, procValue: "+ procValue+" "+ procValue.substring(0, procValue.length()));
            }

            if (validateVariable(procVariable) && finalValue != -1)
                keyValue.put(procVariable, finalValue);
            else{
                //System.out.println("Expressssionnnn: "+procValue);
                int varibleValue=0;
                //if (Parser.parser(procValue))
                    varibleValue = process(procValue, keyValue);
                keyValue.put(procVariable, varibleValue);
            }

            index = s.indexOf(";", result);
            index += 1;

            i = index;
        }
        return keyValue;
    }

    public static void main(String[] args) {

        LinkedHashMap<String, Integer> keyvalue = new LinkedHashMap<String, Integer>();
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();

        keyvalue = validateInput(s);

        for (Map.Entry<String, Integer> entry : keyvalue .entrySet()) {
            System.out.println( entry.getKey()+ " = "+ entry.getValue());
        }
        System.out.println("z parse tree = "+parse_bstree.result);
    }
}

