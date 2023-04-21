package com.grupowl.unidac.desafio.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Colaborador implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 100)
	private String nome;

	@Column(nullable = false, unique = true, length = 15)
	private String cpf;

	@OneToMany(mappedBy = "colaborador", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Opcao> opcoes;

	@Column(nullable = false, name = "data_cafe")
	@Temporal(TemporalType.DATE)
	private LocalDate data;

	@Column(nullable = false)
	private Boolean trouxe;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public List<Opcao> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(List<Opcao> opcoes) {
		this.opcoes = opcoes;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public Boolean getTrouxe() {
		return trouxe;
	}

	public void setTrouxe(Boolean trouxe) {
		this.trouxe = trouxe;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cpf, data, id, nome, opcoes, trouxe);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Colaborador other = (Colaborador) obj;
		return Objects.equals(cpf, other.cpf) && Objects.equals(data, other.data) && Objects.equals(id, other.id)
				&& Objects.equals(nome, other.nome) && Objects.equals(opcoes, other.opcoes)
				&& Objects.equals(trouxe, other.trouxe);
	}

	@Override
	public String toString() {
		return "Colaborador [id=" + id + ", nome=" + nome + ", cpf=" + cpf + ", opcoes=" + opcoes + ", data=" + data
				+ ", trouxe=" + trouxe + "]";
	}
}
