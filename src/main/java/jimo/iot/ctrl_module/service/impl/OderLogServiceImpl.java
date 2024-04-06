package jimo.iot.ctrl_module.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.OderLog;
import jimo.iot.ctrl_module.mapper.OderLogMapper;
import jimo.iot.ctrl_module.service.IOderLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jimo.iot.info.send.SendEmail;
import jimo.iot.util.APIUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Service
public class OderLogServiceImpl extends ServiceImpl<OderLogMapper, OderLog> implements IOderLogService {
    @Resource
    SendEmail sendEmail;

    /***
     * 写入控制记录
     * @param oderLog
     * @return
     */
    @Override
    public boolean writeLog(OderLog oderLog) {
        return baseMapper.insert(oderLog) > 0;
    }

    /***
     * 模块读取未执行且可以执行的指令，并记录读取时间
     * @param moduleId
     * @return
     */
    @Override
    public OderLog readLog(Integer moduleId) {
        LocalDateTime now = LocalDateTime.now();
        OderLog oderLog = baseMapper.selectOne(Wrappers.<OderLog>lambdaQuery()
                .eq(OderLog::getModuleId, moduleId)
                .eq(OderLog::getStatus, 0)//等待执行的状态
                .isNull(OderLog::getReadTime)//指令暂未被读取
                .le(OderLog::getWriteTime, now) // 小于等于当前时间
                .orderByAsc(OderLog::getWriteTime));//按升序排列先查最早的数据
        if (oderLog != null) {
            baseMapper.updateById(new OderLog(oderLog.getId(), now));
        }
        return oderLog;
    }

    /***
     * 根据执行的指令记录ID进行更新status
     * @author JIMO
     * @since 2024-03-31
     */
    @Override
    public boolean updateLog(Integer oderLogId, Integer status) {
        String Bz = "test";
        switch (status) {
            case 4:
                sendEmail.alterEmail("智能浇水模块", "在您下发的远程浇水水任务中，检测到水仓水量不足！建议您及时补水！", LocalDateTime.now());
                status = 2;//属于执行失败的那一类型
                Bz = "水仓缺水-已邮箱提示！";
                break;
            case 3:
                Bz = "土壤湿度很低无需浇水！";
                status = 1;
                break;
            case 5:
                Bz = "状态无需改变！";
                status = 1;
                break;
        }
        return baseMapper.updateById(new OderLog(oderLogId, status, Bz)) > 0;
    }

    /***
     * 用户获取全部指令记录信息（根据status和ReadTime分类为等待中、执行中、执行成功、与执行失败、已撤销）
     * 把该指令记录有关的信息写入
     * 撤销的命令isReadTime: true, status: -1由于撤销会记录撤销时间
     * @param isReadOrder
     * @param status
     * @return
     */
    @Override
    public List<OderLog> getLogs(boolean isReadOrder, Integer status, Integer limit) {

        List<OderLog> oderLogs = new ArrayList<OderLog>();
        if (isReadOrder) {//表示查询模块已经度过数据的记录（status执行中0、执行成功1、与执行失败2）
            if (limit == null || limit <= 0) {
                oderLogs = baseMapper.selectList(
                        Wrappers.<OderLog>lambdaQuery()
                                .eq(OderLog::getStatus, status)
                                .isNotNull(OderLog::getReadTime)
                                .orderByDesc(OderLog::getWriteTime)
                );
            } else {
                oderLogs = baseMapper.selectList(
                        Wrappers.<OderLog>lambdaQuery()
                                .eq(OderLog::getStatus, status)
                                .isNotNull(OderLog::getReadTime)
                                .orderByDesc(OderLog::getWriteTime)
                                .last("LIMIT " + limit)
                );
            }
        } else {//表示查询模块还未读取数据的记录（status等待中0、撤销-1）
            if (limit == null || limit <= 0) {
                oderLogs = baseMapper.selectList(
                        Wrappers.<OderLog>lambdaQuery()
                                .eq(OderLog::getStatus, status)
                                .isNull(OderLog::getReadTime)
                                .orderByDesc(OderLog::getWriteTime));
            } else {
                oderLogs = baseMapper.selectList(
                        Wrappers.<OderLog>lambdaQuery()
                                .eq(OderLog::getStatus, status)
                                .isNull(OderLog::getReadTime)
                                .orderByDesc(OderLog::getWriteTime)
                                .last("LIMIT " + limit)
                );
            }

        }
        return oderLogs;
    }

    /***
     * 根据status和ReadTime分类为等待中(0)、执行中(1)、执行成功(2)、与执行失败(3)、已撤销（4）,判断是否可以撤销这条指令！
     * @param oderLogId
     * @return (大于0则表示不可以再撤销了)
     */
    @Override
    public Integer isUpdate(Integer oderLogId) {
        int idUpa = 4;
        OderLog oderLog = baseMapper.selectById(oderLogId);
        if (oderLog != null) {
            if (oderLog.getReadTime() != null) {//这里已经表示不可以在进行撤回了
                Integer status = oderLog.getStatus();
                idUpa = status == -1 ? 4 : status == 0 ? 1 : status == 1 ? 2 : status == 2 ? 3 : 4;
            } else {
                idUpa = 0;
            }
        }
        return idUpa;
    }

    /***
     * 根据模块ID获取对应的指令信息，根据指令记录的写入时间降序读取，仅读取不做其他任何操作！
     * @param moduleId
     * @param jt 要最新的几条
     * @return
     */
    @Override
    public List<OderLog> getOderByModuleId(Integer moduleId, Integer jt) {
        //jt小于零则代表获取全部数据
        if (jt < 0) {
            return baseMapper.selectList(Wrappers.<OderLog>lambdaQuery()
                    .eq(OderLog::getModuleId, moduleId)
                    .orderByDesc(OderLog::getWriteTime));
        } else {
            return baseMapper.selectList(Wrappers.<OderLog>lambdaQuery()
                    .eq(OderLog::getModuleId, moduleId)
                    .orderByDesc(OderLog::getWriteTime)
                    .last("LIMIT " + jt));
        }
    }

    /***
     * 根据时间限制处理掉过期的指令进行中断。单位为小时！
     * @param hours
     * @param bz
     * @return
     */
    @Override
    public Integer setPastDueOderAll(Integer hours, String bz) {
        return baseMapper.updatePastDueOderStatusAndBz(hours, bz);
    }

}
