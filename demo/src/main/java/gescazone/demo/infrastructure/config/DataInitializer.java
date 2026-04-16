package gescazone.demo.infrastructure.config;

import gescazone.demo.domain.model.RolModel;
import gescazone.demo.domain.model.TipoDocumentoModel;
import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Solo crea el admin si no existe
        if (!usuarioRepository.existsByNumeroDocumento("123456780")) {

            RolModel rol = new RolModel();
            rol.setNombreRol("ADMINISTRADOR");

            TipoDocumentoModel tipoDoc = new TipoDocumentoModel();
            tipoDoc.setNombreTipoDocumento("Cédula");

            UsuarioModel admin = new UsuarioModel();
            admin.setNumeroDocumento("123456780");
            admin.setNombre("Admin");
            admin.setApellido("Sistema");
            admin.setCorreo("admin@gescazone.com");
            admin.setContrasena(passwordEncoder.encode("admin123"));
            admin.setRol(rol);
            admin.setTipoDocumento(tipoDoc);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: documento=123456780, contraseña=admin123");
        } else {
            System.out.println("ℹ️ Usuario administrador ya existe");
        }
    }
}