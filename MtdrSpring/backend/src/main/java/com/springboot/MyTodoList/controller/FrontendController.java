package com.springboot.MyTodoList.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles 404 errors by forwarding to index.html for client-side routing.
 * This allows React Router to handle navigation on page reload.
 */
@Controller
public class FrontendController implements ErrorController {

    /**
     * Forward 404 errors to index.html so React Router can handle the route.
     */
    @RequestMapping("/error")
    public String handleError() {
        return "forward:/index.html";
    }
}
