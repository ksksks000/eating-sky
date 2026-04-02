package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insertBatch(List<DishFlavor> flavors);

    @Delete("delete from  dish_flavor where dish_id = #{dishId}")
    void deleteByIdss(Long dishId);

    void deleteByIds(List<Long> dishIds);

    //根据菜品id查询对应的口味
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getFlavorById(Long dishId);
}
