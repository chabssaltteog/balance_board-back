package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.dto.member.ProfilePostResponseDTO;
import chabssaltteog.balance_board.dto.withdrawal.MailRequestDto;
import chabssaltteog.balance_board.service.member.WithdrawalService;
import chabssaltteog.balance_board.util.MailUtil;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/withdrawal")
@RequiredArgsConstructor
public class WithdrawController {

    private final WithdrawalService withdrawalService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = MailRequestDto.class))}),
            @ApiResponse(responseCode = "400", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = ProfileController.ProfileFailResponse.class))})
    })
    @PostMapping("/sendmail")
    public boolean sendmail(@RequestBody MailRequestDto requestDto){
        return withdrawalService.sendMail(requestDto);
    }

//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Success",
//                    content = {@Content(schema = @Schema(implementation = MailRequestDto.class))}),
//            @ApiResponse(responseCode = "400", description = "Fail",
//                    content = {@Content(schema = @Schema(implementation = ProfileController.ProfileFailResponse.class))})
//    })
//    @PostMapping("/deleteUserData")
//    public boolean withdraw(){
//
//    }
}

