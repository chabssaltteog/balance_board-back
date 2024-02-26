package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.domain.VoteMember;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.CommentDTO;
import chabssaltteog.balance_board.dto.PostDTO;
import chabssaltteog.balance_board.dto.VoteRequestDTO;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.PostRepository;
import chabssaltteog.balance_board.repository.VoteMemberRepository;
import chabssaltteog.balance_board.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteService {

    private final PostRepository postRepository;
    private final VoteMemberRepository voteMemberRepository;
    private final MemberRepository memberRepository;
    private final VoteRepository voteRepository;


    @Transactional
    public VoteMember participateVote(VoteRequestDTO voteRequestDTO) {
        Optional<Vote> optionalVote = voteRepository.findById(voteRequestDTO.getVoteId());
        if (!optionalVote.isPresent()) {
            throw new RuntimeException("투표 정보가 없습니다.");
        }
        Vote vote = optionalVote.get();
        vote.participate(voteRequestDTO.getUserId(), voteRequestDTO.getSelectedOption());

        Post post = postRepository.findById(voteRequestDTO.getPostId()).orElseThrow(() -> new RuntimeException("Post not found"));

        post.incrementVoteCount();

        postRepository.save(post);

        return voteMemberRepository.save(new VoteMember(vote, memberRepository.findByUserId(voteRequestDTO.getUserId()),
                voteRequestDTO.getSelectedOption()));
    }
}