package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.dto.withdrawal.MailRequestDto;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.service.member.WithdrawalService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/api/withdrawal")
@RequiredArgsConstructor
public class WithdrawController {

    private final WithdrawalService withdrawalService;
    private final MemberRepository memberRepository;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = MailRequestDto.class))}),
            @ApiResponse(responseCode = "400", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = ProfileController.ProfileFailResponse.class))})
    })
    @PostMapping("/sendMail")   //회원탈퇴 이메일 전송
    @ResponseBody
    public boolean sendmail(@RequestBody MailRequestDto requestDto){
        return withdrawalService.sendMail(requestDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = MailRequestDto.class))}),
            @ApiResponse(responseCode = "400", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = ProfileController.ProfileFailResponse.class))})
    })
    @GetMapping
    public String withdraw(@RequestParam("withdrawalCode") int withdrawalCode, Model model){
        boolean result = withdrawalService.withdraw(withdrawalCode);

        if(result){
            return "withdrawalSuccess";
        }else{
            return "error/withdrawalFail";
        }
    }
}

