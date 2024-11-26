package com.kupoprodajniugovori.korisnik.controller;

import com.kupoprodajniugovori.korisnik.Korisnik;
import com.kupoprodajniugovori.korisnik.dto.AuthenticationDTO;
import com.kupoprodajniugovori.korisnik.dto.RegistrationDTO;
import com.kupoprodajniugovori.korisnik.dto.TokenDTO;
import com.kupoprodajniugovori.korisnik.service.KorisnikService;
import com.kupoprodajniugovori.utils.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KorisnikService korisnikService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    public AuthController(KorisnikService korisnikService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.korisnikService = korisnikService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationDTO dto) {
        korisnikService.createKorisnik(dto);
        return ResponseEntity.ok("User registered successfully");
    }



    @GetMapping("/{username}")
    public ResponseEntity<Korisnik> getKorisnikByUsername(@PathVariable String username) {
        Korisnik korisnik = (Korisnik) korisnikService.loadUserByUsername(username);
        return ResponseEntity.ok(korisnik);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody AuthenticationDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );
        String token = jwtUtils.generateToken((UserDetails) auth.getPrincipal());
        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/register-admin")
   // @PreAuthorize("hasRole('ADMIN')") // samo postojeći admin može kreirati novog
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegistrationDTO dto) {
        korisnikService.createAdmin(dto);
        return ResponseEntity.ok("Admin registered successfully");
    }

}
