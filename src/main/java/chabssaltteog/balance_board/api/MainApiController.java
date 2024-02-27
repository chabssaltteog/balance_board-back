package chabssaltteog.balance_board.api;

import chabssaltteog.balance_board.domain.Vote;
import chabssaltteog.balance_board.domain.VoteMember;
import chabssaltteog.balance_board.domain.post.Category;
import chabssaltteog.balance_board.dto.*;
import chabssaltteog.balance_board.repository.VoteRepository;
import chabssaltteog.balance_board.service.MainService;
import chabssaltteog.balance_board.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Tag(name = "Main Page", description = "Main Page API")
@Slf4j
public class MainApiController {

    private final MainService mainService;
    private final VoteService voteService;
    private final VoteRepository voteRepository;

    @GetMapping("/posts") //게시글 20개씩 출력
    @Operation(summary = "All Posts", description = "모든 게시글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = PostDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    public List<PostDTO> getAllPosts(
            @RequestParam(defaultValue = "0", value="page")  int page,
            @RequestParam(defaultValue = "20", value="size")  int size
    ) {
        log.info("== GET ALL POSTS ==");
        return mainService.getAllPosts(page, size);

    }

    @GetMapping("/posts/{postId}")
    @Operation(summary = "Post Detail", description = "게시글 상세")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = PostDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    public PostDTO getPost(@PathVariable(name ="postId") Long postId, @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("POST DETAIL : postId = {}", postId);
        if (token == null) {
            log.info("==NO TOKEN 상세 페이지 조회==");
            return mainService.getPostByPostId(postId);
        }
        return mainService.getPostByPostId(postId, token);
    }

    @GetMapping("/{category}") //카테고리 필터링, 20개씩 출력
    @Operation(summary = "Category filtering", description = "카테고리 필터링")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = PostDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Fail")
    })
    public List<PostDTO> getPostByCategory(
            @PathVariable(name = "category") Category category,
            @RequestParam(defaultValue = "0", value="page")  int page,
            @RequestParam(defaultValue = "20", value="size")  int size
    ) {
        log.info("CATEGORY SEARCH : category = {}", category);
        return mainService.getPostsByCategory(category, page, size);
    }

    @PostMapping("/new/post")
    @Operation(summary = "Create POST", description = "게시글 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = CreatePostResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = CreatePostFailResponseDTO.class))})
    })
    public Object createPost(@RequestBody CreatePostRequestDTO requestDTO) {
        try {
            CreatePostResponseDTO postResponseDTO = mainService.createPost(requestDTO);
            log.info("CREATE POST : userID = {}", postResponseDTO.getUserId());
            log.info("CREATE POST : postID = {}", postResponseDTO.getPostId());
            log.info("CREATE POST : Created Time = {}", postResponseDTO.getCreated());
            return postResponseDTO;
        } catch (Exception e) {
            String message = e.getMessage();
            log.info("exception message = {}", message);
            return new CreatePostFailResponseDTO(requestDTO.getUserId(), "게시글 작성 실패");
        }

    }

    @PostMapping("/new/comment")
    @Operation(summary = "Create COMMENT", description = "댓글 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = CommentDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = CreateCommentFailResponseDTO.class))})
    })
    public Object createComment(@RequestBody CreateCommentRequestDTO requestDTO) {
        try {
            CommentDTO commentDTO = mainService.addCommentToPost(requestDTO);
            log.info("CREATE COMMENT : userID = {}", commentDTO.getUserId());
            log.info("CREATE COMMENT : nickname = {}", commentDTO.getNickname());
            log.info("CREATE COMMENT : Created Time = {}", commentDTO.getCreated());
            return commentDTO;
        } catch (Exception e) {
            String message = e.getMessage();
            log.info("exception message = {}", message);
            return new CreateCommentFailResponseDTO(requestDTO.getUserId(), "댓글 작성 실패");
        }

    }

    @PostMapping("/new/vote")
    @Operation(summary = "Create VOTE", description = "투표 실행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = VoteResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = VoteFailResponseDTO.class))})
    })
    public Object doVote(@RequestBody VoteRequestDTO voteRequestDTO){

        try {
            VoteMember voteMember = voteService.participateVote(voteRequestDTO);

            Optional<Vote> optionalVote = voteRepository.findById(voteRequestDTO.getVoteId());
            int option1Count = optionalVote.get().getOption1Count();
            int option2Count = optionalVote.get().getOption2Count();

            log.info("user_id={}", voteMember.getUser().getUserId());
            log.info("vote_id={}", voteMember.getVote().getVoteId());
            return VoteResponseDTO.builder()
                    .voteId(voteMember.getVote().getVoteId())
                    .userId(voteMember.getUser().getUserId())
                    .selectedOption(voteRequestDTO.getSelectedOption())
                    .option1Count(option1Count)
                    .option2Count(option2Count)
                    .build();
        } catch (Exception e) {
            log.info(e.getMessage());
            log.info("==VOTE FAIL==");
            return new VoteFailResponseDTO(voteRequestDTO.getUserId(), voteRequestDTO.getVoteId(), "Vote Fail");
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(title = "POS_RES_02 : 게시글 작성 실패 응답 DTO")
    static class CreatePostFailResponseDTO {
        @Schema(description = "작성자 ID", example = "6")
        private Long userId;

        @Schema(description = "실패 메세지", example = "게시글 작성 실패")
        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(title = "COM_RES_02 : 댓글 작성 실패 응답 DTO")
    static class CreateCommentFailResponseDTO {
        @Schema(description = "작성자 ID", example = "6")
        private Long userId;

        @Schema(description = "실패 메세지", example = "댓글 작성 실패")
        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(title = "VOT_RES_02 : 투표 실패 응답 DTO")
    static class VoteFailResponseDTO {
        @Schema(description = "작성자 ID", example = "6")
        private Long userId;

        @Schema(description = "투표 ID == postId", example = "3")
        private Long voteId;

        @Schema(description = "실패 메세지", example = "투표 실패")
        private String message;
    }



}
