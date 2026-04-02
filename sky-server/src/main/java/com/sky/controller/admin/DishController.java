package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {


    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    //删除缓存数据方法
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);

        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);


        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询结果：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){

        log.info("批量删除菜品：{}",ids);
        dishService.deleteBatch(ids);

        //这里要考虑的因素是，删除操作，他删的可能不止一个，
        //所以你上面的单个删除缓存的操作可能就不够看了
        //这里知道的有两种方法
        //一、删完之后查数据库状态，然后判断哪些改变了，对应去删缓存
        // （这个偏麻烦，而且如果数据量很大的话，要对着删估计也比较耗时，
        //  因为你想，可能有些缓存待会就用不上了呢，但是你还是得去对照着查，那不浪费了吗）
        //二、直接把所有的缓存都删掉（删什么sql，删什么Redis）
        //  （这个的话其实还可以吧，从上面的角度下来分析，有用的重新查就好了
        //  查到了再丢进缓存
        //  但是我个人建议可以参考懒加载的方式，提前搞点批量缓存，因为有准备的肯定是会降低访问压力的）

        //这里要注意的是，单纯的删除操作delete不能识别通配符*，所以这里先全都查出来放集合里再删
        cleanCache("dish_*");

        return Result.success();


    }

    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}",id);
        DishVO dishVO = dishService.getByWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){

        log.info("修改菜品信息：{}",dishDTO);
        dishService.update(dishDTO);

        //你像修改操作可能更复杂，因为涉及到多个表，所以建议缓存全删
        cleanCache("dish_*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status ,Long id){
        log.info("启用或禁用菜品及套餐：{}",status);
        dishService.startOrStop(status,id);

        cleanCache("dish_*");
        return Result.success();

    }


}
