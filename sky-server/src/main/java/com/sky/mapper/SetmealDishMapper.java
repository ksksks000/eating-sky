package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /*查询菜品对应的套餐id*/
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);


    //批量插入套餐内菜品列表数据
    void insertBatch(List<SetmealDish> setmealDishes);


    //管理端根据套餐id查询套餐内菜品数据
    @Select("select * from setmeal_dish where id = #{id}")
    List<SetmealDish> getSetmealDishesById(Long id);
}
