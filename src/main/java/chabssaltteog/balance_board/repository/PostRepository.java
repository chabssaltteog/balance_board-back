package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

//    @Query("SELECT p FROM Post p JOIN FETCH p.category c WHERE c = :category")
    public List<Post> findByCategory(Category category, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.vote v WHERE p.postId = :postId")
    public Optional<Post> findByIdWithVote(Long postId);

}
