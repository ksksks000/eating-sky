package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /*查询菜品对应的套餐id*/
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

}
