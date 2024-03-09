package oporto.inventory.controller;

import lombok.RequiredArgsConstructor;
import oporto.inventory.domain.Member;
import oporto.inventory.repository.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    // Welcome page
    @GetMapping("/")
    public String index() {
        return "view/index";
    }

    @GetMapping("/signup") // HTTP GET requests for displaying the member registration form
    public String signUpForm() {
        return "view/signup";
    }

    @PostMapping("/signup") // HTTP GET requests for member registration
    public String signUp(@RequestParam(name = "memberEmail") String memberEmail,
                         @RequestParam (name = "memberPassword") String memberPassword,
                         @RequestParam (name = "memberName") String memberName,
                         @RequestParam (name = "memberPosition") String memberPosition,
                         @RequestParam (name = "memberBranch") String memberBranch,
                         Model model) {

        Member member = new Member();
        member.setMemberEmail(memberEmail);
        member.setMemberPassword(memberPassword);
        member.setMemberName(memberName);
        member.setMemberPosition(memberPosition);
        member.setMemberBranch(memberBranch);

        Member savedMember = memberRepository.saveMember(member);
        model.addAttribute("member",savedMember);

        return "redirect:/";
    }

    @GetMapping("/signin")
    public String loginForm() {
        return "view/login";
    }

    @PostMapping("/signin")
    public String login(@RequestParam (name = "memberEmail") String memberEmail,
                        @RequestParam (name = "memberPassword") String memberPassword) {

        Optional<Member> loggedInMember = memberRepository.login(memberEmail, memberPassword);

        if(loggedInMember.isPresent()) {
            // Success to log in
            return "redirect:/admin/menus";
        } else {
            // Fail to log in
            return "view/login";
        }
    }
}
