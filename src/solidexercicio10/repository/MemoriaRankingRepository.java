package solidexercicio10.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ranking guardado apenas em memória.
 *
 * <p>Existe para provar o DIP na prática: é a segunda implementação do mesmo
 * contrato e permite executar o jogo (e os testes) sem escrever em disco.
 * Nenhuma linha de {@code JogoService} muda ao escolher uma ou outra.</p>
 */
public class MemoriaRankingRepository implements RankingRepository {

    private final List<RankingEntry> entradas = new ArrayList<>();

    @Override
    public void salvar(RankingEntry entrada) {
        entradas.add(entrada);
    }

    @Override
    public List<RankingEntry> listarTop(int limite) {
        return entradas.stream()
                .sorted(Comparator.comparingInt(RankingEntry::pontuacao).reversed())
                .limit(limite)
                .toList();
    }

    @Override
    public void limpar() {
        entradas.clear();
    }
}
