package edu.whut.skinhealth.controller;

import edu.whut.skinhealth.entity.User;
import edu.whut.skinhealth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.shiro.crypto.hash.Md5Hash;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    private ResponseEntity<List<User>> queryAllUsers() {
        try{
            List<User> allUsers = userService.findAllUser();
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    private ResponseEntity<String> addUser(@RequestBody User newUser){
        try {
            if (userService.findUserByUsername(newUser.getUsername())==null) {
                newUser.setState("正常");
                newUser.setSignupTime(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
                userService.saveUser(newUser);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Exist", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("login") // 登录功能
    private ResponseEntity<String> userLogin(@RequestBody User user){
        try{
            User user1 = userService.findUserByUsername(user.getUsername());    //查找用户
            if (user1 != null) {    //存在
                if (passwordMatches(user.getUsername(), user.getPassword(), user1.getPassword())) {  //密码正确
                    return new ResponseEntity<>("Success", HttpStatus.OK);
                } else {    //用户名与密码匹配错误
                    return new ResponseEntity<>("InfoError", HttpStatus.OK);
                }
            } else {    //用户不存在
                return new ResponseEntity<>("NotExist", HttpStatus.OK);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUserByUsername(@PathVariable("username") String username) {
        try {
            User target = userService.findUserByUsername(username);
            if (target == null){
                return new ResponseEntity<>("NotExist", HttpStatus.OK);
            }else{
                userService.deleteUser(target);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping
    public ResponseEntity<String> updateUserInfo(@RequestBody User user)
    {
        try {
            User oldInfo = userService.findUserByUsername(user.getUsername());
            if (oldInfo == null){
                return new ResponseEntity<>("NotExist", HttpStatus.OK);
            } else{
                oldInfo.setPassword(user.getPassword());
                oldInfo.setState(user.getState());
                oldInfo.setGender(user.getGender());
                oldInfo.setBirthday(user.getBirthday());
                oldInfo.setDistrict(user.getDistrict());
                userService.updateUserInfo(oldInfo);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private boolean passwordMatches(String username, String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        if (rawPassword.equals(storedPassword)) {
            return true;
        }
        return new Md5Hash(rawPassword + username).toHex().equals(storedPassword);
    }

}
