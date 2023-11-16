package jimo.iot.ctrl_module.service.impl;

import jimo.iot.ctrl_module.entity.UserMessage;
import jimo.iot.ctrl_module.mapper.UserMessageMapper;
import jimo.iot.ctrl_module.service.IUserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements IUserMessageService {

    /***
     * 更新用户信息(不存在择重新写入)
     * @param userMessage
     */
    @Override
    public boolean updateUserMessage(UserMessage userMessage) {
        return baseMapper.selectById(userMessage) == null ?
                baseMapper.insert(userMessage) > 0 : baseMapper.updateById(userMessage) >0;
    }

    /***
     * 用户的信息读取
     * @param userId
     * @return
     */
    @Override
    public UserMessage getUserMessage(Integer userId) {
        return baseMapper.selectById(userId);
    }

    /***
     * 更新登录时间
     * @param userId
     * @return
     */
    @Override
    public boolean updateLoginTime(Integer userId) {
        return baseMapper.updateById(new UserMessage(userId, LocalDateTime.now())) > 0;
    }

    /***
     * 更新操作记录
     * @param userId
     * @return
     */
    @Override
    public boolean addOperationRecord(Integer userId) {
        return baseMapper.updateById(
                new UserMessage(userId,baseMapper.selectById(userId).getVisit()+1)) > 0;
    }
}
