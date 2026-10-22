package com.hkms.repository;

import com.hkms.model.entity.PaperEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<PaperEntity, Long> {

    List<PaperEntity> findByCategoryId(String categoryId);

    List<PaperEntity> findByCategoryIdIn(List<String> categoryIds);

    List<PaperEntity> findTop10ByOrderByPriorityScoreDesc();
}
