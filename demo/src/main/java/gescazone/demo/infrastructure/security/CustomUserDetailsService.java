package gescazone.demo.infrastructure.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.domain.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String numeroDocumento) throws UsernameNotFoundException {
        UsuarioModel usuario = usuarioRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + numeroDocumento));

        String roleName = "ROLE_" + usuario.getRol().getNombreRol().toUpperCase();
        
        return User.builder()
                .username(usuario.getNumeroDocumento())
                .password(usuario.getContrasena())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(roleName)))
                .build();
    }
}