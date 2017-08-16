package com.vending.back.machine.app.security;

import com.vending.back.machine.domain.User;
import com.vending.back.machine.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * vyemialyanchyk on 12/21/2016.
 */
@Service("vbmUserDetailsService")
public class JdbcUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        // username here is an email
        User user = userMapper.getUserAuthByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid user " + username);
        }
        return UserDetailsUtil.createUserByUsername(user);
    }

}
