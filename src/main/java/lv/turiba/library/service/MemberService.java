package lv.turiba.library.service;

import lv.turiba.library.model.Member;
import lv.turiba.library.repository.LoanRepository;
import lv.turiba.library.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** CRUD for members. E-mail must be unique; a member with loan history cannot be deleted. */
@Service
public class MemberService {

    private final MemberRepository members;
    private final LoanRepository loans;

    public MemberService(MemberRepository members, LoanRepository loans) {
        this.members = members;
        this.loans = loans;
    }

    public List<Member> findAll() {
        return members.findAllByOrderByLastNameAscFirstNameAsc();
    }

    public Member findById(Long id) {
        return members.findById(id).orElseThrow(() -> new NotFoundException("Member", id));
    }

    @Transactional
    public Member create(Member member) {
        if (members.existsByEmailIgnoreCase(member.getEmail())) {
            throw new BusinessRuleException("email", "A member with e-mail " + member.getEmail() + " already exists");
        }
        if (member.getRegisteredOn() == null) {
            member.setRegisteredOn(LocalDate.now());
        }
        member.setId(null);
        return members.save(member);
    }

    @Transactional
    public Member update(Long id, Member changes) {
        Member existing = findById(id);
        if (members.existsByEmailIgnoreCaseAndIdNot(changes.getEmail(), id)) {
            throw new BusinessRuleException("email", "A member with e-mail " + changes.getEmail() + " already exists");
        }
        existing.setFirstName(changes.getFirstName());
        existing.setLastName(changes.getLastName());
        existing.setEmail(changes.getEmail());
        existing.setPhone(changes.getPhone());
        if (changes.getRegisteredOn() != null) {
            existing.setRegisteredOn(changes.getRegisteredOn());
        }
        return members.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Member existing = findById(id);
        if (loans.existsByMemberId(id)) {
            throw new BusinessRuleException(existing.getFullName()
                    + " has loan records and cannot be deleted");
        }
        members.delete(existing);
    }
}
