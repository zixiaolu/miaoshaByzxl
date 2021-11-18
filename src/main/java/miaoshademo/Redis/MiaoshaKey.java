package miaoshademo.Redis;

public class MiaoshaKey extends BasePrefix{


    public MiaoshaKey(int PastTime,String prefix){
        super(PastTime,prefix);
    }
    public static MiaoshaKey IS_GOOD_OVER=new MiaoshaKey(0,"gio");
    public static MiaoshaKey GET_MIAOSHA_PATH=new MiaoshaKey(60,"gmp");
}
