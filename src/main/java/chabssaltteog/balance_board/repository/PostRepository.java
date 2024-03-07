package chabssaltteog.balance_board.repository;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    public List<Post> findByCategory(Category category, Pageable pageable);

    public List<Post> findAll(Sort sort);

}
