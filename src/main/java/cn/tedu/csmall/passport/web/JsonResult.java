package cn.tedu.csmall.passport.web;

import cn.tedu.csmall.passport.ex.ServiceException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 该类定义统一返回请求结果的方法,JSON对象类型(状态值+结果信息)
 *
 * @Author java@.Wqy
 * @Version 0.0.1
 */
@Data
public class JsonResult<T> implements Serializable {

    //成员变量,需要封装到JsonResult中的属性
    //状态码
    @ApiModelProperty("状态码")
    private Integer state;
    //描述文本
    @ApiModelProperty("操作失败时的描述文本")
    private String message;
    //操作成功时响应的数据
    @ApiModelProperty("操作成功时的返回数据")
    private T data;

    /**
     * 成功的静态方法(不用new,直接类名打点调用)
     * @return 返回state状态为1的JsonResult的对象
     */
    public static JsonResult<Void> ok(){
        return ok(null);//调用ok(Object data)方法,传入null值,则该ok()方法仅返回装有状态码的属性
    }

    /**
     * 成功时需要返回数据的ok()方法
     * @param data 传入需要返回的List集合
     * @return 返回包含List集合的JsonResult对象
     */
    public static <T> JsonResult<T> ok(T data){// 静态方法,参数列表添加泛型后要在方法上说明并进行泛型占位
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.state = ServiceCode.OK.getValue();// 固定设置20000的成功状态码
        jsonResult.data = data;// 接收传入的数据List集合
        return jsonResult;
    }

    /**
     * 重载下面的fail方法
     * @param e 传入自定义异常
     * @return 调用下面的重载方法,传入自定义异常的状态码和状态描述,并返回
     */
    public static JsonResult<Void> fail(ServiceException e){ //传入对应的异常类
        return fail(e.getServiceCode(),e.getMessage());//调用fail()传入异常要设置的枚举属性和异常的反馈信息
    }

    /**
     * 错误的静态方法(不用new,直接类名打点调用)
     * @param serviceCode 业务状态码对象
     * @param message 错误信息(原因)
     * @return 返回JsonResult对象(封装了状态码和错误信息)
     */
    public static JsonResult<Void> fail(ServiceCode serviceCode,String message){// 获取传入的枚举属性和反馈信息
        JsonResult<Void> jsonResult = new JsonResult<>();
        jsonResult.state = serviceCode.getValue();// 调用时仅传入枚举属性即可get到对应的value并设置
        jsonResult.message = message;// 接收获取的异常反馈信息
        return jsonResult;// 最后返回包含状态码和异常反馈信息的指定对象
    }
}
