package org.wpk.test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;

@Mapper
@Primary
public interface TestMapper extends BaseMapper<Controller> {

    @Select("SELECT 'selectTest'")
    String selectTest();
}
