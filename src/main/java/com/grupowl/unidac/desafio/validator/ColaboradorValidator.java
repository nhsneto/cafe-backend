package com.grupowl.unidac.desafio.validator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.grupowl.unidac.desafio.model.Colaborador;
import com.grupowl.unidac.desafio.model.Opcao;
import com.grupowl.unidac.desafio.repository.ColaboradorRepository;

@Component
public class ColaboradorValidator {

	@Autowired
	private ColaboradorRepository repository;

	public void isCreateValid(Colaborador colaborador) throws ResponseStatusException {
		isCPFValido(colaborador.getCpf());
		isDataNoFuturo(colaborador.getData());
		isColaboradorUnico(colaborador.getNome());
		isOpcoesVazia(colaborador.getOpcoes());
		isOpcoesUnicas(colaborador.getOpcoes(), colaborador.getData());
	}

	public void isUpdateValid(Integer id, Colaborador atualizado) throws ResponseStatusException {
		Colaborador antigo = repository.read(id);

		if (!antigo.getCpf().equals(atualizado.getCpf())) {
			isCPFValido(atualizado.getCpf());
		}

		if (!antigo.getData().equals(atualizado.getData())) {
			isDataNoFuturo(atualizado.getData());
		}

		if (!antigo.getNome().equals(atualizado.getNome())) {
			isColaboradorUnico(atualizado.getNome());
		}

		isOpcoesVazia(atualizado.getOpcoes());

		List<String> opcoesAntigo = repository.getOpcoesByColaboradorId(id);
		for (Opcao opcao : atualizado.getOpcoes()) {
			String nome = opcao.getNome();

			if (!opcoesAntigo.contains(nome)) {
				isOpcaoUnica(nome, atualizado.getData());
			}
		}
	}

	private void isCPFValido(String cpf) throws ResponseStatusException {
		boolean temOnzeNumeros = Pattern.compile("[0-9]{11}").matcher(cpf).matches();
		if (!temOnzeNumeros) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF não possui onze (11) dígitos.");
		}

		List<Integer> numbers = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			numbers.add(i, Integer.parseInt(String.valueOf(cpf.charAt(i))));
		}

		int primeiroDigitoVerificador = getDigitoVerificador(numbers);
		numbers.add(primeiroDigitoVerificador);
		int segundoDigitoVerificador = getDigitoVerificador(numbers);

		String cpfDigitosVerificadores = cpf.substring(cpf.length() - 2, cpf.length());
		String digitosVerificadoresCorretos = Integer.toString(primeiroDigitoVerificador)
				+ Integer.toString(segundoDigitoVerificador);

		if (!cpfDigitosVerificadores.equals(digitosVerificadoresCorretos)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido.");
		}

		if (!repository.getColaboradorByCPF(cpf).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O CPF já existe no sistema.");
		}
	}

	private Integer getDigitoVerificador(List<Integer> digitos) {
		int soma = 0;
		int multiplicador = 2;
		for (int i = digitos.size() - 1; i >= 0; i--) {
			soma += digitos.get(i) * multiplicador;
			multiplicador++;
		}

		int restoDivisao = soma % 11;
		if (restoDivisao >= 2) {
			return 11 - restoDivisao;
		}

		return 0;
	}

	private void isColaboradorUnico(String nome) {
		if (!Pattern.compile("^[a-zA-Z\s]{2,}$").matcher(nome).matches()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"O nome informado é inválido. Utilize apenas letras.");
		}

		if (!repository.getColaboradorByNome(nome).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"O Colaborador '" + nome + "' já existe no sistema.");
		}
	}

	private void isOpcoesVazia(List<Opcao> opcoes) {
		if (opcoes.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"O Colaborador precisa levar pelo menos um item para o café.");
		}
	}

	private void isOpcoesUnicas(List<Opcao> opcoes, LocalDate data) {
		List<String> opcoesExistentes = repository.getOpcoesByData(data);
		for (Opcao opcao : opcoes) {
			if (opcoesExistentes.contains(opcao.getNome())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A opção '" + opcao.getNome()
						+ "' já foi escolhida por outro colaborador para o café da manhã.");
			}
		}
	}

	private void isOpcaoUnica(String nome, LocalDate data) {
		List<String> opcoesExistentes = repository.getOpcoesByData(data);
		if (opcoesExistentes.contains(nome)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"A opção '" + nome + "' já foi escolhida por outro colaborador para o café da manhã.");
		}
	}

	private void isDataNoFuturo(LocalDate data) throws ResponseStatusException {
		LocalDate hoje = new Date().toInstant().atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();
		if (data.compareTo(hoje) <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"A data informada é inválida. A data deve ser maior do que a atual.");
		}
	}
}
