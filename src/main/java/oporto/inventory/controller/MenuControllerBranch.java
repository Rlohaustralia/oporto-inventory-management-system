package oporto.inventory.controller;


import oporto.inventory.domain.Menu;
import oporto.inventory.domain.Orders;
import oporto.inventory.repository.MemberRepository;
import oporto.inventory.repository.MenuRepository;
import oporto.inventory.repository.MenuRepositoryBranch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller // A Spring MVC controller
@RequestMapping("/admin/branch/{memberBranch}/menus") // Maps the controller to the specified URL path
public class MenuControllerBranch {

    private final MenuRepository menuRepository; // Dependency injection for MenuRepository
    private final MenuRepositoryBranch menuRepositoryBranch; // Dependency injection for MenuRepositoryBranch


    @Autowired
    public MenuControllerBranch(MenuRepository menuRepository, MenuRepositoryBranch menuRepositoryBranch) {
        this.menuRepository = menuRepository;
        this.menuRepositoryBranch = menuRepositoryBranch;
    }


    @GetMapping // HTTP GET requests for displaying menus
    public String menus(@PathVariable(name = "memberBranch") String branchName, Model model) {

        // Retrieve all menus
        List<Menu> menus = menuRepository.allItem();

        // Retrieve branch ID using branch name
        String branchId = menuRepositoryBranch.getBranchId(branchName);

        // Retrieve branch menu quantities for each menu
        List<Integer> branchMenuQuantities = new ArrayList<>();
        for (Menu menu : menus) {
            String menuId = menu.getId();
            int branchMenuQuantity = menuRepositoryBranch.getBranchMenuQuantity(branchId, menuId);
            branchMenuQuantities.add(branchMenuQuantity);
        }

        // Add attributes to model for the order form view
        model.addAttribute("menus", menus);
        model.addAttribute("branchId", branchId);
        model.addAttribute("branchName", branchName);
        model.addAttribute("branchMenuQuantities", branchMenuQuantities);
        return "view/menusBranch"; // Return view page
    }


    @GetMapping("/order")
    public String getOrderForm(@PathVariable(name = "memberBranch") String branchName, Model model) {

        // Retrieve all menus
        List<Menu> menus = menuRepository.allItem();

        // Retrieve branch ID using branch name
        String branchId = menuRepositoryBranch.getBranchId(branchName);

        // Retrieve branch menu quantities for each menu
        List<Integer> branchMenuQuantities = new ArrayList<>();
        for (Menu menu : menus) {
            String menuId = menu.getId();
            int branchMenuQuantity = menuRepositoryBranch.getBranchMenuQuantity(branchId, menuId);
            branchMenuQuantities.add(branchMenuQuantity);
        }

        // Add attributes to model for the order form view
        model.addAttribute("menus", menus);
        model.addAttribute("branchId", branchId);
        model.addAttribute("branchName", branchName);
        model.addAttribute("branchMenuQuantities", branchMenuQuantities);

        return "view/orderForm"; // Return order form view
    }

    @PostMapping("/order")
    public String postOrderForm(@PathVariable(name = "memberBranch") String branchName,
                                @RequestParam(name = "branchId") String branchId,
                                @RequestParam(name = "menuId") String menuId,
                                @RequestParam(name = "hqQuantity") int hqQuantity,
                                @RequestParam(name = "orderQuantity") int orderQuantity,
                                @RequestParam(name = "orderDate") Timestamp orderDate,
                                Model model) {

        // Redirect the user back to the orderForm page if none of select boxes are checked
        if (hqQuantity == 0 && orderQuantity == 0) {
            return "redirect:/order";
        }

        Orders orders = new Orders();
        orders.setBranchId(branchId);
        orders.setMenuId(menuId);
        orders.setHqQuantity(hqQuantity);
        orders.setOrderQuantity(orderQuantity);
        orders.setOrderDate(orderDate);

        Orders orderDetails = menuRepositoryBranch.saveOrder(orders);

        model.addAttribute("branchName",branchName);
        model.addAttribute("orderDetails", orderDetails);
        return "redirect:/admin/branch/" + branchName + "/menus/order/confirmation";
    }

    @GetMapping("/order/confirmation")
    public String getOrderConfirmation() {
        return "view/orderConfirmation";
    }


}
