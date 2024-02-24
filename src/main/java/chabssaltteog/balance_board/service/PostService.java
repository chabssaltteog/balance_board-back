package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.domain.post.Comment;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.PostDTO;
import chabssaltteog.balance_board.repository.CommentRepository;
import chabssaltteog.balance_board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostByPostId(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public List<Post> getPostsByCategory(Category category)
    {
        return postRepository.findByCategory(category);

    }

    /*public List<Post> getLatestPosts(int count) {
        return postRepository.findTopNByOrderByCreatedDesc(count);
    }*/
    public List<Post> getLatestPosts(int count) {
        return postRepository.findTopNByOrderByCreatedDesc();
    }

    /*public List<Comment> getLatestCommentsForPost(Long postId, int count) {
        return postRepository.findLatestCommentsByPostId(postId, count);
    }*/
    public List<Comment> getLatestCommentsForPost(Long postId, int count) {
        return postRepository.findLatestCommentsByPostId(postId);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Comment addCommentToPost(Long postId, Comment comment) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        comment.setPost(post);
        commentRepository.save(comment);
        post.incrementCommentCount();
        postRepository.save(post);
        return comment;
    }



}
