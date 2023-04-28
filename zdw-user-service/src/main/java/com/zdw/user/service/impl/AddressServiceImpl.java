package com.zdw.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zdw.user.model.AddressDO;
import com.zdw.user.mapper.AddressMapper;
import com.zdw.user.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 电商-公司收发货地址表 服务实现类
 * </p>
 *
 * @author zdw
 * @since 2023-04-28
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressDO> implements AddressService {

    @Autowired
    private  AddressMapper addressMapper;
    @Override
    public AddressDO detail(Long id) {

        AddressDO addressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("id",id));
//        AddressDO addressDO = addressMapper.selectById(id);
        return addressDO;
    }
}
