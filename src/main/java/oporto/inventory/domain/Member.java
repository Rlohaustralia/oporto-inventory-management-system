package oporto.inventory.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class Member {

    private String id;
    private String memberEmail;
    private String memberPassword;
    private String memberName;
    private String memberPosition;
    private String memberBranch;

    public Member(String memberEmail, String memberPassword, String memberName, String memberPosition, String memberBranch) {
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.memberName = memberName;
        this.memberPosition = memberPosition;
        this.memberBranch = memberBranch;
    }
}

