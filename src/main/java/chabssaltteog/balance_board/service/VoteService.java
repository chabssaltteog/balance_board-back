package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.domain.VoteMember;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.PostRepository;
import chabssaltteog.balance_board.repository.VoteMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final PostRepository postRepository;
    private final VoteMemberRepository voteMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void participateVote(Long postId, Long userId, String votedOption) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        Member user = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Vote vote = post.getVote();
        if (vote == null) {
            throw new RuntimeException("Vote not found for the post");
        }

        // 투표 항목에 참여
        vote.participate(userId, votedOption);

        // 참여한 투표를 저장
        voteMemberRepository.save(new VoteMember(vote, user, votedOption));

        // 참여한 투표 정보를 게시글에 업데이트
        postRepository.save(post);
    }
}
