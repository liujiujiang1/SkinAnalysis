package edu.whut.skinhealth.controller;

import edu.whut.skinhealth.entity.User;
import edu.whut.skinhealth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

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
            if (isBlank(newUser.getUsername()) || isBlank(newUser.getPassword())) {
                return new ResponseEntity<>("InfoEmpty", HttpStatus.OK);
            }
            if (isBlank(newUser.getSecurityQuestion()) || isBlank(newUser.getSecurityAnswer())) {
                return new ResponseEntity<>("SecurityEmpty", HttpStatus.OK);
            }
            newUser.setUsername(newUser.getUsername().trim());
            newUser.setSecurityQuestion(newUser.getSecurityQuestion().trim());
            newUser.setSecurityAnswer(newUser.getSecurityAnswer().trim());
            if (userService.findUserByUsername(newUser.getUsername())==null) {
                if (isBlank(newUser.getState())) {
                    newUser.setState(UserService.DEFAULT_STATE);
                }
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
                    if (isFrozen(user1.getState())) {
                        return new ResponseEntity<>("Frozen", HttpStatus.OK);
                    }
                    if (isCancelled(user1.getState())) {
                        return new ResponseEntity<>("Cancelled", HttpStatus.OK);
                    }
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
                if (user.getPassword() != null && !user.getPassword().isBlank()) {
                    oldInfo.setPassword(user.getPassword());
                }
                if (user.getState() != null) {
                    oldInfo.setState(isBlank(user.getState()) ? UserService.DEFAULT_STATE : user.getState());
                }
                if (user.getGender() != null) {
                    oldInfo.setGender(isBlank(user.getGender()) ? UserService.DEFAULT_GENDER : user.getGender());
                }
                if (user.getBirthday() != null) {
                    oldInfo.setBirthday(user.getBirthday());
                }
                if (user.getDistrict() != null) {
                    oldInfo.setDistrict(user.getDistrict());
                }
                if (user.getSecurityQuestion() != null || user.getSecurityAnswer() != null) {
                    if (isBlank(user.getSecurityQuestion()) || isBlank(user.getSecurityAnswer())) {
                        return new ResponseEntity<>("SecurityEmpty", HttpStatus.OK);
                    }
                    oldInfo.setSecurityQuestion(user.getSecurityQuestion().trim());
                    oldInfo.setSecurityAnswer(user.getSecurityAnswer().trim());
                }
                userService.updateUserInfo(oldInfo);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/{username}")
    private ResponseEntity<User> queryUserByUsername(@PathVariable("username") String username) {
        try {
            User user = userService.findUserByUsername(username);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{username}/security-question")
    private ResponseEntity<String> querySecurityQuestion(@PathVariable("username") String username) {
        try {
            User user = userService.findUserByUsername(username);
            if (user == null) {
                return new ResponseEntity<>("NotExist", HttpStatus.OK);
            }
            if (isCancelled(user.getState())) {
                return new ResponseEntity<>("Cancelled", HttpStatus.OK);
            }
            return new ResponseEntity<>(user.getSecurityQuestion() == null ? "" : user.getSecurityQuestion(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("reset-password")
    private ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        try {
            User user = userService.findUserByUsername(request.get("username"));
            if (user == null) {
                return new ResponseEntity<>("NotExist", HttpStatus.OK);
            }
            if (isCancelled(user.getState())) {
                return new ResponseEntity<>("Cancelled", HttpStatus.OK);
            }
            if (!securityAnswerMatches(request.get("securityAnswer"), user.getSecurityAnswer())) {
                return new ResponseEntity<>("AnswerError", HttpStatus.OK);
            }
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.isBlank()) {
                return new ResponseEntity<>("PasswordEmpty", HttpStatus.OK);
            }
            user.setPassword(newPassword);
            userService.updateUserInfo(user);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("change-password")
    private ResponseEntity<String> changePassword(@RequestBody Map<String, String> request) {
        try {
            User user = userService.findUserByUsername(request.get("username"));
            if (user == null) {
                return new ResponseEntity<>("NotExist", HttpStatus.OK);
            }
            if (isFrozen(user.getState())) {
                return new ResponseEntity<>("Frozen", HttpStatus.OK);
            }
            if (isCancelled(user.getState())) {
                return new ResponseEntity<>("Cancelled", HttpStatus.OK);
            }
            if (!passwordMatches(user.getUsername(), request.get("oldPassword"), user.getPassword())) {
                return new ResponseEntity<>("OldPasswordError", HttpStatus.OK);
            }
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.isBlank()) {
                return new ResponseEntity<>("PasswordEmpty", HttpStatus.OK);
            }
            user.setPassword(newPassword);
            userService.updateUserInfo(user);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{username}/password")
    private ResponseEntity<String> resetUserPasswordByAdmin(@PathVariable("username") String username, @RequestBody Map<String, String> request) {
        try {
            User user = userService.findUserByUsername(username);
            if (user == null) {
                return new ResponseEntity<>("NotExist", HttpStatus.OK);
            }
            String newPassword = request.get("password");
            if (newPassword == null || newPassword.isBlank()) {
                return new ResponseEntity<>("PasswordEmpty", HttpStatus.OK);
            }
            user.setPassword(newPassword);
            userService.updateUserInfo(user);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{username}/state")
    private ResponseEntity<String> updateUserStateByAdmin(@PathVariable("username") String username, @RequestBody Map<String, String> request) {
        try {
            User user = userService.findUserByUsername(username);
            if (user == null) {
                return new ResponseEntity<>("NotExist", HttpStatus.OK);
            }
            user.setState(request.get("state"));
            userService.updateUserInfo(user);
            return new ResponseEntity<>("Success", HttpStatus.OK);
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

    private boolean securityAnswerMatches(String rawAnswer, String storedAnswer) {
        if (isBlank(rawAnswer) || isBlank(storedAnswer)) {
            return false;
        }
        return rawAnswer.trim().equalsIgnoreCase(storedAnswer.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isFrozen(String state) {
        return "冻结".equals(state) || "鍐荤粨".equals(state);
    }

    private boolean isCancelled(String state) {
        return "注销".equals(state) || "娉ㄩ攢".equals(state);
    }

}
