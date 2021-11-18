package miaoshademo.util;

import com.mysql.cj.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern mobile_pattern=Pattern.compile("1\\d{10}");
    public static boolean isMoblie(String src){
        if(StringUtils.isEmptyOrWhitespaceOnly(src)){
            return false;
        }
        Matcher m=mobile_pattern.matcher(src);
        return m.matches();
    }

}
