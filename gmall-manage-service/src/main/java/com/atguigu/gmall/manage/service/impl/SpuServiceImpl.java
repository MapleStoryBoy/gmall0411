
package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.PmsProductImageMapper;
import com.atguigu.gmall.manage.mapper.PmsProductInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.atguigu.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.atguigu.gmall.service.SpuSercive;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuSercive {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    PmsProductImageMapper pmsProductImageMapper;
    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    /**
     * Spu列表的查询
     *
     * @param catalog3Id
     * @return
     */
    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {

        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.select(pmsProductInfo);

        return pmsProductInfos;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
//        //保存商品信息
//        pmsProductInfoMapper.insertSelective(pmsProductInfo);
//        String productId = pmsProductInfo.getId();
//        //保存商品图片
//        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
//        for (PmsProductImage pmsProductImage : spuImageList) {
//            pmsProductImage.setProductId(productId);
//            pmsProductImageMapper.insertSelective(pmsProductImage);
//        }
//        //保存商品销售属性
//        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
//        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
//            pmsProductSaleAttr.setProductId(productId);
//            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);
//            //保存商品销售属性值
//            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
//            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
//                pmsProductSaleAttrValue.setProductId(productId);
//                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
//            }
//        }
        //和saveAttrInfo方法相似，先判断id
        if (pmsProductInfo.getId() == null || pmsProductInfo.getId().length() == 0) {
            pmsProductInfo.setId(null);
            pmsProductInfoMapper.insertSelective(pmsProductInfo);
        } else {
            pmsProductInfoMapper.updateByPrimaryKey(pmsProductInfo);
        }

        //图片保存
        Example example = new Example(PmsProductImage.class);
        example.createCriteria().andEqualTo("productId", pmsProductInfo.getId());
        pmsProductImageMapper.deleteByExample(example);

        List<PmsProductImage> imageList = pmsProductInfo.getSpuImageList();
        if (imageList != null) {
            for (PmsProductImage pmsProductImage : imageList) {
                if (pmsProductImage.getId() != null && pmsProductImage.getId().length() == 0) {
                    pmsProductImage.setId(null);
                }
                pmsProductImage.setProductId(pmsProductInfo.getId());
                pmsProductImageMapper.insertSelective(pmsProductImage);
            }
        }

        Example pmsProductSaleAttrExample = new Example(PmsProductSaleAttr.class);
        pmsProductSaleAttrExample.createCriteria().andEqualTo("productId", pmsProductInfo.getId());
        pmsProductSaleAttrMapper.deleteByExample(pmsProductSaleAttrExample);

        //销售属性，spu销售属性保存
        Example pmsProductSaleAttrValueExample = new Example(PmsProductSaleAttrValue.class);
        pmsProductSaleAttrValueExample.createCriteria().andEqualTo("productId", pmsProductInfo.getId());
        pmsProductSaleAttrValueMapper.deleteByExample(pmsProductSaleAttrValueExample);

        //spu属性
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null) {
            for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
                if (pmsProductSaleAttr.getId() != null && pmsProductSaleAttr.getId().length() == 0) {
                    pmsProductSaleAttr.setId(null);
                }
                pmsProductSaleAttr.setProductId(pmsProductInfo.getId());//productId
                pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);


                List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
                for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                    if (pmsProductSaleAttrValue.getId() != null && pmsProductSaleAttrValue.getId().length() == 0) {
                        pmsProductSaleAttrValue.setId(null);
                    }
                    pmsProductSaleAttrValue.setProductId(pmsProductInfo.getId());//设置productId
                    pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
                }

            }
        }

    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> select = pmsProductImageMapper.select(pmsProductImage);

        return select;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> PmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);

        for (PmsProductSaleAttr productSaleAttr : PmsProductSaleAttrs) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());// 销售属性id用的是系统的字典表中id，不是销售属性表的主键
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return PmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId) {
//        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
//        pmsProductSaleAttr.setProductId(productId);
//        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
//
//        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
//            String saleAttrId = productSaleAttr.getSaleAttrId();
//
//            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
//            pmsProductSaleAttrValue.setSaleAttrId(saleAttrId);
//            pmsProductSaleAttrValue.setProductId(productId);
//            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
//
//            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
//        }
        //List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId,skuId);
        return pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId,skuId);
    }


}
