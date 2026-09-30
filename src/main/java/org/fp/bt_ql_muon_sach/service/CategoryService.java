package org.fp.bt_ql_muon_sach.service;

import org.fp.bt_ql_muon_sach.dto.CategoryForm;
import org.fp.bt_ql_muon_sach.dto.CategoryView;
import org.fp.bt_ql_muon_sach.entity.Category;
import org.fp.bt_ql_muon_sach.exception.BusinessException;
import org.fp.bt_ql_muon_sach.exception.NotFoundException;
import org.fp.bt_ql_muon_sach.repository.CategoryRepository;
import org.fp.bt_ql_muon_sach.util.PageRequests;
import org.fp.bt_ql_muon_sach.util.ViewMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<CategoryView> list(int page, int size) {
        return categoryRepository.findAll(PageRequests.of(page, size, "id", "id"))
                .map(ViewMapper::toView);
    }

    @Transactional(readOnly = true)
    public List<CategoryView> activeCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc().stream().map(ViewMapper::toView).toList();
    }

    @Transactional(readOnly = true)
    public CategoryForm getForm(Long id) {
        return ViewMapper.toForm(find(id));
    }

    @Transactional
    public void save(CategoryForm form) {
        String name = form.getName().trim();
        boolean duplicated = form.getId() == null
                ? categoryRepository.existsByNameIgnoreCase(name)
                : categoryRepository.existsByNameIgnoreCaseAndIdNot(name, form.getId());
        if (duplicated) {
            throw new BusinessException("Tên thể loại đã tồn tại.");
        }
        Category category = form.getId() == null ? new Category() : find(form.getId());
        category.setName(name);
        category.setDescription(blankToNull(form.getDescription()));
        category.setActive(form.isActive());
        categoryRepository.save(category);
    }

    @Transactional
    public void softDelete(Long id) {
        Category category = find(id);
        category.setActive(false);
    }

    private Category find(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thể loại."));
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
