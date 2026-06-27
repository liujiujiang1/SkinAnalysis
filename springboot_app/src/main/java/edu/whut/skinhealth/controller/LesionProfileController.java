package edu.whut.skinhealth.controller;

import edu.whut.skinhealth.entity.LesionProfile;
import edu.whut.skinhealth.entity.User;
import edu.whut.skinhealth.po.UserInfo;
import edu.whut.skinhealth.service.LesionProfileService;
import edu.whut.skinhealth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("lesion-profile")
public class LesionProfileController {
    private final LesionProfileService lesionProfileService;
    private final UserService userService;

    public LesionProfileController(LesionProfileService lesionProfileService, UserService userService) {
        this.lesionProfileService = lesionProfileService;
        this.userService = userService;
    }

    @GetMapping("user/{username}")
    public ResponseEntity<List<LesionProfile>> queryByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(lesionProfileService.getByUsername(username), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LesionProfile> create(@RequestBody Map<String, String> request) {
        User user = userService.findUserByUsername(request.get("username"));
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Timestamp now = now();
        LesionProfile lesionProfile = new LesionProfile();
        lesionProfile.setUser(toUserInfo(user));
        lesionProfile.setTitle(defaultIfBlank(request.get("title"), "未命名随访"));
        lesionProfile.setBodySite(request.get("bodySite"));
        lesionProfile.setNote(request.get("note"));
        lesionProfile.setStatus(defaultIfBlank(request.get("status"), "随访中"));
        lesionProfile.setCreatedTime(now);
        lesionProfile.setUpdatedTime(now);
        return new ResponseEntity<>(lesionProfileService.save(lesionProfile), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<LesionProfile> update(@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
        Optional<LesionProfile> optional = lesionProfileService.getByIdAndUsername(id, request.get("username"));
        if (optional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        LesionProfile lesionProfile = optional.get();
        lesionProfile.setTitle(defaultIfBlank(request.get("title"), lesionProfile.getTitle()));
        lesionProfile.setBodySite(request.get("bodySite"));
        lesionProfile.setNote(request.get("note"));
        lesionProfile.setStatus(defaultIfBlank(request.get("status"), lesionProfile.getStatus()));
        lesionProfile.setUpdatedTime(now());
        return new ResponseEntity<>(lesionProfileService.save(lesionProfile), HttpStatus.OK);
    }

    private UserInfo toUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setBirthday(user.getBirthday());
        userInfo.setGender(user.getGender());
        userInfo.setDistrict(user.getDistrict());
        return userInfo;
    }

    private Timestamp now() {
        return new Timestamp(new Date(System.currentTimeMillis()).getTime());
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
