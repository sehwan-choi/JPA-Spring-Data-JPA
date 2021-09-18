package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    List<Member> findMemberAllSelectBy();
    long countMemberAllSelectBy();
    boolean existsMemberById(Long id);
    long deleteMemberAllBy();
    long deleteMemberById(Long id);
    List<Member> findMemberDistinctBy();
    List<Member> findFirst3By();
    List<Member> findTop3By();
    List<Member> findFirstBy();

    /**
     * @Query 어노테이션이 없어도 정상 실행된다.
     * JpaRepository<Member, Long> 에 Member Entity이름과 메소드 이름으로 제일먼저 NamedQuery가 있는지 확인한다.
     * 아래 메소드 실행시 Member.findByUsername 가 된다.
     * 만일 Member.class에 NamedQuery에 설정된 name이 다른경우는 @Query로 name을 잡아 줘야 정상 실행이 된다.
     */
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);   //  컬렉션
    Member findMemberByUsername(String username);       //  단건
    Optional<Member> findOptionalByUsername(String username);       //  단건

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    //Page<Member> findByAge(int age, Pageable pageable);
    //Slice<Member> findByAge(int age, Pageable pageable);
    //List<Member> findByAge(int age, Pageable pageable);


    /**
     * @Modiffing 어노테이션 꼭 해서 update 문이라는 것을 명시해야한다. 하지 않으면 아래 예외 발생
     * clearAutomatically = true : update 쿼리 실행후 영속성 컨텍스트에 남아있던 데이터를 DB에 반영후 영속성 컨텍스트를 clear한다.
     *  => em.flush() 후 em.clear() 와 같다.
     * [org.springframework.dao.InvalidDataAccessApiUsageException: org.hibernate.hql.internal.QueryExecutionRequestException: Not supported for DML operations]
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
