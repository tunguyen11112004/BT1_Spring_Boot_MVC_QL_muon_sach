package org.fp.bt_ql_muon_sach.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageRequests {

    private PageRequests() {
    }

    public static Pageable of(int page, int size, String sort, String... allowed) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        Set<String> fields = Set.of(allowed);
        String field = fields.contains(sort) ? sort : allowed[0];
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, field));
    }
}
