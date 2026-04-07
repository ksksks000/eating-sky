package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.AddressBook;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    //新增用户地址
    @Insert("insert into address_book(user_id,consignee,phone,sex,province_code,province_name,city_code,city_name,district_code,district_name,detail,label,is_default)" +
            "values" +
            "(#{userId},#{consignee},#{phone},#{sex},#{provinceCode},#{provinceName},#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label},#{isDefault})")
    void insert(AddressBook addressBook);

    @Select("select * from address_book where user_id = #{userId} && id_default = 1 ")
    List<AddressBook> getDefaultByUserId(Long userId);

    //根据用户id修改地址簿
    void update(AddressBook addressBook);

    //根据地址簿对应的id删除地址簿
    @Delete("delete from address_book where id = #{id}")
    void delete(Long id);

    //根据地址簿对应的id查询地址簿
    @Select("select * from address_book where id = #{id}")
    AddressBook getOneById(Long id);

    /**
     * 条件查询
     * @param addressBook
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 根据 用户id修改 是否默认地址
     * @param addressBook
     */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    ;
}
