package br.ifsp.demo.security.auth;

import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.exception.EntityAlreadyExistsException;
import br.ifsp.demo.security.config.JwtService;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import br.ifsp.demo.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterUserResponse register(RegisterUserRequest request, WalletService walletService) {

        userRepository.findByEmail(request.email()).ifPresent(unused -> {
            throw new EntityAlreadyExistsException("Email already registered: " + request.email());});

        String encryptedPassword = passwordEncoder.encode(request.password());

        final UUID id = UUID.randomUUID();
        final User user = User.builder()
                .id(id)
                .name(request.name())
                .lastname(request.lastname())
                .email(request.email())
                .password(encryptedPassword)
                .role(Role.USER)
                .build();

        userRepository.save(user);
        walletService.createWallet(id);
        return new RegisterUserResponse(id);
    }

    public AuthResponse authenticate(AuthRequest request) {
        final var authentication = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        authenticationManager.authenticate(authentication);

        final User user = userRepository.findByEmail(request.username()).orElseThrow();
        final String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
