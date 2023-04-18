package com.grupowl.unidac.desafio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.grupowl.unidac.desafio.model.Colaborador;
import com.grupowl.unidac.desafio.repository.ColaboradorRepository;
import com.grupowl.unidac.desafio.util.MensagemDeErro;
import com.grupowl.unidac.desafio.validator.ColaboradorValidator;

@RestController
public class ColaboradorController {

	@Autowired
	private ColaboradorRepository repository;

	@Autowired
	private ColaboradorValidator validator;

	@GetMapping("/colaboradores")
	public ResponseEntity<?> lerTodos() {
		return ResponseEntity.ok().body(repository.readAll());
	}

	@GetMapping("/colaboradores/{id}")
	public ResponseEntity<?> ler(@PathVariable Integer id) {
		return ResponseEntity.ok().body(repository.read(id));
	}

	@PostMapping("/colaboradores")
	public ResponseEntity<?> salvar(@RequestBody Colaborador colaborador) {
		try {
			validator.isCreateValid(colaborador);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(repository.create(colaborador));
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new MensagemDeErro(e.getReason()));
		}
	}

	@PutMapping("/colaboradores/{id}")
	public ResponseEntity<?> atualizar(@PathVariable Integer id, @RequestBody Colaborador colaborador) {
		try {
			validator.isUpdateValid(id, colaborador);
			repository.update(id, colaborador);
			return ResponseEntity.ok().body("");
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new MensagemDeErro(e.getReason()));
		}
	}

	@DeleteMapping("/colaboradores/{id}")
	public ResponseEntity<?> deletar(@PathVariable Integer id) {
		repository.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		
	}
}
