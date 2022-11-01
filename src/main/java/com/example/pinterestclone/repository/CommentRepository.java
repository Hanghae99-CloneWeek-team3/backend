package com.example.pinterestclone.repository;

import com.example.pinterestclone.domain.Comment;
import com.example.pinterestclone.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.annotation.Native;
import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.rootName = 'post' and c.rootId = :rootId")
    Page<Comment> findAllByRootIdAndRootNameLikePost(@Param("rootId") Long rootId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.rootName = 'comment' and c.rootId = :rootId ")
    List<Comment> findAllByRootIdAndRootNameLikeComment(@Param("rootId") Long rootId);

   /* @Query(value = "SELECT c FROM Comment c WHERE c.rootName = 'comment' and c.rootId = :rootId and c.rootName Like % :keyword % ",
    countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.rootName = 'comment' and c.rootId = :rootId and c.rootName Like % :keyword %",
    nativeQuery = true)
    List<Comment> findRecomment(@Param("rootId") Long rootId, @Param("keyword") String keyword);*/
    int countByPostId(Long postId);

    //rootName 컬럼에서 키워드가 포함된 것을 찾음(findBy(컬럼)Containing)

    //Param JQPL Native
    @Query(value = " SELECT c.root_name, c.root_id, c.created_at FROM Comment AS c WHERE c.root_name = 'comment' and c.root_id = :rootId and c.root_name Like :keyword ORDER BY c.created_at asc LIMIT :size, :page ",
            countQuery = "SELECT COUNT(*) FROM Comment AS c WHERE c.root_name = 'comment' and c.root_id = :rootId and c.root_name Like :keyword ORDER BY c.created_at asc LIMIT :size, :page ",
    nativeQuery = true)
    List<Comment> findAllByRootNameAndRootIdOrderByCreatedAtAsc(@Param("rootId") Long rootId, @Param("keyword") String keyword, @Param("size") int size, @Param("page") int page);
}
