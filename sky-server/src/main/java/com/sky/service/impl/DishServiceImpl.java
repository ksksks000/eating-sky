package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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

    @Autowired
    private SetmealDishMapper setmealDishMapper;

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


    //分页查询

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total,records);

    }

    //菜品批量删除
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {

        //判断当前菜品是否能够删除 -- 是否起售
        for (Long id :ids){
            Dish dish = dishMapper.getById(id);
            //当前菜品为起售状态，不能删除
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前菜品是否能够删除 -- 是否与套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        //删除菜品表中的菜品数据
        for(Long id : ids){
            dishMapper.deleteById(id);
            //删除菜品关联的口味数据
            dishFlavorMapper.deleteByIds(id);

        }



    }
}
