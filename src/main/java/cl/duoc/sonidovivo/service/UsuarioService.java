package cl.duoc.sonidovivo.service;

import cl.duoc.sonidovivo.dto.CambioRolRequest;
import cl.duoc.sonidovivo.dto.UsuarioActualizarRequest;
import cl.duoc.sonidovivo.dto.UsuarioCrearRequest;
import cl.duoc.sonidovivo.dto.UsuarioResponse;
import cl.duoc.sonidovivo.exception.ConflictoException;
import cl.duoc.sonidovivo.exception.RecursoNoEncontradoException;
import cl.duoc.sonidovivo.model.Rol;
import cl.duoc.sonidovivo.model.Usuario;
import cl.duoc.sonidovivo.repository.RolRepository;
import cl.duoc.sonidovivo.repository.UsuarioRepository;
import cl.duoc.sonidovivo.security.UsuarioAutenticado;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * reglas de usuarios:
 *  solo el administrador lista, crea, cambia roles y desactiva.
 *  cada usuario puede ver y modificar SOLO su propia cuenta (cambiar el id en la URL no sirve: 403).
 *  eliminar = desactivar (activo=false), porque sus pedidos deben conservarse.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id, UsuarioAutenticado yo) {
        verificarPropietarioOAdmin(id, yo);
        return UsuarioResponse.desde(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioCrearRequest req) {
        String correo = AuthService.normalizarCorreo(req.correo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictoException("Ya existe una cuenta con ese correo");
        }
        Usuario u = new Usuario();
        u.setNombre(req.nombre().trim());
        u.setCorreo(correo);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setFechaNacimiento(req.fechaNacimiento());
        u.setRol(buscarRol(req.rol()));
        return UsuarioResponse.desde(usuarioRepository.save(u));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioActualizarRequest req, UsuarioAutenticado yo) {
        verificarPropietarioOAdmin(id, yo);
        Usuario u = buscar(id);

        if (req.nombre() != null) {
            u.setNombre(req.nombre().trim());
        }
        if (req.correo() != null) {
            String nuevoCorreo = AuthService.normalizarCorreo(req.correo());
            if (!nuevoCorreo.equalsIgnoreCase(u.getCorreo()) && usuarioRepository.existsByCorreoIgnoreCase(nuevoCorreo)) {
                throw new ConflictoException("Ya existe una cuenta con ese correo");
            }
            u.setCorreo(nuevoCorreo);
        }
        if (req.password() != null) {
            u.setPasswordHash(passwordEncoder.encode(req.password()));
        }
        if (req.fechaNacimiento() != null) {
            u.setFechaNacimiento(req.fechaNacimiento());
        }
        if (req.direccion() != null) {
            u.setDireccion(req.direccion().trim());
        }
        if (req.foto() != null) {
            u.setFoto(req.foto());
        }
        return UsuarioResponse.desde(u);
    }

    @Transactional
    public UsuarioResponse cambiarRol(Long id, CambioRolRequest req, UsuarioAutenticado yo) {
        if (yo.id().equals(id)) {
            throw new ConflictoException("No puedes cambiar tu propio rol");
        }
        Usuario u = buscar(id);
        u.setRol(buscarRol(req.rol()));
        return UsuarioResponse.desde(u);
    }

    @Transactional
    public void desactivar(Long id, UsuarioAutenticado yo) {
        if (yo.id().equals(id)) {
            throw new ConflictoException("No puedes desactivar tu propia cuenta");
        }
        Usuario u = buscar(id);
        u.setActivo(false);
    }

    @Transactional
    public UsuarioResponse reactivar(Long id) {
        Usuario u = buscar(id);
        u.setActivo(true);
        return UsuarioResponse.desde(u);
    }

    private void verificarPropietarioOAdmin(Long id, UsuarioAutenticado yo) {
        if (!yo.esAdministrador() && !yo.id().equals(id)) {
            throw new AccessDeniedException("Solo puedes acceder a tu propia cuenta");
        }
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario " + id + " no encontrado"));
    }

    private Rol buscarRol(cl.duoc.sonidovivo.model.NombreRol nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException("El rol " + nombre + " no existe en la base de datos"));
    }
}
