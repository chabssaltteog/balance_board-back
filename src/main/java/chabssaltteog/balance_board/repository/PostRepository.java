package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByCategory(Category category, Pageable pageable);

    @Query("select p from Post p where (p.commentCount + p.likeCount + p.voteCount) >= 15 order by p.created desc ")
    List<Post> findHotPosts(Pageable pageable);
}
