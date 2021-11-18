package miaoshademo.Redis;

public class MiaoshaUserKey extends BasePrefix {

    public static final int TOKEN_LIFE=3600*24;

    private MiaoshaUserKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static MiaoshaUserKey token=new MiaoshaUserKey(TOKEN_LIFE,"tk");
    public static MiaoshaUserKey getByName=new MiaoshaUserKey(TOKEN_LIFE,"name");
    public static MiaoshaUserKey getById=new MiaoshaUserKey(0,"id");
}
