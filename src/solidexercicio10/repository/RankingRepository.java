package solidexercicio10.repository;

import java.util.List;

/**
 * Contrato de persistência do ranking.
 *
 * <p>DIP: o serviço do jogo conhece apenas esta interface. Trocar o arquivo
 * JSON por banco, API ou memória não toca uma linha de
 * {@code JogoService}.</p>
 *
 * <p>ISP: três operações, todas usadas por quem depende da interface.
 * Gravar, listar e limpar são exatamente o que o menu do jogo oferece.</p>
 */
public interface RankingRepository {

    void salvar(RankingEntry entrada);

    /**
     * Melhores pontuações em ordem decrescente.
     *
     * @param limite quantidade máxima de linhas devolvidas
     */
    List<RankingEntry> listarTop(int limite);

    void limpar();
}
