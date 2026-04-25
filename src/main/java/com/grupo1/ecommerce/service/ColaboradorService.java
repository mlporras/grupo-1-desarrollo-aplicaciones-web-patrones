package com.grupo1.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo1.ecommerce.domain.Colaborador;
import com.grupo1.ecommerce.domain.Tienda;
import com.grupo1.ecommerce.domain.Usuario;
import com.grupo1.ecommerce.repository.ColaboradorRepository;
import com.grupo1.ecommerce.repository.UsuarioRepository;

@Service
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final UsuarioRepository usuarioRepository;

    public ColaboradorService(ColaboradorRepository colaboradorRepository,
                              UsuarioRepository usuarioRepository) {
        this.colaboradorRepository = colaboradorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Colaborador> getColaboradores(Tienda tienda) {
        return colaboradorRepository.findByTienda(tienda);
    }

    @Transactional(readOnly = true)
    public Optional<Colaborador> getColaborador(Integer id) {
        return colaboradorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Colaborador> getColaboradorPorUsuarioYTienda(Usuario usuario, Tienda tienda) {
        return colaboradorRepository.findByUsuarioAndTienda(usuario, tienda);
    }

    @Transactional(readOnly = true)
    public Optional<Colaborador> getColaboradorActivoPorUsuario(Usuario usuario) {
        return colaboradorRepository.findByUsuarioAndActivoTrue(usuario);
    }

    @Transactional
    public Colaborador crearColaborador(String nombre, String correo, String contrasena,
                                        String rolColaborador, Tienda tienda) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuario.setRol("COLABORADOR");
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        Colaborador colaborador = new Colaborador();
        colaborador.setUsuario(usuario);
        colaborador.setTienda(tienda);
        colaborador.setRolColaborador(rolColaborador);
        colaborador.setActivo(true);

        return colaboradorRepository.save(colaborador);
    }

    @Transactional
    public Colaborador actualizarRol(Integer idColaborador, String nuevoRol) {
        Colaborador colaborador = colaboradorRepository.findById(idColaborador)
                .orElseThrow(() -> new IllegalArgumentException("Colaborador no encontrado."));
        colaborador.setRolColaborador(nuevoRol);
        return colaboradorRepository.save(colaborador);
    }

    @Transactional
    public void eliminar(Integer idColaborador) {
        Colaborador colaborador = colaboradorRepository.findById(idColaborador)
                .orElseThrow(() -> new IllegalArgumentException("Colaborador no encontrado."));
        colaborador.setActivo(false);
        colaborador.getUsuario().setActivo(false);
        usuarioRepository.save(colaborador.getUsuario());
        colaboradorRepository.save(colaborador);
    }
}
