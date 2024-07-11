package org.ctc.service;

import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.ctc.costant.Constance;
import org.ctc.dao.ImageDao;
import org.ctc.dao.UsersDao;
import org.ctc.dto.ImageDTO;
import org.ctc.dto.Result;
import org.ctc.dto.UserDetailDTO;
import org.ctc.dto.UserProfileDTO;
import org.ctc.entity.Image;
import org.ctc.entity.Users;
import org.ctc.util.ImageUtil;
import org.ctc.util.JavaMail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.TimeUnit;


import static org.ctc.costant.Constance.*;
import static org.ctc.util.JavaMail.genAuthCode;
import static org.ctc.util.Jwt.generateToken;
import static org.ctc.util.Jwt.parseSHA256;

@Service
public class UserService {

    private UsersDao usersDao;

    private ImageDao imageDao;

    private ImageService imageService;

    private final RestTemplate restTemplate;

    private final ImageUtil imageUtil;

    private RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.key}")
    private String jwtKey;
    @Value("${jwt.exipred}")
    private String jwtExpired;
    @Value("${java.mail}")
    private String javaSecret;


    public UserService(UsersDao usersDao,
                       ImageDao imageDao,
                       RestTemplate restTemplate,
                       ImageService imageService,
                       ImageUtil imageUtil,
                       RedisTemplate<String, String> redisTemplate) {
        this.usersDao = usersDao;
        this.imageDao = imageDao;
        this.restTemplate = restTemplate;
        this.imageService = imageService;
        this.imageUtil = imageUtil;
        this.redisTemplate = redisTemplate;
    }


    public Result getUser(Integer id) {
        Optional<Users> usersOptional = usersDao.findById(id);
        if (usersOptional.isPresent()) {
            Users users = usersOptional.get();
            UserProfileDTO res = new UserProfileDTO();

            List<Image> images = imageDao.findBySourceStringAndSourceTypeAndSourceId(SHOP, USER_TYPE, users.getUserId());
            if (images.size() > 0) {             //有圖片的話才
                Image image = images.get(0); //user圖片只有一張
                String filePath = imageUtil.generateImageFile(USER_TYPE, users.getUserId(), image.getImageId(), image.getExtension());
            }

            BeanUtils.copyProperties(users, res);
            return new Result(SUCCESS, res);
        } else {
            return new Result(USER_NOT_EXIST);
        }

    }
    @Transactional
    public Result createUser(Users users) {
        if (usersDao.existsUsersByMail(users.getMail())) {
            return new Result<>(EMAIL_ALREADY_EXIST);
        } else {
            String sha256Secret = parseSHA256(users.getSecret());
            users.setSecret(sha256Secret);
            if (users.getIsOpenGoogle() == null) {
                users.setIsOpenGoogle(GOOGLE_CLOSE);
            } else {
                users.setIsOpenGoogle(GOOGLE_OPEN);
            }
            Integer id = usersDao.save(users).getUserId();
            usersDao.flush();
            return new Result<>(SUCCESS, id);
        }
    }
    @Transactional
    public Result updateUser(Integer userId,
                             String userName,
                             Date birthday,
                             Integer sex) {


        Optional<Users> usersOptional = usersDao.findById(userId);
        if (usersOptional.isPresent()) {
            Users target = usersOptional.get();
            target.setBirthday(birthday);
            target.setUserName(userName);
            target.setSex(sex);
            usersDao.save(target);
            return new Result(SUCCESS);
        } else {
            return new Result(UNKNOWN_ERROR);
        }

    }

    @Transactional
    public Result updateUser(MultipartFile file,
                             Integer userId,
                             String userName,
                             Date birthday,
                             Integer sex) {


        List<Image> images = imageDao.findBySourceStringAndSourceTypeAndSourceId(SHOP, USER_TYPE, userId);
        if (images.size() > 0) {
            imageService.updateImageToServer(file, images.get(0).getImageId());
        } else {
            imageService.uploadNewImageToServer(file, userId, USER_TYPE);
        }
        Optional<Users> usersOptional = usersDao.findById(userId);
        if (usersOptional.isPresent()) {
            Users target = usersOptional.get();
            target.setUserName(userName);
            target.setSex(sex);
            target.setBirthday(birthday);
            usersDao.save(target);
            return new Result(SUCCESS);
        } else {
            return new Result(UNKNOWN_ERROR);
        }
    }
    @Transactional
    public Result deleteUser(Integer id) {
        usersDao.deleteById(id);
        return new Result(SUCCESS);
    }


    public Result login(Users user) {
        String sha256Secret = parseSHA256(user.getSecret());
        Users userRe = usersDao.findByMail(user.getMail());
        if (userRe == null) {
            return new Result<>(USER_NOT_EXIST);
        }
        if (!userRe.getSecret().equals(sha256Secret)) {
            return new Result<>(USER_NOT_EXIST);
        }
        String token = generateToken(userRe.getUserName(), userRe.getUserId(), Long.parseLong(jwtExpired), jwtKey);

        UserProfileDTO userProfileDTO=new UserProfileDTO();
        userProfileDTO.setUserId(userRe.getUserId());
        userProfileDTO.setToken(token);
        userProfileDTO.setMail(userRe.getMail());
        userProfileDTO.setUserName(userRe.getUserName());
        userProfileDTO.setFilePath(userRe.getUserImage());
        userProfileDTO.setSex(userRe.getSex());
        userProfileDTO.setBirthday(userRe.getBirthday());
        return new Result<>(SUCCESS, userProfileDTO);


    }

    public Result forgotPass(String mail) {
        JavaMail javaMail = new JavaMail();
        if (usersDao.existsUsersByMail(mail)) {
            javaMail.setPASSWORD(javaSecret);
            javaMail.setRECIPIENT(mail);
            String secret = genAuthCode();
            javaMail.setTXT("驗證碼為 :" + secret);
            javaMail.sendMail();
            redisTemplate.opsForValue().set(mail, secret, VERIFY_EXPIRED_TIME, TimeUnit.MINUTES);
            return new Result<>(SUCCESS);
        } else {
            return new Result<>(EMAIL_NOT_EXIST);

        }
    }


    public Result verifyCode(String secret, String mail) {
        if (secret.equals(redisTemplate.opsForValue().get(mail))) {
            return new Result<>(SUCCESS);
        } else {
            return new Result<>(VERIFY_CODE_WRONG);
        }
    }
    @Transactional
    public Result ChangePass(String secret, String mail) {
        Users nowUser = usersDao.findByMail(mail);

        if (nowUser != null && secret != null) {
            String sha256Secret = parseSHA256(secret);
            nowUser.setSecret(sha256Secret);
            usersDao.save(nowUser);
            return new Result<>(SUCCESS);
        } else {
            return new Result<>(EMAIL_NOT_EXIST);
        }
    }

    public Result updateSellerInfo(Integer userId, String sellerName, String sellerImage, String sellerDisc) {
        Users user = usersDao.findById(userId).orElse(null);
        if (user == null) {
            return new Result<>();
        }

        user.setSellerName(sellerName);
        user.setSellerImage(sellerImage);
        user.setSellerDisc(sellerDisc);
        usersDao.save(user);

        return new Result<>();
    }
    public Result<UserDetailDTO> getUserDetails(Integer userId) {
        Optional<Users> userOptional = this.usersDao.findById(userId);
        if (userOptional.isPresent()) {
            Users user = (Users)userOptional.get();
            UserDetailDTO userDetailDTO = new UserDetailDTO();
            userDetailDTO.setUserId(user.getUserId());
            userDetailDTO.setUserName(user.getUserName());
            userDetailDTO.setSex(user.getSex());
            userDetailDTO.setMail(user.getMail());
            userDetailDTO.setBirthday(user.getBirthday());

            // 將 byte[] 圖片數據轉換為 Base64 編碼字符串
            if (user.getUserImage() != null) {
                userDetailDTO.setUserImage(user.getUserImage());
            }

            return new Result(Constance.SUCCESS, userDetailDTO);
        } else {
            return new Result(Constance.USER_NOT_EXIST);
        }
    }

    public Result saveUserDetails(UserDetailDTO userDetailDTO) {
        Optional<Users> existingUserOptional = this.usersDao.findById(userDetailDTO.getUserId());
        Users user;
        if (existingUserOptional.isPresent()) {
            user = (Users)existingUserOptional.get();
        } else {
            user = new Users();
            user.setUserId(userDetailDTO.getUserId());
        }
        user.setUserName(userDetailDTO.getUserName());
        user.setSex(userDetailDTO.getSex());
        user.setBirthday(userDetailDTO.getBirthday());
        this.usersDao.save(user);
        return new Result(Constance.SUCCESS);
    }

    public Map<String, Boolean> getUserNotifications(Integer userId) {
        Users user = usersDao.findById(userId).orElse(null);

        Map<String, Boolean> notifications = new HashMap<>();

        if (user != null) {
            notifications.put("mailNoti", user.getMailNoti());
            notifications.put("orderNoti", user.getOrderNoti());
            notifications.put("discountNoti", user.getDiscountNoti());
        } else {
            notifications.put("mailNoti", false);
            notifications.put("orderNoti", false);
            notifications.put("discountNoti", false);
        }

        return notifications;
    }

    public String toggleNotification(Integer userId, String field) {
        Optional<Users> userOptional = this.usersDao.findById(userId);
        if (userOptional.isPresent()) {
            Users user = (Users) userOptional.get();
            switch (field) {
                case "mailNoti":
                    user.setMailNoti(!user.getMailNoti());
                    break;
                case "orderNoti":
                    user.setOrderNoti(!user.getOrderNoti());
                    break;
                case "discountNoti":
                    user.setDiscountNoti(!user.getDiscountNoti());
                    break;
                default:
                    return "Invalid field";
            }
            this.usersDao.save(user);
            return "Field " + field + " toggled successfully";
        } else {
            return "User not found";
        }
    }

    @Transactional
    public boolean invalidateUser(Integer userId) {
        Optional<Users> optionalUser = this.usersDao.findById(userId);
        if (optionalUser.isPresent()) {
            Users user = (Users)optionalUser.get();
            user.setValid(false);
            this.usersDao.save(user);
            return true;
        } else {
            return false;
        }
    }

    public String getRedirectPath(Integer userId) {
        Optional<Users> userOptional = usersDao.findById(userId);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            String redirectPath = (user.getSecret() == null) ? "/changepass" : "/verifypass";
            System.out.println("Redirect path: " + redirectPath); // 打印日志
            return redirectPath;
        }
        return "/userNotFound";
    }

    public Map<String, String> verifyCurrentPassword(Integer userId, String currentPassword) {
        Map<String, String> response = new HashMap<>();

        Users result = usersDao.findById(userId).orElse(null);

        if (result == null) {
            response.put("status", Constance.USER_NOT_EXIST.toString());
            response.put("message", "User not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, response.toString());
        }
        // 加密
        String sha256CurrentPassword = parseSHA256(currentPassword);
        // 驗證密碼是否正確
        if (!result.getSecret().equals(sha256CurrentPassword)) {
            response.put("status", HttpStatus.UNAUTHORIZED.toString());
            response.put("message", "Invalid current password");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, response.toString());
        }
        response.put("status", Constance.SUCCESS.toString());
        response.put("redirect", "/changepass");
        return response;
    }

    @Transactional
    public Result<String> changeNewPass(Integer userId, String newPassword1, String newPassword2) {
        Optional<Users> userOptional = usersDao.findById(userId);
        if (!userOptional.isPresent()) {
            return new Result<>(404, "用戶不存在");
        }
        if (!newPassword1.equals(newPassword2)) {
            return new Result<>(400, "兩次輸入密碼不一致");
        }
        Users user = userOptional.get();
        String hashedPassword = parseSHA256(newPassword1);
        user.setSecret(hashedPassword);
        usersDao.save(user);
        return new Result<>(200, "密碼更改成功");
    }

    public Result saveUserImage(Integer userId, String userImage) {
        Users user = usersDao.findById(userId).orElse(null);

        if (user != null) {
            user.setUserImage(userImage);

            usersDao.save(user);
            return new Result(SUCCESS);
        } else {
            return new Result(USER_NOT_EXIST);
        }
    }

}
