package com.grupo1.ecommerce.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.repository.TiendaRepository;
import com.grupo1.ecommerce.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TiendaRepository tiendaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, TiendaRepository tiendaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tiendaRepository = tiendaRepository;
    }

 
    

    @Transactional(readOnly = true)
    public Optional<Usuario> login(String correo, String contrasena) {
        return usuarioRepository.findByCorreoAndContrasena(correo, contrasena);
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Transactional
    public Usuario registrarEmprendedor(String nombre, String correo, String contrasena, String nombreTienda) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuario.setRol("ADMIN"); // 
        
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        Tienda tienda = new Tienda();
        tienda.setUsuario(usuario);
        tienda.setNombreComercial(nombreTienda);
        tienda.setActivo(true);
        tiendaRepository.save(tienda);

        return usuario;
    }

 
    @Transactional
    public Usuario registrarCliente(Usuario usuario) {
        usuario.setRol("CLIENTE"); 
        usuario.setActivo(true);
     
        return usuarioRepository.save(usuario);
    }

 
  
    @Transactional
    public void actualizarPerfil(Usuario usuario) {
        Usuario existente = usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
        if (existente != null) {
            existente.setNombre(usuario.getNombre());
            existente.setCorreo(usuario.getCorreo());
            // Solo actualiza la contraseña si se envía una nueva
            if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
                existente.setContrasena(usuario.getContrasena());
            }
            usuarioRepository.save(existente);
        }
    }

 

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public Optional<Tienda> getTiendaPorUsuario(Usuario usuario) {
        return tiendaRepository.findByUsuario(usuario);
    }
}
