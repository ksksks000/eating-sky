package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    @Select("select count(id) from dish where category_id = #{categoryId}")
    public Integer countByCategoryId(Long categoryId);



    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //根据id查询菜品
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

/*
    //根据id删除菜品
    @Delete("delete from dish where id =#{id}")
    void deleteById(Long id);
*/

    //根据菜品id集合批量删除菜品
    void deleteById(List<Long> ids);

    //根据菜品id查询对应的口味
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getFlavorById(Long dishId);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);
}
