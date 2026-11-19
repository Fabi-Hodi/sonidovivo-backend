package cl.duoc.sonidovivo.dto;

/** respuesta del login. token de acceso, tipo, vigencia en segundos y datos publicos del usuario. */
public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UsuarioResponse usuario
) { }
