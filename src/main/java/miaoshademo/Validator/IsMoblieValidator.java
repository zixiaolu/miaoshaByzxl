package miaoshademo.Validator;

import com.mysql.cj.util.StringUtils;
import miaoshademo.util.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMoblieValidator implements ConstraintValidator<IsMoblie,String> {

    private boolean required=false;
    @Override
    public void initialize(IsMoblie constraintAnnotation) {
        required=constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMoblie(s);
        }
        else{
            if(StringUtils.isEmptyOrWhitespaceOnly(s)){
                return true;
            }
        }
        return ValidatorUtil.isMoblie(s);
    }
}
