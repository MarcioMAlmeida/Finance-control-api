package br.com.marcio.financecontrol.repository;

import br.com.marcio.financecontrol.model.Lancamento;
import br.com.marcio.financecontrol.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    List<Lancamento> findByUsuarioAndDataBetween(Usuario usuario, LocalDate start, LocalDate end);
}