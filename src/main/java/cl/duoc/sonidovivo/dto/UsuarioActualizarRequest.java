package cl.duoc.sonidovivo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * modificacion de datos propios (o de cualquiera si es Administrador).
 * los campos en null no se modifican. El rol y el estado NO se cambian aqui.
 */
public record UsuarioActualizarRequest(
        @Schema(example = "Ana María Pérez")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(example = "ana.perez@correo.cl")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120)
        String correo,

        @Schema(example = "OtraClave456")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password,

        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate fechaNacimiento,

        @Size(max = 255)
        String foto,

        @Schema(example = "Av. Siempre Viva 742, Santiago")
        @Size(max = 200)
        String direccion
) { }
