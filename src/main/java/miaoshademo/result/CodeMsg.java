package miaoshademo.result;

public class CodeMsg {
    private int code;
    private String msg;
    //通过建立Codemsg静态类将异常分类
    public static CodeMsg SUCCESS=new CodeMsg(0,"登陆成功");
    //通用
    public static CodeMsg SERVER_ERROR=new CodeMsg(500100,"server_error");
    public static CodeMsg BIND_ERROR=new CodeMsg(500101,"参数校验异常: %s");
    public static CodeMsg ILLEGAL_REQUST=new CodeMsg(500102,"非法访问");
    //登录
    public static CodeMsg PASSWORD_EMPTY=new CodeMsg(500211,"密码不能为空");
    public static CodeMsg MOBILE_EMPTY=new CodeMsg(500212,"手机号不能为空");
    public static CodeMsg MOBILE_NOSURE=new CodeMsg(500213,"手机号格式错误");
    public static CodeMsg ACCOUNT_ERROR=new CodeMsg(500213,"请输入正确的用户名及密码");
    public static CodeMsg LOGIN_ERROR=new CodeMsg(500214,"请先登录");
    //商品
    //订单
    public static CodeMsg OREDER_NOT_EXIST=new CodeMsg(500400,"订单不存在");
    //秒杀
    public static CodeMsg MIAOSHA_END=new CodeMsg(500500,"很遗憾，秒杀已结束");
    public static CodeMsg MIAOSHA_ONCE=new CodeMsg(500501,"一件商品只能秒杀一个哦");
    public static CodeMsg MIAOSHA_ING=new CodeMsg(500502,"排队中");
    private CodeMsg(int i, String error) {
        this.code=i;
        this.msg=error;
    }
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    public CodeMsg fillArgs(Object...args){
        int code =this.code;
        String message=String.format(this.msg,args);
        return new CodeMsg(code,message);
    }
}
