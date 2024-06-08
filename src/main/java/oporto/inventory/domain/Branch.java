package oporto.inventory.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class Branch {

    private String id;
    private String branchName;

    public Branch(String branchName) {
        this.branchName = branchName;
    }
}
