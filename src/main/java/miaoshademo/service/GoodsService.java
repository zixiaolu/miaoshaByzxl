package miaoshademo.service;

import miaoshademo.dao.GoodsDao;
import miaoshademo.domain.Goods;
import miaoshademo.domain.MiaoshaGoods;
import miaoshademo.vo.GoodsVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GoodsService {

    @Autowired

    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsid) {
        return goodsDao.getGoodsVoByGoodsId(goodsid);
    }
    public boolean reduceStock(GoodsVo goods){
        MiaoshaGoods g= new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        int ret=goodsDao.reduceStock(g);
        return ret>0;
    }
}
