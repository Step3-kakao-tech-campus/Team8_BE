package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki.group.invitation.Invitation;
import com.kakao.techcampus.wekiki.group.officialGroup.OfficialGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.closedGroup.UnOfficialClosedGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupJPARepository extends JpaRepository<Group, Long> {
    /*
        그룹 검색 용
     */
    @Query("select g from OfficialGroup g")
    List<OfficialGroup> findAllOfficialGroup();
    @Query("select g from UnOfficialOpenedGroup g")
    List<UnOfficialOpenedGroup> findAllUnOfficialOpenGroup();
    @Query("SELECT g FROM OfficialGroup g WHERE g.groupName LIKE CONCAT('%', :keyword, '%')")
    Page<OfficialGroup> findOfficialGroupsByKeyword(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT g FROM UnOfficialOpenedGroup g WHERE g.groupName LIKE CONCAT('%', :keyword, '%')")
    Page<UnOfficialOpenedGroup> findUnOfficialOpenedGroupsByKeyword(@Param("keyword") String keyword, Pageable pageable);

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

    @Query("SELECT g.invitation FROM UnOfficialClosedGroup  g WHERE g.id = :id")
    Invitation findInvitationLinkByGroupId(@Param("id") Long id);
}
