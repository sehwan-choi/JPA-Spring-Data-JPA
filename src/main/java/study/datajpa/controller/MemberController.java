package study.datajpa.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @GetMapping("/members2")
    public Page<MemberDto> list2(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> dto = page.map(m -> new MemberDto(m));
        return dto;
    }

    @GetMapping("/members3")
    public Result list3(
            @Qualifier("member") Pageable memberPageable,
            @Qualifier("team") Pageable teamPageable) {
        Page<Member> memberPage = memberRepository.findAll(memberPageable);
        Page<Team> teamPage = teamRepository.findAll(teamPageable);

        return new Result(memberPage,teamPage);
    }

    @PostConstruct
    public void init() {
        for (int i = 0 ; i < 100 ; i ++) {
            Member member = new Member("user"+i,i);
            memberRepository.save(member);
        }

        for (int j = 0 ; j < 100 ; j ++) {
            Team team = new Team("team" + j);
            teamRepository.save(team);
        }
    }

    @Data
    static class Result {
        private Page<Member> memberPage;
        private Page<Team> teamPage;

        public Result(Page<Member> memberPage, Page<Team> teamPage) {
            this.memberPage = memberPage;
            this.teamPage = teamPage;
        }
    }
}
