/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajadores.service;

import com.example.trabajadores.model.Empleado;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.trabajadores.repository.EmpleadoRepository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author sise
 */
@Service
public class EmpleadoService {
    
    @Autowired
    private EmpleadoRepository repository;
    
    
    /**
     * Funcion para listar la tabla empleado
     * @return
     */
    public List<Empleado> listarTodas(){
        return repository.findAll();
        
    }
    /**Funcion para Guardar datos del inventario
      *@param empleado
      */
     public void guardar(Empleado empleado){
        repository.save(empleado);
        
        
    }
     
      /***
     *FUNCION PARA  BUSCAR REGISTRO EMPLEADO POR ID
     *@param id
     *@return
     */
     public Optional<Empleado> buscarPorId(Long id){
        return repository.findById(id);
    }
       public void eliminar(Long id) {
        repository.deleteById(id);
    }

     
}
