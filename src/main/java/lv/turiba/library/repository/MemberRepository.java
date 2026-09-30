package lv.turiba.library.repository;

import lv.turiba.library.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByOrderByLastNameAscFirstNameAsc();

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
