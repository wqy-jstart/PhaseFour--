package cn.tedu.csmall.passport.ex;

import cn.tedu.csmall.passport.web.ServiceCode;

/**
 * 业务异常,继承自RuntimeException
 *
 * @Author java.@Wqy
 * @Version 0.0.1
 */
public class ServiceException extends RuntimeException {

    private ServiceCode serviceCode;

    /**
     * 以便抛出异常时传入状态码和状态描述
     * @param serviceCode 传入ServiceCode枚举类中的属性赋值给成员变量
     * @param message 抛出异常时反馈的信息
     */
    public ServiceException(ServiceCode serviceCode,String message) {
        super(message);// Throwable类中的构造方法
        this.serviceCode = serviceCode;// 将传入的枚举属性赋值给成员变量
    }

    /**
     * 向外界公开获取该异常的枚举属性
     *
     * @return 返回成员变量业务状态码的实例
     */
    public ServiceCode getServiceCode(){
        return serviceCode;//为JsonResult返回ServiceCode(不包含状态码)
    }
}

