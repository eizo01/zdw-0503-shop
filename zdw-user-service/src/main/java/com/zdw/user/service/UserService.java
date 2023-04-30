package com.zdw.user.service;

import com.zdw.user.model.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdw.user.request.UserLoginRequest;
import com.zdw.user.request.UserRegisterRequest;
import com.zdw.util.JsonData;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zdw
 * @since 2023-04-28
 */
public interface UserService extends IService<UserDO> {


    JsonData register(UserRegisterRequest registerRequest);

    JsonData login(UserLoginRequest loginRequest);
}
