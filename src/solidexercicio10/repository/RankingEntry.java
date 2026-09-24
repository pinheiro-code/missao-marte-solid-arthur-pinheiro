package solidexercicio10.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import solidexercicio10.model.Dificuldade;

/**
 * Uma linha do ranking.
 *
 * <p>Objeto de valor imutável. É o que atravessa o contrato
 * {@link RankingRepository}, no lugar dos cinco parâmetros posicionais que o
 * tutorial propõe em {@code salvar(...)}: com um único argumento não há como
 * trocar a ordem de dois inteiros sem o compilador perceber.</p>
 */
public record RankingEntry(String nome,
                           int pontuacao,
                           Dificuldade dificuldade,
                           int passageirosColetados,
                           String dataHora,
                           long tempoJogoSegundos) {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Cria a entrada carimbando a data e hora do encerramento da partida. */
    public static RankingEntry agora(String nome, int pontuacao, Dificuldade dificuldade,
                                     int passageirosColetados, long tempoJogoSegundos) {
        return new RankingEntry(nome, pontuacao, dificuldade, passageirosColetados,
                LocalDateTime.now().format(FORMATO), tempoJogoSegundos);
    }
}
