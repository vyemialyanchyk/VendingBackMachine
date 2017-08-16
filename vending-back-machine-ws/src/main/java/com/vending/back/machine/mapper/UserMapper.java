package com.vending.back.machine.mapper;

import com.vending.back.machine.domain.*;
import com.vending.back.machine.helper.history.Trackable;
import org.springframework.transaction.annotation.Transactional;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * vyemialyanchyk on 1/30/2017.
 */
public interface UserMapper {
    @Transactional(readOnly = true)
    @Select("SELECT u.email, u.first_name, u.last_name, u.id, u.status, u.active, u.verify_email, " +
            "us.password_hash, ur.role_name, us.password_reset_token, " +
            "if(pts.id is not null, pts.name, null) as photo " +
            "FROM vbm_user u " +
            "JOIN vbm_user_sensitive us ON u.id = us.user_id " +
            "LEFT JOIN vbm_user_role ur ON u.id = ur.user_id " +
            "WHERE u.id = #{userId} " +
            "ORDER BY u.created LIMIT 1")
    @ResultMap("getUserWithRoles")
    User getUserAuthById(@Param("userId") Long userId);

    @Transactional(readOnly = true)
    @Select("SELECT u.email, u.first_name, u.last_name, u.id, u.status, u.active, u.verify_email, " +
            "us.password_hash, ur.role_name, us.password_reset_token, " +
            "if(pts.id is not null, pts.name, null) as photo " +
            "FROM vbm_user u " +
            "JOIN vbm_user_sensitive us ON u.id = us.user_id " +
            "LEFT JOIN vbm_user_role ur ON u.id = ur.user_id " +
            "WHERE u.email = lower(#{email}) " +
            "ORDER BY u.created LIMIT 1")
    @ResultMap("getUserWithRoles")
    User getUserAuthByEmail(@Param("email") String email);

    @Transactional(readOnly = true)
    @Select("SELECT u.id, u.email, us.password_reset_token, us.updated, u.first_name, u.last_name " +
            "FROM vbm_user u " +
            "JOIN vbm_user_sensitive us ON u.id = us.user_id " +
            "WHERE u.email = lower(#{email}) and u.active = true " +
            "ORDER BY u.created LIMIT 1")
    UserAuthForPassword getUserAuthForPasswordByEmail(@Param("email") String email);

    @Transactional(readOnly = true)
    @Select("SELECT u.email, u.first_name, u.last_name, u.id, u.status, u.active, u.verify_email, " +
            "us.password_hash, ur.role_name, us.password_reset_token, " +
            "if(pts.id is not null, pts.name, null) as photo " +
            "FROM vbm_user u " +
            "JOIN vbm_user_login_tokens ult ON u.id = ult.user_id " +
            "JOIN vbm_user_sensitive us ON u.id = us.user_id " +
            "LEFT JOIN vbm_user_role ur ON u.id = ur.user_id " +
            "WHERE ult.login_token = #{loginToken} " +
            "ORDER BY u.created LIMIT 1")
    @ResultMap("getUserWithRoles")
    User getUserAuthByLoginToken(@Param("loginToken") String loginToken);

    @Transactional(readOnly = true)
    @Select("SELECT u.email, u.first_name, u.last_name, u.id, u.status, u.active, u.verify_email, " +
            "us.password_hash, ur.role_name, us.password_reset_token, " +
            "if(pts.id is not null, pts.name, null) as photo " +
            "FROM vbm_user u " +
            "JOIN vbm_user_sensitive us ON u.id = us.user_id " +
            "LEFT JOIN vbm_user_role ur ON u.id = ur.user_id " +
            "WHERE us.verify_email_token = #{verifyEmailToken} " +
            "ORDER BY u.created LIMIT 1")
    @ResultMap("getUserWithRoles")
    User getUserAuthByVerifyEmailToken(@Param("verifyEmailToken") String verifyEmailToken);

    @Transactional(readOnly = true)
    @Select("SELECT u.email, u.first_name, u.last_name, u.id, u.status, u.active, u.verify_email, " +
            "us.password_hash, ur.role_name, us.password_reset_token, " +
            "if(pts.id is not null, pts.name, null) as photo " +
            "FROM vbm_user u " +
            "JOIN vbm_user_sensitive us ON u.id = us.user_id " +
            "LEFT JOIN vbm_user_role ur ON u.id = ur.user_id " +
            "WHERE us.password_reset_token = #{passwordResetToken} " +
            "ORDER BY u.created LIMIT 1")
    @ResultMap("getUserWithRoles")
    User getUserAuthByPasswordResetToken(@Param("passwordResetToken") String passwordResetToken);

    @Transactional(readOnly = true)
    @Select("SELECT count(id) cnt FROM vbm_user where email = lower(#{email})")
    int userExists(@Param("email") String email);

    @Transactional(readOnly = true)
    @Select("SELECT * FROM vbm_user WHERE email = lower(#{email})")
    User getUserByEmail(@Param("email") String email);

