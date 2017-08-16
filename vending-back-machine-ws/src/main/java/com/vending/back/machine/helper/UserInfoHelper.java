package com.vending.back.machine.helper;

import com.vending.back.machine.domain.UserInfo;
import com.vending.back.machine.error.CodeErrorException;
import com.vending.back.machine.mapper.UserMapper;
import com.vending.back.machine.util.ValidationUtil;
import com.vending.back.machine.helper.translate.ErrorMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * vyemialyanchyk on 2/15/2017.
 */
@Slf4j
@Service
public class UserInfoHelper {

    @Autowired
    private UserMapper userMapper;

    public void validateEmail(String email, Long userId) {
        ValidationUtil.checkEmail("email", email);
        if (userMapper.userExists(email) > 0) {
            if (userId == null || !userMapper.getUserByEmail(email).getId().equals(userId)) {
                log.info(String.format("User with email %s already exists", email));
                throw new CodeErrorException(CodeErrorException.ErrorCode.DUPLICATE_EMAIL,
                        ErrorMsg.USER_WITH_EMAIL_ALREADY_EXISTS.get(email));
            }
        }
    }

    public static void preProcessFields(@RequestBody UserInfo userInfo) {
        if (userInfo.getEmail() != null) {
            userInfo.setEmail(userInfo.getEmail().toLowerCase());
        }
        userInfo.setFirstName(WordUtils.capitalizeFully(userInfo.getFirstName()));
        userInfo.setLastName(WordUtils.capitalizeFully(userInfo.getLastName()));
    }

}
