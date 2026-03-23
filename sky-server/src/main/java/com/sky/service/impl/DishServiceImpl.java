package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    /*
    * 新增菜品
    * 这个是真麻烦
    *
    * 主要的是什么
    * 是实体类里面有数组属性
    * 而这个数组属性又具有操作空间，意思是可以多插入几条口味
    * 而这个口味呢他又没有什么具体的限定，所以你拿动态SQL全都写在一起好像也没办法
    * 最重要的前提是什么呢
    * 是你没办法写在一起
    * 因为要想批量插入口味就得有口味表的id
    * 而口味表的id你得先从前面插入那里拿到
    * woc
    * 又有点没懂了
    * 前面的id跟后面口味表的有什么关系
    * 懂了，
    * 你先要理解口味表的属性到底是什么意思
    * dishid是可以重复的，只是针对每一个dishid可能口味不同，即value不同
    * 总而言之，
    * 你要知道是给哪个菜品dishid去赋值value就行了
    * 所以你是不是得提前知道dishid
    * */
    @Override
    @Transactional//因为这个插入涉及到 菜的插入 和 菜的口味的插入，这个不能同时操作，而且是放在一起的事务
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插入1条数据
        dishMapper.insert(dish);

        //从dish里获取id主键
        /*这里有个注意的地方，就是前面插入数据还不算执行完，所以你插入的dishid是暂时拿不到的
        * 但是你可以从动态sql里面去先取出来
        * 相当于就是你在这个线程里面，用线程的方法拿到了，但我没说一定是，只是这样好去理解*/
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size()>0){

            /*
            * 这里是批量插入dishId到dishFlavor里
            * 因为你下面要批量插入n条数据到口味表
            * 那你是不是要遍历口味表根据flavor_id来提供索引遍历
            * （话说，如果用指针链表会不会就不用这么麻烦）
            * 所以这也是为什么要提前拿到dishID并对dishFlavor遍历*/
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
