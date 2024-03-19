package oporto.inventory.controller;

import lombok.RequiredArgsConstructor;
import oporto.inventory.domain.Member;
import oporto.inventory.repository.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String signUp(@RequestParam(name = "memberEmail", defaultValue = "na") String memberEmail,
                         @RequestParam (name = "memberPassword", defaultValue = "na") String memberPassword,
                         @RequestParam (name = "memberName", defaultValue = "na") String memberName,
                         @RequestParam (name = "memberPosition", defaultValue = "na") String memberPosition,
                         @RequestParam (name = "memberBranch", defaultValue = "na") String memberBranch,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        // Redirect the user back to the signup page if any of the fields are empty
        if (memberEmail.equals("na") || memberPassword.equals("na") || memberName.equals("na") ||
                memberPosition.equals("na") || memberBranch.equals("na")) {
            return "redirect:/signup";
        }

        Member member = new Member();
        member.setMemberEmail(memberEmail);
        member.setMemberPassword(memberPassword);
        member.setMemberName(memberName);
        member.setMemberPosition(memberPosition);
        member.setMemberBranch(memberBranch);

        Member savedMember = memberRepository.saveMember(member);

        // if there is a duplicate email then redirect to signup page
        if (savedMember == null) {
            model.addAttribute("errorMessage", "Email already exists. Please use a different email.");
            redirectAttributes.addAttribute("duplicateEmail", true);
            return "redirect:/signup";
        }

        // else
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
