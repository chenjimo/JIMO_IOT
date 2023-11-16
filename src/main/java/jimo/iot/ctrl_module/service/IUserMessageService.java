package jimo.iot.ctrl_module.service;

import jimo.iot.ctrl_module.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 *  1、用户信息写入及更新
 *  2、用户信息读取
 *  3、更新登陆时间
 *  4、增添操作记录
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
public interface IUserMessageService extends IService<UserMessage> {
    /***
     * 更新用户信息(不存在择重新写入)
     * @param userMessage
     */
    public  boolean updateUserMessage(UserMessage userMessage);

    /***
     * 用户的信息读取
     * @param userId
     * @return
     */
    public  UserMessage getUserMessage(Integer userId);
    /***
     * 更新登录时间
     * @param userId
     * @return
     */
    public  boolean updateLoginTime(Integer userId);

    /***
     * 更新操作记录
     * @param userId
     * @return
     */
    public boolean  addOperationRecord(Integer userId);

}