    @Transactional(readOnly = true)
    @Select("SELECT * FROM vbm_user WHERE id = (#{userId})")
    User getUserById(@Param("userId") Long userId);

    @Transactional(readOnly = true)
    @Select("SELECT "
            + "u.id, "
            + "u.first_name, "
            + "u.last_name, "
            + "u.email "
            + "FROM vbm_user u "
            + "WHERE u.id = #{userId}")
    UserInfo getUserInfoById(Long userId);

    @Options(useGeneratedKeys = true, keyProperty = "user.id")
    @Insert("INSERT INTO "
            + "vbm_user(first_name, last_name, email, updater, verify_email) "
            + "VALUES(#{user.firstName}, #{user.lastName}, lower(#{user.email}), #{updater}, false)")
    @Transactional
    int createUser(@Param("user") UserInfo user);

    @Options(useGeneratedKeys = true, keyProperty = "user.id")
    @Insert("INSERT INTO "
            + "vbm_user(first_name, last_name, email, updater) "
            + "VALUES(#{user.firstName}, #{user.lastName}, lower(#{user.email}), #{updater})")
    @Transactional
    int createManagerUser(@Param("user") UserInfo user);

    @Insert("INSERT INTO vbm_user_role(user_id, role_name, updater) VALUES(#{userId}, #{role}, #{updater})")
    @Transactional
    int insertRole(@Param("userId") Long userId, @Param("role") String role);

    @Options(useGeneratedKeys = true, keyProperty = "user.id")
    @Insert("INSERT INTO vbm_user_sensitive(user_id, password_hash, updater, verify_email_token) VALUES(#{user.id}, #{user.passwordHash}, #{updater}, #{verifyEmailToken})")
    @Transactional
    int createUserSensitive(@Param("user") User user, @Param("verifyEmailToken") String verifyEmailToken);

    @Options(useGeneratedKeys = true, keyProperty = "profile.id")
    @Insert("INSERT INTO vbm_user_profile ( " +
            "user_id, birth_date, phone, city, " +
            "country, updater " +
            ") VALUES ( " +
            "#{profile.userId}, #{profile.birthDate}, #{profile.phone}, #{profile.city}, " +
            "#{profile.country}, #{updater})")
    @Transactional
    int createUserProfile(@Param("profile") UserProfile userProfile);

    @Trackable(table = "vbm_user_profile", where = "user_id=#{profile.userId}")
    @Update("UPDATE vbm_user_profile " +
            "SET " +
            "birth_date=#{profile.birthDate}, " +
            "phone=#{profile.phone}, " +
            "city=#{profile.city}, " +
            "country=#{profile.country}, " +
            "updater=#{updater}, " +
            "updated=now() " +
            "WHERE user_id=#{profile.userId} OR id=#{profile.id} ORDER BY id DESC LIMIT 1")
    @Transactional
    int updateUserProfile(@Param("profile") UserProfile userProfile);

    @Transactional(readOnly = true)
    @Select("SELECT count(id) cnt FROM vbm_user_profile where user_id = #{userId}")
    @Results(value = {@Result(column = "cnt", javaType = boolean.class)})
    boolean userProfileExistsForUserId(@Param("userId") Long userId);

    @Transactional(readOnly = true)
    @Select("<script>" +
            "SELECT " +
            "pr.id, " +
            "pr.user_id, " +
            "pr.birth_date, " +
            "pr.phone, " +
            "pr.city, " +
            "pr.country " +
            "FROM vbm_user_profile pr " +
            "WHERE pr.user_id = #{userId} " +
            "ORDER BY pr.id DESC LIMIT 1" +
            "</script>")
    UserProfile getUserProfile(@Param("userId") Long userId);

    @Trackable(table = "vbm_user", where = "id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user SET first_name=#{userInfo.firstName}, last_name=#{userInfo.lastName}, updated=now(), updater=#{updater} WHERE id=#{userId}")
    int updateUserName(@Param("userId") Long userId, @Param("userInfo") UserInfo userInfo);

    @Trackable(table = "vbm_user", where = "id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user SET email=#{email}, updated=now(), updater=#{updater} WHERE id=#{userId}")
    int updateUserEmail(@Param("userId") Long userId, @Param("email") String email);

    @Trackable(table = "vbm_user", where = "id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user SET first_name=#{userInfo.firstName}, last_name=#{userInfo.lastName}, email=#{userInfo.email}, updated=now(), " +
            "updater=#{updater} WHERE id=#{userId}")
    int updateUserInfo(@Param("userId") Long userId, @Param("userInfo") UserInfo userInfo);

    @Trackable(table = "vbm_user", where = "id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user SET first_name=#{userInfo.firstName}, last_name=#{userInfo.lastName}, email=#{userInfo.email}, updated=now(), " +
            "updater=#{updater}, verify_email=false WHERE id=#{userId}")
    int updateUserInfoEmail(@Param("userId") Long userId, @Param("userInfo") UserInfo userInfo);

