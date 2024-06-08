package oporto.inventory.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class BranchMenu {

    private Long id;
    private String branchId;
    private String menuId;
    private int branchMenuQuantity;

    public BranchMenu(String branchId, String menuId, int branchMenuQuantity) {
        this.branchId = branchId;
        this.menuId = menuId;
        this.branchMenuQuantity = branchMenuQuantity;
    }
}
