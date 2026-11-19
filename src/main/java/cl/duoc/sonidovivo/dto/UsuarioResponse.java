package cl.duoc.sonidovivo.dto;

import cl.duoc.sonidovivo.model.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** lo que la API devuelve de un usuario. nunca incluye la contraseña ni su hash. */
public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String rol,
        LocalDate fechaNacimiento,
        String direccion,
        String foto,
        boolean activo,
        LocalDateTime fechaCreacion
) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getCorreo(),
                u.getRol().getNombre().name(),
                u.getFechaNacimiento(),
                u.getDireccion(),
                u.getFoto(),
                u.isActivo(),
                u.getFechaCreacion()
        );
    }
}
