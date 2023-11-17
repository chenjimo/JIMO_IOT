package jimo.iot.ctrl_module.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.OderMessage;
import jimo.iot.ctrl_module.mapper.OderMessageMapper;
import jimo.iot.ctrl_module.service.IOderMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Service
public class OderMessageServiceImpl extends ServiceImpl<OderMessageMapper, OderMessage> implements IOderMessageService {

    @Resource
    ModuleMessageServiceImpl moduleMessageService;
    /***
     * 更新指令新
     * @param oderMessage
     * @return
     */
    @Override
    public boolean writeOderMessage(OderMessage oderMessage) {
        return baseMapper.updateById(oderMessage) > 0;
    }

    /***
     * 读取全部指令信息，根据模块的类别进行分组。（用户）
     * @return
     */
    @Override
    public Map<String, List<OderMessage>> readOderMessagesByOrderModule() {
        Map map = new HashMap<String, List<OderMessage>>();
        baseMapper.selectObjs(Wrappers.<OderMessage>lambdaQuery()
                .groupBy(OderMessage::getModuleId)
                .select(OderMessage::getModuleId))
                .forEach(
                moduleId -> {
                    List list = new ArrayList<OderMessage>();
                    String name = moduleMessageService.getModuleMessage((Integer) moduleId).getName();
                    baseMapper.selectList(Wrappers.<OderMessage>lambdaQuery()
                            .eq(OderMessage::getModuleId, moduleId)).forEach(
                            oderMessage -> {
                                oderMessage.setModuleName(name);
                                list.add(oderMessage);
                            }
                    );
                    //将Module的name写入到对应的Map中进行存储
                    map.put(name,list);
                }
        );
        return map;
    }

    /***
     * 读取指令信息（模块）
     * @param orderMessageId
     * @return
     */
    @Override
    public OderMessage readOderMessage(Integer orderMessageId) {
        return baseMapper.selectById(orderMessageId);
    }
}
