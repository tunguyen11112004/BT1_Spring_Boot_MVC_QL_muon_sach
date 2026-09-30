package org.fp.bt_ql_muon_sach.service;

import org.fp.bt_ql_muon_sach.dto.MemberForm;
import org.fp.bt_ql_muon_sach.dto.MemberView;
import org.fp.bt_ql_muon_sach.entity.Member;
import org.fp.bt_ql_muon_sach.entity.MemberStatus;
import org.fp.bt_ql_muon_sach.exception.BusinessException;
import org.fp.bt_ql_muon_sach.exception.NotFoundException;
import org.fp.bt_ql_muon_sach.repository.MemberRepository;
import org.fp.bt_ql_muon_sach.util.PageRequests;
import org.fp.bt_ql_muon_sach.util.ViewMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public Page<MemberView> list(MemberStatus status, int page, int size) {
        var pageable = PageRequests.of(page, size, "id", "id", "createdAt");
        Page<Member> result = status == null
                ? memberRepository.findAll(pageable)
                : memberRepository.findByStatus(status, pageable);
        return result.map(ViewMapper::toView);
    }

    @Transactional(readOnly = true)
    public List<MemberView> activeMembers() {
        return memberRepository.findActive().stream().map(ViewMapper::toView).toList();
    }

    @Transactional(readOnly = true)
    public MemberForm getForm(Long id) {
        return ViewMapper.toForm(find(id));
    }

    @Transactional
    public void save(MemberForm form) {
        String email = form.getEmail().trim().toLowerCase();
        boolean duplicated = form.getId() == null
                ? memberRepository.existsByEmailIgnoreCase(email)
                : memberRepository.existsByEmailIgnoreCaseAndIdNot(email, form.getId());
        if (duplicated) {
            throw new BusinessException("Email thành viên đã tồn tại.");
        }
        Member member = form.getId() == null ? new Member() : find(form.getId());
        member.setFullName(form.getFullName().trim());
        member.setEmail(email);
        member.setPhone(form.getPhone() == null || form.getPhone().isBlank() ? null : form.getPhone().trim());
        member.setStatus(form.getStatus());
        memberRepository.save(member);
    }

    @Transactional
    public void toggleLock(Long id) {
        Member member = find(id);
        member.setStatus(member.getStatus() == MemberStatus.BLOCKED ? MemberStatus.ACTIVE : MemberStatus.BLOCKED);
    }

    private Member find(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thành viên."));
    }
}
