package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    public List<Post> findByCategory(Category category);


    /*public List<Post> findTopNByOrderByCreatedDesc(int count);    //최근 게시글들 중에서 최대 count개의 게시글을 조회*/
    public List<Post> findTopNByOrderByCreatedDesc();    //최근 게시글들 중에서 최대 count개의 게시글을 조회

    /*@Query("SELECT c FROM Comment c WHERE c.post.postId = :postId ORDER BY c.created DESC")
    public List<Comment> findLatestCommentsByPostId(Long postId, int count);*/

    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId ORDER BY c.created DESC")
    public List<Comment> findLatestCommentsByPostId(Long postId);

}
