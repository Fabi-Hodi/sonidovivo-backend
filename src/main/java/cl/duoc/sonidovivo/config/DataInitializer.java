package cl.duoc.sonidovivo.config;

import cl.duoc.sonidovivo.model.NombreRol;
import cl.duoc.sonidovivo.model.Rol;
import cl.duoc.sonidovivo.model.Usuario;
import cl.duoc.sonidovivo.repository.RolRepository;
import cl.duoc.sonidovivo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * carga datos iniciales SOLO si faltan (no duplica nada al reiniciar):
 * los tres roles del caso.
 *  cuentas de demostracion (admin, vendedor, cliente) si se definio DEMO_PASSWORD.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String demoPassword;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.seed.demo-password:}") String demoPassword) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.demoPassword = demoPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        crearRolSiFalta(NombreRol.ADMINISTRADOR, "Gestiona usuarios, roles, catálogo y pedidos");
        crearRolSiFalta(NombreRol.VENDEDOR, "Actualiza stock y estados de pedidos");
        crearRolSiFalta(NombreRol.CLIENTE, "Compra productos y ve solo sus pedidos");

        if (demoPassword == null || demoPassword.isBlank()) {
            log.warn("DEMO_PASSWORD no está definido: no se crean cuentas de demostración.");
            return;
        }
        crearUsuarioSiFalta("Fabian", "fabian@sonidovivo.cl", NombreRol.ADMINISTRADOR, "/img/usuarios/Fabian.png");
        crearUsuarioSiFalta("Jonathan", "jonathan@sonidovivo.cl", NombreRol.ADMINISTRADOR, "/img/usuarios/Jona.png");
        crearUsuarioSiFalta("Vendedor Demo", "vendedor@sonidovivo.cl", NombreRol.VENDEDOR, null);
        crearUsuarioSiFalta("Cliente Demo", "cliente@sonidovivo.cl", NombreRol.CLIENTE, null);
    }

    private void crearRolSiFalta(NombreRol nombre, String descripcion) {
        if (rolRepository.findByNombre(nombre).isEmpty()) {
            rolRepository.save(new Rol(nombre, descripcion));
        }
    }

    private void crearUsuarioSiFalta(String nombre, String correo, NombreRol nombreRol, String foto) {
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            return;
        }
        Rol rol = rolRepository.findByNombre(nombreRol).orElseThrow();
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setPasswordHash(passwordEncoder.encode(demoPassword));
        u.setRol(rol);
        u.setFoto(foto);
        usuarioRepository.save(u);
        log.info("Usuario de demostración creado: {} ({})", correo, nombreRol);
    }
}
