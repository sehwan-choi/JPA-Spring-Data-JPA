package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final MemberRepository memberRepository;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/api/mapping")
    public Page<MemberDto> mapping() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        //when
        int age = 10;

        // 첫번째 파라미터 : 현재 페이지
        // 두번째 파라미터 : 조회할 데이터 수
        // 세번째 파라미터 : 소팅 방법
        // 네번째 파라미터 : 소팅할 컬럼
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // API 결과 반환시 Entity를 그대로 반환하면 안되기 때문에 Dto로 변경해서 반환한다.
        return page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
    }
}
