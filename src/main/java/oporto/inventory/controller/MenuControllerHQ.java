package oporto.inventory.controller;

import oporto.inventory.domain.Menu;
import oporto.inventory.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller // A Spring MVC controller
@RequestMapping("/admin/hq/menus") // Maps the controller to the specified URL path
public class MenuControllerHQ {

    private final MenuRepository menuRepository; // Dependency injection for MenuRepository

    @Autowired
    public MenuControllerHQ(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }


    @GetMapping // HTTP GET requests for displaying menus
    public String menus(Model model) {
        List<Menu> menus = menuRepository.allItem(); // Retrieves all menus from the repository
        model.addAttribute("menus",menus); // Adds the list of menus to the model attribute
        return "view/menus"; // Return view page
    }


    @GetMapping("/{menuId}") // HTTP GET requests for displaying menu details
    public String menu(@PathVariable(name="menuId") String id, Model model) {
        // The menuId is extracted from the URL path using @PathVariable annotation
        Menu menu = menuRepository.searchMenuById(id);
        model.addAttribute("menu", menu);
        return "view/menu";
    }


    @GetMapping("/register") // HTTP GET requests for displaying the menu registration form
    public String getRegistrationForm() {
        return "view/menuRegistrationForm";
    }


    @PostMapping("/register") // HTTP POST requests for the menu registration
    public String postRegistrationForm(@RequestParam(name = "menuCategory", defaultValue = "na") String menuCategory,
                                       @RequestParam(name = "menuName", defaultValue = "na") String menuName,
                                       @RequestParam(name = "menuPrice", defaultValue = "0.0") double menuPrice,
                                       @RequestParam(name = "menuQuantity", defaultValue = "0") int menuQuantity,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {

        // Redirect the user back to the registration page if any of the fields are empty or there is an invalid input
        if (menuCategory.equals("na") || menuName.equals("na") || menuPrice <= 0.0 || menuQuantity <= 0) {
            return "redirect:/admin/hq/menus/register";
        }

        Menu menu = new Menu();
        menu.setMenuCategory(menuCategory);
        menu.setMenuName(menuName);
        menu.setMenuPrice(menuPrice);
        menu.setMenuQuantity(menuQuantity);

        Menu savedMenu = menuRepository.saveMenu(menu);
        model.addAttribute("menu", menu);
        redirectAttributes.addAttribute("menuId", savedMenu.getId());
        redirectAttributes.addAttribute("registerStatus", true);
        return "redirect:/admin/hq/menus/{menuId}"; // Redirect (POST -> GET)
    }


    @GetMapping("/{menuId}/edit") // HTTP GET requests for displaying the edit form
    public String getEditForm(@PathVariable(name = "menuId") String menuId, Model model) {
        Menu menu = menuRepository.searchMenuById(menuId);
        model.addAttribute("menu", menu);
        return "view/editMenu";
    }


    @PostMapping("/{menuId}/edit") // HTTP GET requests for editing the form
    public String postEditForm(@PathVariable(name = "menuId") String menuId,
                               @RequestParam(name = "menuCategory", defaultValue = "na") String menuCategory,
                               @RequestParam(name = "menuName", defaultValue = "na") String menuName,
                               @RequestParam(name = "menuPrice", defaultValue = "0.0") double menuPrice,
                               @RequestParam(name = "menuQuantity", defaultValue = "0") int menuQuantity,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // Redirect the user back to the edit page if any of the fields are empty or there is an invalid input
        if (menuCategory.equals("na") || menuName.equals("na") || menuPrice <= 0.0 || menuQuantity <= 0) {
            return "redirect:/admin/hq/menus/{menuId}/edit";
        }

        Menu menu = new Menu();
        menu.setMenuCategory(menuCategory);
        menu.setMenuName(menuName);
        menu.setMenuPrice(menuPrice);
        menu.setMenuQuantity(menuQuantity);
        menuRepository.updateMenu(menuId,menu);
        redirectAttributes.addAttribute("editStatus", true);
        return "redirect:/admin/hq/menus/{menuId}"; // Redirect (POST -> GET)
    }


    @GetMapping("/{menuId}/delete") // HTTP GET requests for deleting the item
    public String deleteMenu(@PathVariable(name = "menuId") String id,
                             RedirectAttributes redirectAttributes) {
        menuRepository.deleteMenu(id);
        redirectAttributes.addAttribute("deleteStatus", true);
        return "redirect:/admin/hq/menus";
    }
}
