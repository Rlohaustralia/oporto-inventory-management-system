package oporto.inventory.controller;


import oporto.inventory.domain.Menu;
import oporto.inventory.domain.Orders;
import oporto.inventory.repository.MenuRepository;
import oporto.inventory.repository.MenuRepositoryBranch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



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
    public String menus(@PathVariable(name = "memberBranch") String branchName,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size,
                        Model model) {

        // Retrieve all menus
//        List<Menu> menus = menuRepository.allItem();

        // Retrieve branch ID using branch name
        String branchId = menuRepositoryBranch.getBranchId(branchName);

        int offset = page * size;
        List<Menu> menus = menuRepository.findMenusByBranchId(branchId, offset, size);
        int totalMenus = menuRepository.countMenusByBranchId(branchId);
        int totalPages = (int) Math.ceil((double) totalMenus /size);

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
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalMenus);
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
                                @RequestParam(name = "selectedMenus") List<String> selectedMenus,
                                @RequestParam(name = "orderQuantities") List<Integer> AllOrderQuantities,
                                RedirectAttributes redirectAttributes) {

        // Remove zeros from orderQuantities list so that we can only extract from selected menus' order quantities.
        List<Integer> orderQuantities = new ArrayList<>(selectedMenus.size());
        for (int i = 0; i < AllOrderQuantities.size(); i++) {
            if (AllOrderQuantities.get(i) != 0) {
                orderQuantities.add(AllOrderQuantities.get(i));
            }
        }

        // Validate if both selectedMenus and orderQuantities are not null and have the same size
        if (selectedMenus.size() != orderQuantities.size()) {
            return "redirect:/admin/branch/" + branchName + "/menus/order";
        }

        List<Orders> orderDetails = new ArrayList<>();
        List<Integer> orderIds = new ArrayList<>();

        try {
            for (int i = 0; i < selectedMenus.size(); i++) {
                String menuId = selectedMenus.get(i);
                int orderQuantity = orderQuantities.get(i);

                Orders orders = new Orders();

                String branchId = menuRepositoryBranch.getBranchId(branchName);
                orders.setBranchId(branchId);
                orders.setMenuId(menuId);
                orders.setMenuName(menuRepository.searchMenuNameById(menuId));
                int hqQuantity = menuRepository.searchMenuQuantity(menuId);
                orders.setHqQuantity(hqQuantity);
                orders.setOrderQuantity(orderQuantity);
                orders.setOrderDate(new Timestamp(System.currentTimeMillis()));

                // Save order and fetch orderId
                orderIds.add(menuRepositoryBranch.saveOrder(orders));
                orderDetails.add(orders);
            }

            redirectAttributes.addFlashAttribute("orderDetails", orderDetails);
            redirectAttributes.addFlashAttribute("orderIds", orderIds);
            redirectAttributes.addFlashAttribute("branchName", branchName);

            return "redirect:/admin/branch/" + branchName + "/menus/order/confirmation";

        } catch (Exception e) {
            // Reload the page
            return "redirect:/admin/branch/" + branchName + "/menus/order";
        }
    }


    @GetMapping("/order/confirmation")
    public String getOrderConfirmation(@PathVariable(name = "memberBranch") String branchName) {
        return "view/orderConfirmation";
    }


    @PostMapping("/order/confirmation")
    public String cancelOrder(@PathVariable(name = "memberBranch") String branchName,
                              @RequestParam List<Integer> orderIds,
                              RedirectAttributes redirectAttributes) {

        try {
            // Loop through orderDetails and cancel each order by orderId
            for (int orderId : orderIds) {
                menuRepositoryBranch.cancelOrderByOrderId(orderId);
            }
        } catch (Exception e) {
            // Handle exceptions
            return "redirect:/admin/branch/" + branchName + "/menus/order/confirmation";
        }
        return "redirect:/admin/branch/" + branchName + "/menus"; // Redirect to menus page after cancellation
    }

    @GetMapping("/search")
    public String searchMenus(@PathVariable(name = "memberBranch") String branchName,
                              @RequestParam(name = "keyword", required = false, defaultValue = "")
                                  String keyword, Model model) {

        List<Menu> menus = menuRepository.searchMenuByKeyword(keyword);
        String branchId = menuRepositoryBranch.getBranchId(branchName);
        

        // Retrieve branch menu quantities for each menu
        List<Integer> branchMenuQuantities = new ArrayList<>();
        for (Menu menu : menus) {
            String menuId = menu.getId();
            int branchMenuQuantity = menuRepositoryBranch.getBranchMenuQuantity(branchId, menuId);
            branchMenuQuantities.add(branchMenuQuantity);
        }

        model.addAttribute("menus", menus);
        if (branchId != null) model.addAttribute("branchId", branchId);
        if (branchName != null) model.addAttribute("branchName", branchName);
        model.addAttribute("branchMenuQuantities", branchMenuQuantities);

        return "view/menusBranch";
    }
}

