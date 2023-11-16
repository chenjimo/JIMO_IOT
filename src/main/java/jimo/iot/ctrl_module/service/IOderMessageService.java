package jimo.iot.ctrl_module.service;

import jimo.iot.ctrl_module.entity.OderMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 *  1、写入控制指令
 *  2、读取获取全部指令信息
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
public interface IOderMessageService extends IService<OderMessage> {
    /***
     * 更新指令新
     * @param oderMessage
     * @return
     */
     public boolean writeOderMessage(OderMessage oderMessage);

    /***
     * 读取全部指令信息，根据模块的类别进行分组。（用户需要）
     * @return
     */
     public Map<String, List<OderMessage>> readOderMessagesByOrderModule();

    /***
     * 读取指令信息(模块需要)
     * @param orderMessageId
     * @return
     */
    public OderMessage readOderMessage(Integer orderMessageId);

}
