package oporto.inventory.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter //  to automatically generate code such as getters, setters and constructor
@RequiredArgsConstructor
public class Menu {

    // Define fields
    private String id;
    private String menuCategory;
    private String menuName;
    private Double menuPrice;
    private Integer menuQuantity;

    public Menu(String menuCategory, String menuName, Double menuPrice, Integer menuQuantity) {
        this.menuCategory = menuCategory;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuQuantity = menuQuantity;
    }
}
