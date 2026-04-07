package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public Result save(@RequestBody AddressBook addressBook){
        log.info("新增地址簿信息,{}",addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 查询当前登录用户的所有地址信息
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /*查询默认地址*/
    @GetMapping("/default")
    public Result<AddressBook> getDefaultById(){
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(1);
        List<AddressBook> defaultList = addressBookService.list(addressBook);
        if(defaultList != null && defaultList.size()>0){
            return  Result.success(defaultList.get(0));
        }
        return  Result.error("没有查询到默认地址");

    }

    /*修改地址*/

    @PutMapping
    public Result update(@RequestBody AddressBook addressBook){
        log.info("修改地址信息：{}",addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }


    /*根据id删除地址*/
    @DeleteMapping
    public Result deleteById(@RequestParam Long id){
        addressBookService.deleteById(id);
        return Result.success();
    }

    /*根据id查询地址*/
    @GetMapping("/{id}")
    public Result<AddressBook> getOneById(@RequestParam Long id){

        AddressBook addressBook = addressBookService.getOneById(id);
        return Result.success(addressBook);
    }

    /*设置默认地址*/
    @PutMapping("/default")
    public Result setIdDefault(@RequestBody AddressBook addressBook){
        addressBookService.setIdDefault(addressBook);
        return Result.success();
    }
}
