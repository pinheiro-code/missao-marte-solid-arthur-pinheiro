package solidexercicio10.model;

/**
 * Passageiro a ser resgatado na superfície marciana.
 *
 * <p>LSP: a classe é abstrata e {@code getPontuacao()} não tem implementação
 * padrão. No original, {@code Passageiro} era concreta e devolvia 10 pontos,
 * o que permitia criar um passageiro "genérico" cuja pontuação não
 * significava nada. Aqui todo subtipo é obrigado a declarar seu valor, e
 * qualquer um deles pode ocupar o lugar da classe base sem surpresa.</p>
 *
 * <p>OCP: um tipo novo de passageiro é uma subclasse nova mais o registro de
 * uma fábrica no {@code Main}. Nem o serviço nem a apresentação mudam.</p>
 */
public abstract class Passageiro extends EntidadeMapa {

    private final String nome;
    private final String tipo;

    protected Passageiro(String nome, String tipo, int x, int y) {
        super(x, y);
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    /** Pontos que o resgate deste passageiro soma à missão. */
    public abstract int getPontuacao();
}
