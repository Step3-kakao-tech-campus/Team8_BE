package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.SearchGroupDTO;
import com.kakao.techcampus.wekiki.group.officialGroup.OfficialGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.closedGroup.UnOfficialClosedGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
public interface GroupJPARepository extends JpaRepository<Group, Long> {

    /*
        그룹 검색 용
     */
    @Query("SELECT g FROM OfficialGroup g WHERE g.groupName LIKE CONCAT('%', :keyword, '%')")
    List<OfficialGroup> findOfficialGroupsByKeyword(@Param("keyword") String keyword);
    @Query("SELECT g FROM UnOfficialOpenedGroup g WHERE g.groupName LIKE CONCAT('%', :keyword, '%')")
    List<UnOfficialOpenedGroup> findUnOfficialOpenedGroupsByKeyword(@Param("keyword") String keyword);

    /*
        비공식 공개 그룹 상세 조회
     */
    @Query("SELECT g FROM UnOfficialOpenedGroup g WHERE g.id = :id")
    UnOfficialOpenedGroup findUnOfficialOpenedGroupById(@Param("id") Long id);


    /*
        초대 링크
     */
    @Query("SELECT g FROM UnOfficialClosedGroup g WHERE g.id = :id")
    UnOfficialClosedGroup findUnOfficialClosedGroupById(@Param("id") Long id);
}
