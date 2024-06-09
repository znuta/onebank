package com.onebank.onebank.auth.utilities;

import com.onebank.onebank.auth.entity.AppUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

@Component
public class UserUtil {

    public Collection<SimpleGrantedAuthority> getAuthority(AppUser user) {
        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
        String code = user.getRole().getRole();
        authorities.add(new SimpleGrantedAuthority(code));
        System.out.println("GRANTED AUTHORITIES " + authorities);
        return authorities;
    }


}
