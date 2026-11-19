package cl.duoc.sonidovivo.dto;

import cl.duoc.sonidovivo.model.NombreRol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** alta de usuario hecha por el administrador (puede elegir el rol). */
public record UsuarioCrearRequest(
        @Schema(example = "Pedro Soto")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100)
        String nombre,

        @Schema(example = "pedro.soto@sonidovivo.cl")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120)
        String correo,

        @Schema(example = "ClaveSegura123")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password,

        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate fechaNacimiento,

        @Schema(example = "VENDEDOR")
        @NotNull(message = "El rol es obligatorio")
        NombreRol rol
) { }
