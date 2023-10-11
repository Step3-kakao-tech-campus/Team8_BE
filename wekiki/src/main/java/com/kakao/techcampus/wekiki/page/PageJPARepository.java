package com.kakao.techcampus.wekiki.page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PageJPARepository extends JpaRepository<PageInfo, Long> {

    @Query("SELECT p FROM PageInfo p WHERE p.title LIKE :keyword%")
    Page<PageInfo> findPagesByTitleContainingKeyword(String keyword, Pageable pageable);

}
