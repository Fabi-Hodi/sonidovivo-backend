package cl.duoc.sonidovivo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** datos del formulario "crear cuenta". el rol NO se recibe, lo asigna el servidor (CLIENTE). */
public record RegistroRequest(
        @Schema(example = "Ana Pérez")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(example = "ana.perez@correo.cl")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120)
        String correo,

        @Schema(example = "ClaveSegura123")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password,

        @Schema(example = "2000-05-20")
        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate fechaNacimiento,

        @Schema(example = "Av. Siempre Viva 742, Santiago")
        @Size(max = 200)
        String direccion
) { }
