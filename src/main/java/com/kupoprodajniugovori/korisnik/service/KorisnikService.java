package com.kupoprodajniugovori.korisnik.service;

import com.kupoprodajniugovori.korisnik.Korisnik;
import com.kupoprodajniugovori.korisnik.KorisnikRepository;
import com.kupoprodajniugovori.korisnik.Role;
import com.kupoprodajniugovori.korisnik.dto.RegistrationDTO;
import jakarta.validation.ValidationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class KorisnikService implements UserDetailsService {

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;

    public KorisnikService(KorisnikRepository korisnikRepository, PasswordEncoder passwordEncoder) {
        this.korisnikRepository = korisnikRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return korisnikRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen: " + username));
    }

    @Transactional
    public void createKorisnik(RegistrationDTO dto) {
        if (korisnikRepository.existsByUsername(dto.username())) {
            throw new ValidationException("Korisnik već postoji");
        }

        Korisnik korisnik = new Korisnik();
        korisnik.setUsername(dto.username());
        korisnik.setPassword(passwordEncoder.encode(dto.password()));
        korisnik.setRoles(Set.of(Role.USER));

        korisnikRepository.save(korisnik);
    }

    @Transactional
    public void createAdmin(RegistrationDTO dto) {
        if (korisnikRepository.existsByUsername(dto.username())) {
            throw new ValidationException("Korisnik već postoji");
        }

        Korisnik korisnik = new Korisnik();
        korisnik.setUsername(dto.username());
        korisnik.setPassword(passwordEncoder.encode(dto.password()));
        korisnik.setRoles(Set.of(Role.ADMIN)); // postavljamo ADMIN rolu

        korisnikRepository.save(korisnik);
    }

    // Metoda za inicijalni admin račun ako je potrebno
    @Transactional
    public void createInitialAdmin(String username, String password) {
        if (!korisnikRepository.existsByUsername(username)) {
            Korisnik admin = new Korisnik();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRoles(Set.of(Role.ADMIN));
            korisnikRepository.save(admin);
        }
    }

}
