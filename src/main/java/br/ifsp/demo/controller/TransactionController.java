package br.ifsp.demo.controller;

import br.ifsp.demo.security.auth.AuthenticationInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/hello")
@AllArgsConstructor
@Tag(name = "Hello API")
public class TransactionController {

    private final AuthenticationInfoService authService;

    @GetMapping
    public ResponseEntity<String> hello() {
        final UUID userId = authService.getAuthenticatedUserId();
        return ResponseEntity.ok("Hello: " + userId.toString());
    }
}
