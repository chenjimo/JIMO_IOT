package jimo.iot.ctrl_module.mapper;

import jimo.iot.ctrl_module.entity.OderLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author JIMO
 * @since 2024-4-5
 */
public interface OderLogMapper extends BaseMapper<OderLog> {
    /**
     * 更新指定时间前且status为0的指令状态为2，并设置bz字段。
     *
     * @param hours   时间限制，单位为小时
     * @param bzValue 要设置的bz字段值
     * @return 受影响的记录数
     */
    @Update("UPDATE oder_log SET status = 2, bz = #{bzValue} " +
            "WHERE write_time < DATE_SUB(NOW(), INTERVAL #{hours} HOUR) AND status = 0")
    int updatePastDueOderStatusAndBz(@Param("hours") Integer hours, @Param("bzValue") String bzValue);
}
