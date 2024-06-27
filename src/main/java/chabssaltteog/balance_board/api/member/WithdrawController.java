package chabssaltteog.balance_board.api.member;

import chabssaltteog.balance_board.dto.withdrawal.MailRequestDto;
import chabssaltteog.balance_board.exception.InvalidUserException;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.service.member.WithdrawalService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import lombok.extern.slf4j.Slf4j;


/*@Controller
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
    @PostMapping("/sendMail")   //회원탈퇴 이메일 전송
    @ResponseBody
    public ResponseEntity<String> sendmail(@RequestBody MailRequestDto requestDto, Authentication authentication){
        try {
            boolean result = withdrawalService.sendMail(requestDto, authentication);
            if (result){
                return ResponseEntity.ok("메일 전송 성공");
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송실패");
            }
        }catch (InvalidUserException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송 중 오류 발생");
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = MailRequestDto.class))}),
            @ApiResponse(responseCode = "400", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = ProfileController.ProfileFailResponse.class))})
    })
    @GetMapping
    public String withdraw(@RequestParam("withdrawalCode") int withdrawalCode, Model model){
        try {
            boolean result = withdrawalService.withdraw(withdrawalCode);
            if (result) {
                return "withdrawalSuccess";
            } else {
                return "error/withdrawalFail";
            }
        } catch (Exception e) {
            return "error/withdrawalFail";
        }
    }
}*/

///////////////////////////////////////////////////////////////
@Slf4j
@Controller
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
    @PostMapping("/sendMail")   //회원탈퇴 이메일 전송
    @ResponseBody
    public ResponseEntity<String> sendmail(@RequestBody MailRequestDto requestDto, Authentication authentication){
        try {
            boolean emailSent = withdrawalService.sendMail(requestDto, authentication);
            if (emailSent){
                return ResponseEntity.ok("메일 전송 성공");
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송실패");
            }
        }catch (InvalidUserException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송 중 오류 발생");
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(schema = @Schema(implementation = MailRequestDto.class))}),
            @ApiResponse(responseCode = "400", description = "Fail",
                    content = {@Content(schema = @Schema(implementation = ProfileController.ProfileFailResponse.class))})
    })
    @GetMapping("/withdraw")
    public ModelAndView withdraw(@RequestParam("withdrawalCode") int withdrawalCode){
        boolean result = withdrawalService.withdraw(withdrawalCode);

        /*if (result) {
            return ResponseEntity.ok("회원 탈퇴가 성공적으로 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 탈퇴 코드이거나 이미 탈퇴된 회원입니다.");
        }*/
        if (result) {
            log.info("회원 탈퇴가 성공적으로 완료되었습니다. 탈퇴 코드: {}", withdrawalCode);
            return new ModelAndView("redirect:/withdrawalSuccess");
        } else {
            log.warn("회원 탈퇴 실패. 유효하지 않은 탈퇴 코드이거나 이미 탈퇴된 회원입니다. 탈퇴 코드: {}", withdrawalCode);
            return new ModelAndView("redirect:/error/withdrawalFail");
        }

    }
}