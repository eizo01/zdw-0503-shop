package com.zdw.user.service;

import com.zdw.user.model.AddressDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdw.user.request.AddressAddReqeust;
import com.zdw.user.vo.AddressVO;

import java.util.List;

/**
 * <p>
 * 电商-公司收发货地址表 服务类
 * </p>
 *
 * @author zdw
 * @since 2023-04-28
 */
public interface AddressService extends IService<AddressDO> {


    /**
     * 根据id查询地址信息
     * @param id
     * @return
     */
    AddressVO detail(Long id);

    /**
     * 新增收货地址
     * @param addressAddReqeust
     */
    void add(AddressAddReqeust addressAddReqeust);

    /**
     * 根据id删除地址
     * @param addressId
     * @return
     */
    int del(int addressId);

    /**
     * 查找用户全部收货地址
     * @return
     */
    List<AddressVO> listUserAllAddress();

}
