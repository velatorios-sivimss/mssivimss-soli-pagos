package com.imss.sivimss.solipagos.util;

import com.imss.sivimss.solipagos.model.request.UsuarioDto;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class UsuarioDtoTest {
    @Test
    public void usuarioDtoTest() throws Exception {
        UsuarioDto request=new UsuarioDto();
        assertNull(request.getIdUsuario());
        assertNull(request.getDesRol());
        assertNull(request.getNombre());
        assertNull(request.getNombre());
    }
}
