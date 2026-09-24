package solidexercicio10.presentation;

/**
 * O que o serviço do menu precisa perguntar ao piloto.
 *
 * <p>ISP: duas operações, ambas usadas por {@code JogoService}. Quem só
 * navega no menu não fica dependendo dos comandos de pilotagem.</p>
 */
public interface EntradaJogo {

    String lerOpcaoMenu();

    boolean confirmarResetDoRanking();
}
