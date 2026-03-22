package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Insert("insert into category (type,name,sort,status,create_time,update_time,create_user,update_user)" +
            "values" +
            "(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    public void insert(Category category);

    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);


    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);



    List<Category> list(Integer type);

    void update(Category category);
}
