package miaoshademo.Redis;

public class OrderKey extends BasePrefix {
    public OrderKey(String Prefix) {
        super(Prefix);
    }
    public static OrderKey getMiaoshaOrderByUidGid=new OrderKey("miaoshao_order");
}
