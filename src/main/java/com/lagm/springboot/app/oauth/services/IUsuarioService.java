package com.lagm.springboot.app.oauth.services;

import com.lagm.springboot.app.commons.usuarios.models.entity.Usuario;

public interface IUsuarioService {
	public Usuario findByUsername(String username);
}
