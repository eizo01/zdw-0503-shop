package com.zdw.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zdw.product.mapper.BannerMapper;

import com.zdw.product.model.BannerDO;
import com.zdw.product.service.BannerService;
import com.zdw.product.vo.BannerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
@Service
public class BannerServiceImpl implements BannerService {
    @Resource
    private BannerMapper bannerMapper;

    /**
     * 根据权重展示  升序排列
     * @return
     */
    @Override
    public List<BannerVO> list() {

    List<BannerDO> brannerDoList =  bannerMapper.selectList(
            new QueryWrapper<BannerDO>().orderByAsc("weight"));
    List<BannerVO> bannerVOS = brannerDoList.stream().map(obj -> {
        BannerVO bannerVO = new BannerVO();
        BeanUtils.copyProperties(obj, bannerVO);
        return bannerVO;
    }).collect(Collectors.toList());

        return bannerVOS;
}
}
