package com.grupowl.unidac.desafio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.grupowl.unidac.desafio.model.Colaborador;
import com.grupowl.unidac.desafio.repository.ColaboradorRepository;

@RestController
public class ColaboradorController {

	@Autowired
	ColaboradorRepository colaboradorRepo;

	@GetMapping("/colaboradores")
	public List<Colaborador> lerTodos() {
		return colaboradorRepo.readAll();
	}

	@GetMapping("/colaboradores/{id}")
	public Colaborador ler(@PathVariable Integer id) {
		return colaboradorRepo.read(id);
	}

	@PostMapping("/colaboradores")
	public Colaborador salvar(@RequestBody Colaborador colaborador) {
		return colaboradorRepo.create(colaborador);
	}

	@PutMapping("/colaboradores/{id}")
	public void atualizar(@PathVariable Integer id, @RequestBody Colaborador colaborador) {
		colaboradorRepo.update(id, colaborador);
	}

	@DeleteMapping("/colaboradores/{id}")
	public void deletar(@PathVariable Integer id) {
		colaboradorRepo.delete(id);
	}
}
