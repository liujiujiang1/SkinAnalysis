package edu.whut.skinhealth.service;

import edu.whut.skinhealth.dao.UserRepository;
import edu.whut.skinhealth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService{
    public static final String DEFAULT_STATE = "正常";
    public static final String DEFAULT_GENDER = "女";
    public static final String DEFAULT_SECURITY_QUESTION = "是否为患者";
    public static final String DEFAULT_SECURITY_ANSWER = "否";

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        applyDefaults(user);
        return userRepository.save(user);
    }
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
    public User updateUserInfo(User user){
        applyDefaults(user);
        return userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if (applyDefaults(user)) {
            return userRepository.save(user);
        }
        return user;
    }

    public List<User> findAllUser(){
        List<User> users = userRepository.findAll();
        List<User> changedUsers = new ArrayList<>();
        for (User user : users) {
            if (applyDefaults(user)) {
                changedUsers.add(user);
            }
        }
        if (!changedUsers.isEmpty()) {
            userRepository.saveAll(changedUsers);
        }
        return users;
    }

    public void normalizeExistingUsers() {
        findAllUser();
    }

    public boolean applyDefaults(User user) {
        if (user == null) {
            return false;
        }

        boolean changed = false;
        if (isBlank(user.getState())) {
            user.setState(DEFAULT_STATE);
            changed = true;
        }
        if (isBlank(user.getGender())) {
            user.setGender(DEFAULT_GENDER);
            changed = true;
        }
        if (isBlank(user.getSecurityQuestion())) {
            user.setSecurityQuestion(DEFAULT_SECURITY_QUESTION);
            changed = true;
        }
        if (isBlank(user.getSecurityAnswer())) {
            user.setSecurityAnswer(DEFAULT_SECURITY_ANSWER);
            changed = true;
        }
        if (user.getSignupTime() == null) {
            user.setSignupTime(new Timestamp(System.currentTimeMillis()));
            changed = true;
        }
        return changed;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
