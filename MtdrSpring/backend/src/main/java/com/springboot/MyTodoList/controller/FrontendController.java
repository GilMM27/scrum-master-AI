package com.springboot.MyTodoList.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles 404 errors and SPA routes by forwarding to index.html for client-side routing.
 * This allows React Router to handle navigation on page reload.
 *
 * In Spring Framework 6.x (Spring Boot 3.x), unmatched paths throw NoResourceFoundException
 * instead of forwarding to /error. Explicit catch-all mappings here prevent that exception
 * from being thrown for frontend routes.
 */
@Controller
public class FrontendController implements ErrorController {

    /**
     * Forward error dispatches to index.html so React Router can handle the route.
     */
    @RequestMapping("/error")
    public String handleError() {
        return "forward:/index.html";
    }

    /**
     * Handle single-level SPA routes (e.g., /login, /unauthorized, /admin).
     * The regex [^\\.] matches path segments without a file extension,
     * so static resources like main.js or style.css are not intercepted.
     */
    @GetMapping("/{path:[^\\.]*}")
    public String forwardSingleLevel() {
        return "forward:/index.html";
    }

    /**
     * Handle two-level SPA routes (e.g., /admin/home, /manager/tasks, /developer/backlog).
     * Only matches paths without a file extension in any segment.
     */
    @GetMapping("/{path1:[^\\.]*}/{path2:[^\\.]*}")
    public String forwardTwoLevels() {
        return "forward:/index.html";
    }
}
