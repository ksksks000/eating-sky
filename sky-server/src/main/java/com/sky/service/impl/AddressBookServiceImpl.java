package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

  /*新增地址簿信息*/
    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 条件查询
     *
     * @param addressBook
     * @return
     */
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    /*修改地址信息*/

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);

    }

    /*根据ID 删除地址*/

    @Override
    public void deleteById(Long id) {
        addressBookMapper.delete(id);
    }

    /*根据id查询地址*/

    @Override
    public AddressBook getOneById(Long id) {
        return addressBookMapper.getOneById(id);
    }

    /*设置默认地址*/

    @Override
    public void setIdDefault(AddressBook addressBook) {
        //1、将当前用户的所有地址修改为非默认地址 update address_book set is_default = ? where user_id = ?
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        //2、将当前地址改为默认地址 update address_book set is_default = ? where id = ?
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);

        /*
        * 这里我有了更深的一点感悟，
        * 就是，他前面controller的addressbook实体类实际上是前端传入数据的一种要求格式吧，这么看
        * 然后你的什么七七八八的东西除了userid可能随着点击事件的发生会自动传到后面来
        * 到了这里呢，
        * 他首先肯定可以确定的是必然传了个addressbook对应的id过来
        * 不然后面动态SQL肯定是查不到指定addressbook对象去修改的
        * 然后刚好到了这一层的时候，
        * 这个addressbook以自身为饵
        * 先把自己的默认状态设为0
        * 又因为大家都属于同一个userid
        * 所以，自己带着全部设为0的目标和分组条件userid直接传进去修改
        * 改完之后再单独根据自己的id设置为默认地址为1
        * 为什么直接修改而不是先查询原来默认的的再改？
        * 这中间应该多了一步操作
        * 此方案：全部设为0 + 根据当前id设为1
        * 原方案：查询default为1 + 修改此id为0 + 根据当前id设为1
        * 而且你来看，把所有的defalut设为0，相当于是n次操作
        *           查询defalute为1，你要边遍历边判断，最坏情况n+n此操作
        *           这中间就又多了操作时间，数据量一大就感觉很吃亏了
        *   所以其实我建议是建索引，而且还要设置更牛逼的优先级什么七七八八的
        *   不过这些都是后话了*/
    }
}
