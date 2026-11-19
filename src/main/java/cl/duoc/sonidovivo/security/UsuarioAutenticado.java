package cl.duoc.sonidovivo.security;

import cl.duoc.sonidovivo.model.NombreRol;

/**
 * identidad validada a partir del JWT. ss lo que reciben los controladores con @AuthenticationPrincipal.
 * el id sale del token firmado, nunca de un dato que envie el cliente.
 */
public record UsuarioAutenticado(Long id, String correo, NombreRol rol) {

    public boolean esAdministrador() {
        return rol == NombreRol.ADMINISTRADOR;
    }
}
