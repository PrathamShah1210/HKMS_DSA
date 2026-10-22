package com.hkms.repository;

import com.hkms.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {

    List<CategoryEntity> findByParentId(String parentId);

    List<CategoryEntity> findByParentIdIsNull();
}