    @Trackable(table = "vbm_user_sensitive", where = "user_id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user_sensitive SET password_hash=#{newPasswordHash}, updated=now(), updater=#{updater} WHERE user_id=#{userId}")
    int updateUserPassword(@Param("userId") Long userId, @Param("newPasswordHash") String newPasswordHash);

    @Trackable(table = "vbm_user_sensitive", where = "user_id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user_sensitive SET password_reset_token=#{resetToken}, updated=now(), updater=#{updater} WHERE user_id=#{userId}")
    int setUserPasswordResetToken(@Param("userId") Long userId, @Param("resetToken") String resetToken);

    @Trackable(table = "vbm_user_sensitive", where = "user_id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user_sensitive SET password_reset_token=#{resetToken}, password_hash=#{newPasswordHash}, updated=now(), updater=#{updater} WHERE user_id=#{userId}")
    int setUserPasswordResetTokenAndPassword(@Param("userId") Long userId, @Param("resetToken") String resetToken, @Param("newPasswordHash") String newPasswordHash);

    @Transactional
    @Insert("INSERT INTO vbm_user_login_token(login_token, user_id, updater) VALUES(#{loginToken}, #{userId}, #{updater})")
    int createUserLoginToken(@Param("userId") Long userId, @Param("loginToken") String loginToken);

    @Trackable(table = "vbm_user_login_token", where = "login_token=#{loginToken}")
    @Transactional
    @Delete("DELETE FROM vbm_user_login_token WHERE login_token=#{loginToken}")
    int deleteUserLoginToken(@Param("loginToken") String loginToken);

    @Trackable(table = "vbm_user_login_token", where = "user_id=#{userId}")
    @Transactional
    @Delete("DELETE FROM vbm_user_login_token WHERE user_id=#{userId}")
    int deleteAllUserLoginTokens(@Param("userId") Long userId);

    @Transactional(readOnly = true)
    @Select("<script>" +
            "SELECT " +
            "u.id, " +
            "u.first_name, " +
            "u.last_name, " +
            "u.email, " +
            "ur.role_name " +
            "FROM vbm_user u " +
            "JOIN vbm_user_role ur on u.id = ur.user_id " +
            "ORDER BY u.first_name, u.last_name " +
            "</script> ")
    @ResultMap("getUserWithRoles")
    List<UserInfoWithRoles> getManagerUsersForFilter();

    @Transactional(readOnly = true)
    @Select("<script>" +
            "SELECT " +
            "u.id, " +
            "u.first_name, " +
            "u.last_name, " +
            "u.email, " +
            "ur.role_name " +
            "FROM vbm_user u " +
            "JOIN vbm_user_role ur on u.id = ur.user_id " +
            "WHERE u.active = true " +
            "ORDER BY u.first_name, u.last_name " +
            "</script> ")
    @ResultMap("getUserWithRoles")
    List<UserInfoWithRoles> getManagerUsersForAssign();

    @Trackable(table = "vbm_user", where = "user_id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user SET email=#{email}, active=false, updater=#{updater} WHERE id=#{userId}")
    void deactivateUser(@Param("userId") Long userId, @Param("email") String email);

    @Transactional
    @Delete("DELETE FROM vbm_user_role WHERE user_id=#{userId} and role_name != 'ROLE_MANAGER'")
    Boolean deleteRoles(@Param("userId") Long userId);

    @Transactional(readOnly = true)
    @Select("SELECT " +
            "u.id, " +
            "u.first_name, " +
            "u.last_name, " +
            "u.email, " +
            "ur.role_name " +
            "FROM vbm_user u " +
            "JOIN vbm_user_role ur on u.id = ur.user_id " +
            "WHERE u.id = #{userId}")
    @ResultMap("getUserWithRoles")
    UserInfoWithRoles getUserInfoWithRolesById(Long userId);

    @Transactional(readOnly = true)
    @Select("SELECT " +
            "user_id " +
            "FROM vbm_user_sensitive " +
            "WHERE verify_email_token = #{verifyEmailToken}")
    Long getUserIdByEmailVerifyToken(@Param("verifyEmailToken") String verifyEmailToken);

    @Trackable(table = "vbm_user", where = "id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user SET verify_email=#{verifyEmail}, updater=#{updater} WHERE id=#{userId}")
    void setVerifyEmail(@Param("userId") Long userId, @Param("verifyEmail") Boolean verifyEmail);

    @Trackable(table = "vbm_user_sensitive", where = "user_id=#{userId}")
    @Transactional
    @Update("UPDATE vbm_user_sensitive SET verify_email_token=#{verifyEmailToken}, updater=#{updater} WHERE user_id=#{userId}")
    void setVerifyEmailToken(@Param("userId") Long userId, @Param("verifyEmailToken") String verifyEmailToken);
}
