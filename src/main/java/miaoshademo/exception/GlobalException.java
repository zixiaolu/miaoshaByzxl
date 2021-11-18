package miaoshademo.exception;

import miaoshademo.result.CodeMsg;

public class GlobalException extends RuntimeException{
    private static final long serivalVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm){
        super(cm.toString());
        this.cm=cm;
    }

    public static long getSerivalVersionUID() {
        return serivalVersionUID;
    }

    public CodeMsg getCm() {
        return cm;
    }

}
