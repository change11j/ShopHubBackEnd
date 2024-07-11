package org.ctc.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import okhttp3.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.catalina.User;
import org.ctc.dao.ImageDao;
import org.ctc.dao.UsersDao;
import org.ctc.dto.GoogleData;
import org.ctc.entity.Image;
import org.ctc.entity.Users;
import org.ctc.service.ImageService;
import org.ctc.util.ImageUtil;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.ctc.costant.Constance.*;
import static org.ctc.util.Jwt.parseToGooglePayload;


@Controller
public class GoogleAuthController {


    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private ImageDao imageDao;

    private UsersDao usersDao;

    private ImageUtil imageUtil;

    private ImageService imageService;

    public GoogleAuthController(ImageDao imageDao,
                                UsersDao usersDao,
                                ImageService imageService,
                                ImageUtil imageUtil){
        this.imageDao=imageDao;
        this.usersDao=usersDao;
        this.imageService=imageService;
        this.imageUtil=imageUtil;
    }



    @RequestMapping(value = "/auth/google", method = RequestMethod.GET)
    public void method(HttpSession session,HttpServletResponse httpServletResponse) {
        String state = generateState();
        session.setAttribute("state", state);   //存state
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/auth" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=openid profile email" +
                "&state=" + state;
        System.out.println(state);
        httpServletResponse.setHeader("Location", googleAuthUrl);
        httpServletResponse.setStatus(302);
    }
    @RequestMapping(value = "/googleoauth/getCode", method = RequestMethod.GET)
    public void  getCode(@RequestParam("code") String code,
                          @RequestParam("state") String state,
                         HttpSession session,
                         HttpServletResponse response){
        System.out.println("code inside");
        String sessionState = (String) session.getAttribute("state");
        if (!state.equals(sessionState)) { //驗證state
            //TODO

        }else {
               OkHttpClient client = new OkHttpClient();

               RequestBody tokenRequestBody = new FormBody.Builder()
                       .add("code", code)
                       .add("client_id", clientId)
                       .add("client_secret", clientSecret)
                       .add("redirect_uri", redirectUri)
                       .add("grant_type", "authorization_code")
                       .build();

               Request tokenRequest = new Request.Builder()
                       .url("https://oauth2.googleapis.com/token")
                       .post(tokenRequestBody)
                       .build();


               try (Response tokenResponse = client.newCall(tokenRequest).execute()) {
                   if (!tokenResponse.isSuccessful()) {
                       response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed to get access token");
                       return;
                   }

                   ObjectMapper mapper = new ObjectMapper();
                   JsonNode jsonNode = mapper.readTree(tokenResponse.body().string());
                   String idToken = jsonNode.get("id_token").asText();

                   GoogleData googleData =parseToGooglePayload(idToken);
                    
                  //解析會員資料後，若此信箱已存在則開通google登入，若沒有就直接創建新帳號
                   Users newUser;
                   newUser =usersDao.findByMail(googleData.getEmail());
                   String filePath=null;
                   if(newUser==null){  //沒有此mail存在
                       newUser=new Users();
                       newUser.setIsOpenGoogle(GOOGLE_OPEN);
                       newUser.setMail(googleData.getEmail());
                       newUser.setUserName(googleData.getName());
                       newUser.setIsOpenGoogle(GOOGLE_OPEN);
                       filePath=googleData.getPicture();
                       newUser.setUserImage(filePath);
                       newUser=usersDao.save(newUser);
                       //imageService.getUrlImage(googleData.getPicture(),newUser.getUserId(),USER_TYPE);
                   }else {  //此mail存在
                       filePath=newUser.getUserImage();
                       newUser.setIsOpenGoogle(GOOGLE_OPEN);
                       newUser=usersDao.save(newUser);
                   }




                  //Image image = imageDao.findBySourceTypeAndSourceStringAndSourceId(USER_TYPE,SHOP,newUser.getUserId());


                   response.setContentType("text/html");
                   response.getWriter().println("<script>window.opener.postMessage({ id_token: '" + idToken + "',filePath: '"+filePath+"',userId : '"+newUser.getUserId()+"' }, '*'); window.close();</script>");
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }



        }
    }

    private String generateState() {  //產生state
        return "random_state";
    }


}