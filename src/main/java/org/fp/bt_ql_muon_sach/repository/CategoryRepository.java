package org.fp.bt_ql_muon_sach.repository;

import org.fp.bt_ql_muon_sach.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Category> findByActiveTrueOrderByNameAsc();
}
