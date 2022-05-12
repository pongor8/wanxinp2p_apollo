package cn.itcast.wanxinp2p.account.service;

import cn.itcast.wanxinp2p.account.common.AccountErrorCode;
import cn.itcast.wanxinp2p.account.entity.Account;
import cn.itcast.wanxinp2p.account.mapper.AccountMapper;
import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.BusinessException;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.domain.StatusCode;
import cn.itcast.wanxinp2p.common.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private SmsService smsService;

    @Value("${sms.enable}")
    private Boolean smsEnable;

    /**
     * 发送并获取短信验证码
     *
     * @param mobile
     * @return
     */
    @Override
    public RestResponse getMSMCode(@PathVariable String mobile) {
        return smsService.getMSMCode(mobile);
    }

    @Override
    //@Hmily(confirmMethod = "confirmRegister", cancelMethod = "cancelRegister")
    public AccountDTO register(@RequestBody AccountRegisterDTO registerDTO) {
        Account account = new Account();
        account.setUsername(registerDTO.getUsername());
        account.setMobile(registerDTO.getMobile());
        account.setPassword(PasswordUtil.generate(registerDTO.getPassword()));
        if (smsEnable) {
            account.setPassword(PasswordUtil.generate(account.getMobile()));
        }
        account.setDomain("c");
        account.setStatus(StatusCode.STATUS_OUT.getCode());
        save(account);
        AccountDTO accountDTO = convertAccountEntityToDTO(account);
        return accountDTO;
    }

    public void confirmRegister(AccountRegisterDTO registerDTO) {
        log.info("execute confirmRegister");
    }

    public void cancelRegister(AccountRegisterDTO registerDTO) {
        log.info("execute cancelRegister");
        //删除账号
        remove(Wrappers.<Account>lambdaQuery().eq(Account::getUsername, registerDTO.getUsername()));
    }

    @Override
    public AccountDTO login(@RequestBody AccountLoginDTO accountLoginDTO) {
        Account account = null;
        if (accountLoginDTO.getDomain().equalsIgnoreCase("c")) {
            account = getAccountByMobile(accountLoginDTO.getMobile());//获取c端用户
        } else {
            account = getAccountByUsername(accountLoginDTO.getUsername());//获取b端用户
        }
        if (account == null) {
            throw new BusinessException(AccountErrorCode.E_130104); // 用户不存在
        }
        AccountDTO accountDTO = convertAccountEntityToDTO(account);
        if (smsEnable) {// 如果smsEnable=true，说明是短信验证码登录，不做密码校验
            return accountDTO;
        } //验证密码
        if (PasswordUtil.verify(accountLoginDTO.getPassword(), account.getPassword())) {
            return accountDTO;
        }
        throw new BusinessException(AccountErrorCode.E_130105);
    }

    private Account getAccountByMobile(String mobile) {
        return getOne(new QueryWrapper<Account>().lambda().eq(Account::getMobile, mobile));
    }

    private Account getAccountByUsername(String username) {
        return getOne(new QueryWrapper<Account>().lambda().eq(Account::getUsername, username));
    }

    /**
     * entity转为dto
     *
     * @param entity
     * @return
     */
    private AccountDTO convertAccountEntityToDTO(Account entity) {
        if (entity == null) {
            return null;
        }
        AccountDTO dto = new AccountDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public Integer checkMobile(String mobile, String key, String code) {
        smsService.verifySmsCode(key, code);
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Account::getMobile, mobile);
        long count = count(wrapper);
        return count > 0 ? 1 : 0;
    }
}
