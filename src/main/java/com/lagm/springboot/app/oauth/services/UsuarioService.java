package com.lagm.springboot.app.oauth.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lagm.springboot.app.commons.usuarios.models.entity.Usuario;
import com.lagm.springboot.app.oauth.clients.UsuarioFeignClient;

@Service
public class UsuarioService implements UserDetailsService {
	private Logger log = LoggerFactory.getLogger(UsuarioService.class);
	
	@Autowired
	UsuarioFeignClient client;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = client.findByUsername(username);
		if (usuario == null) {
			String mensajeError = "Error en el login, no existe el usuario \'" + username + "\'en el sistema";
			log.error(mensajeError);
			throw new UsernameNotFoundException(mensajeError);
		}
		
		/*
		 * GrantedAuthority: Es un objeto Rol en SpringSecurity
		 * Role: Es un objeto rol en la librería propia Usuario Commons
		 * */
		List<GrantedAuthority> authorities = 
				usuario.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getNombre()))
				.peek(authority -> log.info("Role: " + authority.getAuthority())) //Imprime el nombre del rol
				.collect(Collectors.toList()); // authorities son los roles
		
		log.info("Usuario autenticado: " + username);
		
		return new User(
				usuario.getUsername(), 
				usuario.getPassword(), 
				usuario.getEnabled(),
				true, 
				true, 
				true,
				authorities);
	}

}
