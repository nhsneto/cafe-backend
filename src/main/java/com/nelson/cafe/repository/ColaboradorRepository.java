package com.nelson.cafe.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nelson.cafe.model.Colaborador;
import com.nelson.cafe.model.Opcao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
@Transactional
public class ColaboradorRepository {

	@PersistenceContext
	private EntityManager em;

	public List<Colaborador> readAll() {
		Query query = em.createNativeQuery("SELECT * FROM colaborador", Colaborador.class);
		return (List<Colaborador>) query.getResultList();
	}
	
	public Colaborador create(Colaborador colaborador) {
		Colaborador persistedColaborador = persistColaborador(colaborador);

		for (Opcao opcao : colaborador.getOpcoes()) {
			em.createNativeQuery("INSERT INTO opcao VALUES (NULL, ?, ?, ?)")
				.setParameter(1, persistedColaborador.getData())
				.setParameter(2, opcao.getNome())
				.setParameter(3, persistedColaborador.getId())
				.executeUpdate();
		}

 		return (Colaborador) em.createNativeQuery(
			"SELECT * FROM colaborador WHERE id = ?", Colaborador.class)
 				.setParameter(1, persistedColaborador.getId())
 				.getSingleResult();
	}

	private Colaborador persistColaborador(Colaborador colaborador) {
		em.createNativeQuery("INSERT INTO colaborador VALUES (NULL, ?, ?, ?, ?)")
		.setParameter(1, colaborador.getCpf())
		.setParameter(2, colaborador.getData())
		.setParameter(3, colaborador.getNome())
		.setParameter(4, Boolean.FALSE)
		.executeUpdate();

		return (Colaborador) em.createNativeQuery(
				"SELECT * FROM colaborador WHERE cpf = ?", Colaborador.class)
					.setParameter(1, colaborador.getCpf()).getSingleResult();
	}

	public Colaborador read(Integer id) {
		return (Colaborador) em.createNativeQuery(
				"SELECT * FROM colaborador WHERE id = ?", Colaborador.class)
					.setParameter(1, id)
					.getSingleResult();
	}

	public void update(Integer id, Colaborador colaborador) {
		updateFields(id, colaborador);
		updateOptions(id, colaborador);
	}

	private void updateFields(Integer id, Colaborador atualizado) {
		Colaborador antigo = read(id);

		if (!antigo.getCpf().equalsIgnoreCase(atualizado.getCpf())) {
			em.createNativeQuery("UPDATE colaborador SET cpf = ? WHERE id = ?")
				.setParameter(1, atualizado.getCpf())
				.setParameter(2, id)
				.executeUpdate();
		}

		if (!antigo.getData().equals(atualizado.getData())) {
			em.createNativeQuery("UPDATE colaborador SET data_cafe = ? WHERE id = ?")
				.setParameter(1, atualizado.getData())
				.setParameter(2, id)
				.executeUpdate();
		}

		if (!antigo.getNome().equalsIgnoreCase(atualizado.getNome())) {
			em.createNativeQuery("UPDATE colaborador SET nome = ? WHERE id = ?")
				.setParameter(1, atualizado.getNome())
				.setParameter(2, id)
				.executeUpdate();
		}

		if (!antigo.getTrouxe().equals(atualizado.getTrouxe())) {
			em.createNativeQuery("UPDATE colaborador SET trouxe = ? WHERE id = ?")
			.setParameter(1, atualizado.getTrouxe())
			.setParameter(2, id)
			.executeUpdate();
		}
	}

	private void updateOptions(Integer id, Colaborador atualizado) {
		Colaborador antigo = read(id);

		List<String> antigoOpcaoNomeList = new ArrayList<>();
		for (Opcao opcao : antigo.getOpcoes()) {
			antigoOpcaoNomeList.add(opcao.getNome());
		}

		List<String> atualizadoOpcaoNomeList = new ArrayList<>();
		for (Opcao opcao : atualizado.getOpcoes()) {
			atualizadoOpcaoNomeList.add(opcao.getNome());
		}

		for (String opcaoNome : atualizadoOpcaoNomeList) {
			if (!antigoOpcaoNomeList.contains(opcaoNome)) {
				em.createNativeQuery("INSERT INTO opcao VALUES (NULL, ?, ?, ?)")
					.setParameter(1, antigo.getData())
					.setParameter(2, opcaoNome)
					.setParameter(3, id)
					.executeUpdate();
			}
		}

		for (String opcaoNome : antigoOpcaoNomeList) {
			if (!atualizadoOpcaoNomeList.contains(opcaoNome)) {
				em.createNativeQuery("DELETE FROM opcao WHERE nome = ? AND data_cafe = ?")
					.setParameter(1, opcaoNome)
					.setParameter(2, antigo.getData())
					.executeUpdate();
			}
		}
	}

	public void delete(Integer id) {
		em.createNativeQuery("DELETE FROM opcao WHERE id = ?")
			.setParameter(1, id)
			.executeUpdate();

		em.createNativeQuery("DELETE FROM colaborador WHERE id = ?")
			.setParameter(1, id)
			.executeUpdate();
	}

	public List<Colaborador> getColaboradorByCPF(String cpf) {
		 return (List<Colaborador>) em.createNativeQuery(
				"SELECT * FROM colaborador WHERE cpf = ?", Colaborador.class)
					.setParameter(1, cpf)
					.getResultList();
	}

	public List<Colaborador> getColaboradorByNome(String nome) {
		return (List<Colaborador>) em.createNativeQuery(
				"SELECT * FROM colaborador WHERE UPPER(nome) = UPPER(?)", Colaborador.class)
					.setParameter(1, nome)
					.getResultList();
	}
	
	public List<String> getOpcoesByData(LocalDate data) {
		return (List<String>) em.createNativeQuery(
				"SELECT UPPER(nome) FROM opcao WHERE data_cafe = ?", String.class)
					.setParameter(1, data)
					.getResultList();
	}

	public List<String> getOpcoesByColaboradorId(Integer colaboradorId) {
		return (List<String>) em.createNativeQuery(
				"SELECT nome FROM opcao WHERE colaborador_id = ?", String.class)
					.setParameter(1, colaboradorId)
					.getResultList();
	}
}
