package solidexercicio10.presentation;

import solidexercicio10.model.Dificuldade;

/**
 * O que o serviço da partida precisa perguntar ao piloto.
 *
 * <p>ISP: cinco operações, todas usadas por {@code PartidaService}.</p>
 */
public interface EntradaPartida {

    String lerNomePiloto();

    Dificuldade lerDificuldade();

    int lerTamanhoMapa();

    void aguardarDecolagem();

    /** Tecla digitada: w, s, a, d, c ou q. */
    char lerComando();
}
