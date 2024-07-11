package org.ctc.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.ctc.dao.UsersDao;
import org.ctc.dto.PasswordDTO;
import org.ctc.dto.UserDetailDTO;
import org.ctc.dto.UserImageDTO;
import org.ctc.dto.SellerInfoDTO;
import org.ctc.ecpay.AioCheckOutALL;
import org.ctc.entity.Users;
import org.ctc.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.ctc.dto.Result;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.ctc.costant.Constance.*;
import static org.ctc.costant.Constance.UNKNOWN_ERROR;
import static org.ctc.ecpay.EcpayFunction.genCheckMacValue;

@Tag(name = "user")
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/createUser")
    public Result createUser(@RequestBody Users users) {
        return userService.createUser(users);
    }


    @GetMapping("/getUser/{id}")
    public Result getUser(@PathVariable Integer id) {
        return userService.getUser(id);
    }

    @PostMapping("/updateUser")
    public Result updateUserNotContainImage(@RequestParam("userId") Integer userId,
                                            @RequestParam("userName") String userName,
                                            @RequestParam("birthday") String birthday,
                                            @RequestParam("sex") Integer sex) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = dateFormat.parse(birthday);

        } catch (ParseException e) {
            return new Result<>(PARSE_DATE_ERROR);
        }
        return userService.updateUser(userId, userName, date, sex);
    }

    @PostMapping("/updateUserWithImage")
    public Result updateUserContainImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("userId") Integer userId,
                                         @RequestParam("userName") String userName,
                                         @RequestParam("birthday") String birthday,
                                         @RequestParam("sex") Integer sex) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = dateFormat.parse(birthday);

        } catch (ParseException e) {
            return new Result<>(PARSE_DATE_ERROR);
        }
        return userService.updateUser(file, userId, userName, date, sex);
    }

    @GetMapping("/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }


    @PostMapping("/login")
    public Result login(@RequestBody Users user) {
        return userService.login(user);
    }

    @PostMapping("/forgotPass")
    public Result forgotPass(@RequestParam String mail) {
        return userService.forgotPass(mail);
    }

    @PostMapping("/verifyCode")
    public Result verify(@RequestParam String secret, @RequestParam String mail) {
        return userService.verifyCode(secret, mail);
    }

    @PostMapping("/ChangePass")
    public Result ChangePass(@RequestParam String secret, @RequestParam String mail) {
        return userService.ChangePass(secret, mail);
    }
    @GetMapping({"/details/{userId}"})
    public Result<UserDetailDTO> getUserDetails(@PathVariable Integer userId) {
        return this.userService.getUserDetails(userId);
    }

    @PostMapping({"/details/updateUser"})
    public Result updateUser(@RequestBody UserDetailDTO userDetailDTO) {
        return this.userService.saveUserDetails(userDetailDTO);
    }

    @PutMapping({"/toggleNotification"})
    public Map<String, String> toggleBoolean(@RequestParam Integer userId, @RequestParam String field) {
        String result = this.userService.toggleNotification(userId, field);
        Map<String, String> response = new HashMap();
        response.put("message", result);
        return response;
    }


    @GetMapping("/getNotification/{userId}")
    public Map<String, Boolean> getUserNotifications(@PathVariable Integer userId) {
        return userService.getUserNotifications(userId);
    }

    @PutMapping({"/{id}/invalidate"})
    public Map<String, String> invalidateUser(@PathVariable Integer id) {
        boolean result = this.userService.invalidateUser(id);
        Map<String, String> response = new HashMap();
        return response;
    }

    @GetMapping("/checkPassword")
    public String checkPassword(@RequestParam Integer userId) {return userService.getRedirectPath(userId);}

    @PostMapping("/verifyPassword")
    public Map<String, String> verifyPassword(@RequestBody PasswordDTO request) {
        return userService.verifyCurrentPassword(request.getUserId(), request.getCurrentPassword());
    }

    @PostMapping("/changePassword")
    public Result<String> changePassword(@RequestBody PasswordDTO request) {
        System.out.println("Received request: " + request);
        Result<String> result = userService.changeNewPass(request.getUserId(), request.getNewPassword1(), request.getNewPassword2());
        System.out.println("Sending response: " + result);
        return result;
    }

    @PostMapping("/uploadImage")
    public Result uploadUserImage(@RequestBody UserImageDTO request) {
        return userService.saveUserImage(request.getUserId(), request.getUserImage());
    }
    @PostMapping("/updateSellerInfo")
    public Result updateSellerInfo(@RequestBody SellerInfoDTO sellerInfoDTO) {
        return userService.updateSellerInfo(
                sellerInfoDTO.getUserId(),
                sellerInfoDTO.getSellerName(),
                sellerInfoDTO.getSellerImage(),
                sellerInfoDTO.getSellerDisc()
        );
    }

}
