package chabssaltteog.balance_board.api.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Health check", description = "https health check")
@Slf4j
public class StatusCheckController {

    @Operation(summary = "Check health status", description = "Endpoint for performing a health check.")
    @GetMapping("/")
    public ResponseEntity<Void> checkHealthStatus() {
        try {
            // Health check logic here
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Health check failed", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}