package com.zdw.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zdw.enums.AddressStatusEnum;
import com.zdw.interceptor.LoginInterceptor;
import com.zdw.model.LoginUser;
import com.zdw.user.model.AddressDO;
import com.zdw.user.mapper.AddressMapper;
import com.zdw.user.request.AddressAddReqeust;
import com.zdw.user.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.user.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 电商-公司收发货地址表 服务实现类
 * </p>
 *
 * @author zdw
 * @since 2023-04-28
 */
@Service
@Slf4j
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressDO> implements AddressService {

    @Autowired
    private  AddressMapper addressMapper;
    @Override
    public AddressVO detail(Long id) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        AddressDO addressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("id",id).eq("user_id",loginUser.getId()));

        if(addressDO == null){
            return null;
        }
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(addressDO,addressVO);

        return addressVO;
    }

    /**
     * 新增收货地址

     * @param addressAddReqeust
     */
    @Override
    public void add(AddressAddReqeust addressAddReqeust) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        AddressDO addressDO = new AddressDO();
        addressDO.setCreateTime(new Date());
        addressDO.setUserId(loginUser.getId());

        BeanUtils.copyProperties(addressAddReqeust,addressDO);


        //是否有默认收货地址
        if(addressDO.getDefaultStatus() == AddressStatusEnum.DEFAULT_STATUS.getStatus()){
            //查找数据库是否有默认地址
            AddressDO defaultAddressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>()
                    .eq("user_id",loginUser.getId())
                    .eq("default_status",AddressStatusEnum.DEFAULT_STATUS.getStatus()));

            if(defaultAddressDO != null){
                //修改为非默认收货地址
                defaultAddressDO.setDefaultStatus(AddressStatusEnum.COMMON_STATUS.getStatus());
                addressMapper.update(defaultAddressDO,new QueryWrapper<AddressDO>().eq("id",defaultAddressDO.getId()));
            }
        }

        int rows = addressMapper.insert(addressDO);

        log.info("新增收货地址:rows={},data={}",rows,addressDO);

    }

    @Override
    public int del(int addressId) {
        return 0;
    }

    @Override
    public List<AddressVO> listUserAllAddress() {
        return null;
    }
}
