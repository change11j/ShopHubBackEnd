package org.ctc.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.ctc.dto.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;

@RequestMapping("/mart")
@Controller
public class MartController {

    @RequestMapping(value = "/seven", method = RequestMethod.GET)
    public void getSevenMart(HttpSession session, HttpServletResponse httpServletResponse) {
        String sevenURL = "https://emap.presco.com.tw/c2cemap.ashx?eshopid=1&&servicetype=1&url="+
                "http://localhost:8081/mart/getSevenAddr";
        httpServletResponse.setHeader("Location", sevenURL);
        httpServletResponse.setStatus(302);
    }

    @RequestMapping(value = "/family", method = RequestMethod.GET)
    public void getFamilyMart(HttpSession session,HttpServletResponse httpServletResponse) {
        String sevenURL = "https://mfme.map.com.tw/default.aspx?&cvsid=1717746527911&cvstemp=eyJjYWxsYmFja191cmwiOiJodHRwczovL3Nob3BlZS50dy9jaGVja291dC8ifQ==&exchange=true&" +
                "opid=048af2438891a89a3536ac09cc96ccbd34a1714e88cf8fdb63e6186dcc3ff89d&"+
                "cvslink=http://localhost:8081/mart/getFMAddr&cvsname=shopee.tw";
        httpServletResponse.setHeader("Location", sevenURL);
        httpServletResponse.setStatus(302);
    }


    @PostMapping("/getSevenAddr")
    public void getSevenAddr(@RequestParam String storename,@RequestParam String storeaddress, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().println("<script>window.opener.postMessage({ storename: '" + storename + "',storeaddress: '"+storeaddress+"'}, '*'); window.close();</script>");


    }


    @GetMapping("/getFMAddr")
    public void getFMAddr(HttpServletResponse response,@RequestParam String name,@RequestParam String addr) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().println("<script>window.opener.postMessage({ storename: '" + name + "',storeaddress: '"+addr+"'}, '*'); window.close();</script>");
    }

}
