package cn.tedu.csmall.passport.web;

/**
 * 业务状态码
 *
 * @Author java.@Wqy
 * @Version 0.0.1
 */
public enum ServiceCode {

    // 使用枚举中的常量时相当于调用枚举的构造方法 例:OK()
    /**
     * 成功!
     */
    OK(20000),

    /**
     * 错误: 检查参数产生的错误
     */
    ERR_BAD_REQUEST(40000),

    /**
     * 登录失败,用户名或密码错误
     */
    ERR_UNAUTHORIZED(40100),

    /**
     * 错误：未登录（账号被禁用）
     */
    ERR_UNAUTHORIZED_DISABLED(40200),

    /**
     * 错误: 无此权限
     */
    ERR_FORBIDDEN(40300),

    /**
     * 错误: 数据不存在
     */
    ERROR_CONFLICT(40400),

    /**
     * 错误: 数据冲突
     */
    ERR_NOT_FOUND(40900),

    /**
     * 错误: 插入数据异常
     */
    ERR_INSERT(50000),

    /**
     * 错误: 删除数据异常
     */
    ERR_DELETE(50100),

    /**
     * 错误: 修改数据异常
     */
    ERR_UPDATE(50200),

    /**
     * 错误: 查询数据异常
     */
    ERR_SELECT(50300),

    /**
     * 错误: JWT签名错误
     */
    ERR_JWT_SIGNATURE(60000),// ctrl + shift + U(自动转大小写)

    /**
     * 错误: JWT数据格式错误
     */
    ERR_JWT_MALFORMED(60100),

    /**
     * 错误: JWT已过期
     */
    ERR_JWT_EXPIRED(60200);

    private Integer value;// 声明一个变量,代表每个枚举对应的状态码

    private ServiceCode(Integer value){// 添加构造方法,给枚举设置Integer类型的状态码
        this.value = value;
    }

    public Integer getValue(){// 提供get方法使得外部获取某个枚举对应的状态码
        return value;
    }
}
