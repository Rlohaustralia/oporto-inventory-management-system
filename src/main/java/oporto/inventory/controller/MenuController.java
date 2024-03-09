package oporto.inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // A Spring MVC controller
@RequestMapping("/admin/menus") // Maps the controller to the specified URL path
public class MenuController {

    @GetMapping
    public String helloWorld() {
        return "index";
    }
}
