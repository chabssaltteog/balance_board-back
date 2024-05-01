package chabssaltteog.balance_board.service;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.vote.Vote;
import chabssaltteog.balance_board.domain.vote.VoteMember;
import chabssaltteog.balance_board.domain.post.Post;
import chabssaltteog.balance_board.dto.vote.AnonymousVoteResponseDTO;
import chabssaltteog.balance_board.dto.vote.VoteRequestDTO;
import chabssaltteog.balance_board.dto.vote.VoteResponseDTO;
import chabssaltteog.balance_board.exception.DuplicateVoteException;
import chabssaltteog.balance_board.exception.InvalidUserException;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.PostRepository;
import chabssaltteog.balance_board.repository.VoteMemberRepository;
import chabssaltteog.balance_board.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteService {

    private final PostRepository postRepository;
    private final VoteMemberRepository voteMemberRepository;
    private final MemberRepository memberRepository;
    private final VoteRepository voteRepository;
    private final MainService mainService;


    @Transactional
    public VoteResponseDTO participateVote(VoteRequestDTO voteRequestDTO, Authentication authentication) {

        Member member = mainService.getMember(authentication);
        if (!member.getUserId().equals(voteRequestDTO.getUserId())) {
            throw new NotFoundException("사용자 정보가 맞지 않습니다.");
        }

        Optional<Vote> optionalVote = voteRepository.findById(voteRequestDTO.getVoteId());
        if (optionalVote.isEmpty()) {
            throw new NotFoundException("올바른 투표가 아닙니다.");
        }
        Vote vote = optionalVote.get();

        Post post = postRepository.findById(voteRequestDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 중복 투표 확인
        boolean checkVoted = voteMemberRepository.existsByVoteAndUser_UserId(vote, voteRequestDTO.getUserId());
        if (checkVoted) {
            throw new DuplicateVoteException("이미 투표하였습니다.");
        }

        vote.participate(voteRequestDTO.getSelectedOption());

        post.incrementVoteCount();

        voteMemberRepository.save(new VoteMember(vote, memberRepository.findByUserId(voteRequestDTO.getUserId()),
                voteRequestDTO.getSelectedOption()));

        int option1Count = vote.getOption1Count();
        int option2Count = vote.getOption2Count();

        int experiencePoints = member.incrementExperiencePoints(5);
        member.updateLevel(experiencePoints);

        return VoteResponseDTO.builder()
                .voteId(voteRequestDTO.getVoteId())
                .userId(voteRequestDTO.getUserId())
                .selectedOption(voteRequestDTO.getSelectedOption())
                .option1Count(option1Count)
                .option2Count(option2Count)
                .build();
    }

    public AnonymousVoteResponseDTO anonymousVote(VoteRequestDTO voteRequestDTO) {

        Long voteId = voteRequestDTO.getVoteId();
        if (voteRepository.findById(voteId).isEmpty()) {
            throw new NotFoundException("올바른 투표가 아닙니다.");
        }
        Vote vote = voteRepository.findById(voteId).get();

        return AnonymousVoteResponseDTO.builder()
                .option1Count(vote.getOption1Count())
                .option2Count(vote.getOption2Count())
                .build();
    }
}