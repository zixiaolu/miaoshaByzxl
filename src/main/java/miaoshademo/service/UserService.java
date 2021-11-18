package miaoshademo.service;

import miaoshademo.dao.UserDao;
import miaoshademo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;

@Service
public class UserService {

    @Autowired
    UserDao userDao;
    public User getById(int id){
        return userDao.getById(id);
    }
    @Transactional//事务标签，完成时提交，未完成时回滚
    public boolean tx(){
        /*
        这个事务是必定报错的，因为数据库中含有主键为1的记录了
         */
        User u1=new User();
        u1.setId(2);
        u1.setName("psc");
        userDao.insert(u1);
        User u2=new User();
        u2.setId(1);
        u2.setName("u1");
        userDao.insert(u2);
        return true;
    }
}
