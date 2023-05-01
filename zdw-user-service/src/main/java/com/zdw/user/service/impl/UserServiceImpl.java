package com.zdw.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zdw.enums.BizCodeEnum;
import com.zdw.enums.SendCodeEnum;
import com.zdw.interceptor.LoginInterceptor;
import com.zdw.model.LoginUser;
import com.zdw.user.model.UserDO;
import com.zdw.user.mapper.UserMapper;
import com.zdw.user.request.UserLoginRequest;
import com.zdw.user.request.UserRegisterRequest;
import com.zdw.user.service.NotifyService;
import com.zdw.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.user.vo.UserVO;
import com.zdw.util.CommonUtil;
import com.zdw.util.JWTUtil;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zdw
 * @since 2023-04-28
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    @Autowired
    private NotifyService notifyService;

    @Autowired
    private UserMapper userMapper;
    /**
     * 用户注册
     * * 邮箱验证码验证
     * * 密码加密（TODO）
     * * 账号唯一性检查(TODO)
     * * 插入数据库
     * * 新注册用户福利发放(TODO)
     *
     * @param registerRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class,propagation=Propagation.REQUIRED)
    //@GlobalTransactional
    public JsonData register(UserRegisterRequest registerRequest) {

        boolean checkCode = false;
        //校验验证码
        if (StringUtils.isNotBlank(registerRequest.getMail())) {
            checkCode = notifyService.checkCode(SendCodeEnum.USER_REGISTER, registerRequest.getMail(), registerRequest.getCode());
        }

        if (!checkCode) {
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }

        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(registerRequest, userDO);

        userDO.setCreateTime(new Date());
        userDO.setSlogan("人生需要动态规划，学习需要贪心算法");

        //设置密码 8位随机长度生成秘钥 盐 每一位账号的盐都是不一样的
        userDO.setSecret("$1$" + CommonUtil.getStringNumRandom(8));

        //密码+盐处理
        String cryptPwd = Md5Crypt.md5Crypt(registerRequest.getPwd().getBytes(), userDO.getSecret());
        userDO.setPwd(cryptPwd);

        //账号唯一性检查 794666918@qq.com
        if (checkUnique(userDO.getMail())) {

            int rows = userMapper.insert(userDO);
            log.info("rows:{},注册成功:{}", rows, userDO.toString());

//                    //新用户注册成功，初始化信息，发放福利等 TODO
           userRegisterInitTask(userDO);


            //模拟异常
            //int b = 1/0;

            return JsonData.buildSuccess();
        } else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_REPEAT);
        }

    }

    /**
     * 1、根据Mail去找有没这记录
     *      * 2、有的话，则用秘钥+用户传递的明文密码，进行加密，再和数据库的密文进行匹配
     *      *
     *      * @param loginRequest
     * @return
     */
    @Override
    public JsonData login(UserLoginRequest loginRequest) {
        List<UserDO> userDOS = userMapper.selectList(new QueryWrapper<UserDO>().eq("mail", loginRequest.getMail()));

        if (userDOS != null && userDOS.size() == 1){
            // 注册成功
            UserDO userDO = userDOS.get(0);

            String cryptPwd = Md5Crypt.md5Crypt(loginRequest.getPwd().getBytes(), userDO.getSecret());
            if (cryptPwd.equals(userDO.getPwd())){
                //TODO 登录成功 生成token
                //生成token令牌
                LoginUser userDTO = new LoginUser();
                BeanUtils.copyProperties(userDO, userDTO);
                String token = JWTUtil.geneJsonWebToken(userDTO);
                return JsonData.buildSuccess(token);

            }else{
                return JsonData.buildCodeAndMsg(BizCodeEnum.ACCOUNT_PWD_ERROR.getCode(),BizCodeEnum.ACCOUNT_PWD_ERROR.getMessage());

            }
        }else{
            //注册未成功
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER);
        }
    }

    /**
     * 用户注册，初始化福利信息 TODO
     *
     * @param userDO
     */
    private void userRegisterInitTask(UserDO userDO) {
    }


    /**
     * 校验用户账号唯一
     *
     * @param mail
     * @return
     */
    private boolean checkUnique(String mail) {

        QueryWrapper queryWrapper = new QueryWrapper<UserDO>().eq("mail", mail);

        List<UserDO> list = userMapper.selectList(queryWrapper);

        return list.size() > 0 ? false : true;

    }

    @Override
    public UserVO findUserDetail() {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        Long id = loginUser.getId();

        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>().eq("id", id));
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDO,userVO);
        return userVO;
    }
}
















