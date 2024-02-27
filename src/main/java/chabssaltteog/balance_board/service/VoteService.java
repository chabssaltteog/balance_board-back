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

        try {
            Optional<Vote> vote1 = voteRepository.findById(voteRequestDTO.getVoteId());//투표 찾고

            if (vote1.isEmpty()) {
                throw new RuntimeException("Vote not found");
            }

            Vote vote = vote1.get();

            // 중복 투표 확인
            boolean checkVoted = voteMemberRepository.existsByVoteAndUser_UserId(vote, voteRequestDTO.getUserId());
            if (checkVoted) {
                throw new RuntimeException("User has already voted for this poll");
            }

            vote.participate(voteRequestDTO.getUserId(), voteRequestDTO.getSelectedOption());//투표항목 투표수 증가

            //게시글 찾고
            Post post = postRepository.findById(voteRequestDTO.getPostId()).orElseThrow(() -> new RuntimeException("Post not found"));

            //게시글 투표 참여자 수 증가
            post.incrementVoteCount();

            //업데이트된 투표정보 저장
            postRepository.save(post);

            //voteMember 레코드값 저장
            return voteMemberRepository.save(new VoteMember(vote, memberRepository.findByUserId(voteRequestDTO.getUserId()), voteRequestDTO.getSelectedOption()));
        } catch (Exception e) {
            log.error("Error in participateVote method.", e);
            throw new RuntimeException("Error participating in vote.", e);
        }
    }
}