package solidexercicio10.model;

/**
 * Cria um passageiro em uma coordenada.
 *
 * <p>OCP: o catálogo de fábricas é montado no {@code Main}. Um tipo novo de
 * passageiro entra no jogo com uma classe nova e uma linha a mais no
 * catálogo, sem tocar no gerador da missão, no serviço ou no renderizador,
 * que era o que o {@code switch} de {@code criarPassageiroPolimorfico}
 * obrigava no código original.</p>
 */
@FunctionalInterface
public interface FabricaPassageiro {

    Passageiro criar(int x, int y);
}
