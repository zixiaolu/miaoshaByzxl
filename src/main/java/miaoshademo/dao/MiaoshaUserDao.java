package miaoshademo.dao;

import miaoshademo.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Repository
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id= #{id}")
    public MiaoshaUser getByid(@Param("id") long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    public void update(MiaoshaUser toBeUpdata);
}
