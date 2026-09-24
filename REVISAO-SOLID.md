# Revisão final da atividade

**Aluno:** Arthur Pinheiro
**Disciplina:** Projeto e Arquitetura de Sistemas
**Data:** 24/09/2026
**Versão analisada:** `src/solidexercicio10`, comparada com `src/exercicio10`

---

## 1. Como validei a solução

| Item | Comando | Resultado |
| --- | --- | --- |
| Compilação do código inicial | `javac -encoding UTF-8 -d out src/exercicio10/*.java` | sem erros |
| Compilação da versão refatorada | `javac -encoding UTF-8 -Xlint:all -d out-solid ...` | sem erros e **sem avisos**, mesmo com `-Xlint:all` |
| Compilação dos testes | `javac -encoding UTF-8 -cp out-solid -d out-test test/*.java` | sem erros |
| Testes de unidade | `java -cp "out-solid;out-test" TestesMissaoMarte` | **71 verificações, 0 falhas** |
| Teste de integração | `java -cp "out-solid;out-test" TesteIntegracaoConsole` | **17 verificações, 0 falhas** |
| Teste funcional do jogo | `bash test/teste-funcional.sh` | **69 cenários, 0 falhas** |
| Determinismo | suíte executada 3 vezes seguidas | mesmo resultado nas 3 |
| **Total** | | **157 verificações, 0 falhas** |

A saída completa das três está em [`docs/evidencia-testes.md`](docs/evidencia-testes.md).

### Checklist manual, item a item

Cada linha foi executada nas **duas** versões e comparada.

- [x] compilação do código inicial;
- [x] compilação da versão refatorada;
- [x] início de uma missão pelo menu (opção 1);
- [x] escolha de piloto, dificuldade e tamanho de mapa;
- [x] movimentação `w/s/a/d` com o custo de 1 ponto por movimento;
- [x] embarque com `c`, incluindo o caso "nenhum passageiro nesta posição";
- [x] colisão com asteroide e com inimigo, com perda de vida;
- [x] encerramento por `q`, por pontuação zerada e por destruição da nave;
- [x] vitória exigindo todos embarcados **e** nave de volta em (0,0);
- [x] estatísticas ao final da partida vencida;
- [x] consulta do ranking (opção 2), com e sem registros;
- [x] reset do ranking (opção 3), confirmando e cancelando;
- [x] persistência: arquivo JSON criado, relido por outra instância e apagado no reset;
- [x] opção inválida no menu;
- [x] outro teste: **mapa pequeno demais para a dificuldade** (ver achado M1).

### Comparação de saída entre as versões

Com a mesma entrada, as duas versões produzem o mesmo mapa, a mesma legenda na mesma
ordem, a mesma linha de status e a mesma pontuação. Saída real da versão refatorada,
dificuldade Fácil, mapa 3, após um comando `d`:

```
Mapa da Missão (Pontos: 29) - Piloto: Arthur
     -3 -2 -1  0  1  2  3
     __ __ __ __ __ __ __
 -3|  .  #  .  .  .  .  .
 -2|  .  .  .  .  .  E  .
 -1|  P  .  .  .  .  .  .
  0|  .  .  .  L  @  .  .
  1|  .  .  .  P  .  .  .
  2|  .  .  .  .  .  .  .
  3|  .  .  X  .  .  E  .
Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta, #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio
Nave em (1,0) | Pontos: 29 | Vidas: 3 | A bordo: 0/5 | Restantes: 4
```

O arquivo de ranking gravado pela versão refatorada mantém o mesmo esquema JSON do
original:

```json
[{"name":"Arthur","score":66,"dificuldade":"FACIL","passageirosColetados":4,"dataHora":"2026-09-24 16:15:29","tempoJogo":0}]
```

---

## 2. Achados por princípio

### SRP

```text
Local: exercicio10.Main (557 linhas, 472 efetivas) -> solidexercicio10.Main (60 linhas, 21 efetivas)
Princípio relacionado: SRP
Observação: a classe original tinha seis motivos independentes para mudar: fluxo do
  menu, regras da partida, desenho da tela, geração do cenário, formato de
  armazenamento e leitura da entrada. Qualquer uma dessas mudanças obrigava a abrir
  o mesmo arquivo, e duas pessoas mexendo em coisas diferentes colidiam no mesmo
  ponto. Hoje esses seis motivos estão em JogoService, PartidaService, MapaRenderer,
  GeradorDeMissao, ArquivoRankingRepository e ConsoleUI.
Impacto para manutenção, testes ou evolução: com tudo junto, não havia como exercitar
  uma regra sem console e sem disco, e por isso o projeto não tinha nenhum teste.
  Depois da separação, 157 verificações automatizadas passaram a rodar em menos de um
  segundo.
Proposta: implementado.
Prioridade: alta (feito)
```

```text
Local: solidexercicio10.presentation.ConsoleUI
Princípio relacionado: SRP
Observação: no original, System.out aparecia 65 vezes, em 12 dos 19 métodos de Main,
  no meio das regras. Agora é a única classe do projeto que chama System.out e
  Scanner. Nenhum texto de tela sobrou dentro do pacote service.
Impacto para manutenção, testes ou evolução: mudar uma mensagem é mexer em um arquivo
  só, sem risco de tocar em regra de jogo. E o serviço pôde ser testado com uma
  implementação falsa no lugar do console.
Proposta: implementado.
Prioridade: alta (feito)
```

### OCP

```text
Local: model.FabricaPassageiro, service.GeradorDeMissao, presentation.MapaRenderer
Princípio relacionado: OCP
Observação: no original, acrescentar um tipo de passageiro exigia editar DOIS pontos
  da lógica principal: o switch de criarPassageiroPolimorfico e a cadeia de
  instanceof de desenharMapa. Agora o catálogo de fábricas é montado no Main, o
  gerador percorre esse catálogo sem citar nenhum tipo concreto, e o renderizador
  usa getSimbolo() por polimorfismo e deriva a legenda do mesmo catálogo.
Impacto para manutenção, testes ou evolução: um tipo novo é uma classe nova mais uma
  linha no Main. O teste testeRendererAceitaTipoNovoSemAlteracao() cria um passageiro
  "Médico" dentro do próprio teste e confirma que ele aparece no mapa e na legenda
  sem que nenhuma classe de produção tenha sido alterada.
Proposta: implementado.
Prioridade: alta (feito)
```

```text
Local: model.Dificuldade
Princípio relacionado: OCP
Observação: a configuração de cada dificuldade (pontuação inicial e quantidade de
  passageiros, asteroides e inimigos) estava em dois switch dentro de Main. Passou
  para o próprio enum, como atributos de cada constante.
Impacto para manutenção, testes ou evolução: uma dificuldade nova ("Insano") é uma
  constante a mais, sem nenhum switch para atualizar e sem risco de esquecer um dos
  dois lugares.
Proposta: implementado.
Prioridade: média (feito)
```

### LSP

```text
Local: model.Passageiro
Princípio relacionado: LSP
Observação: no original, Passageiro era concreta e devolvia 10 pontos por padrão.
  Dava para instanciar um passageiro "genérico" cuja pontuação não representava
  nenhuma regra, e Professor sobrescrevia com o mesmo 10, o que torna o override
  indistinguível do comportamento herdado. Agora a classe é abstrata e
  getPontuacao() não tem implementação padrão.
Impacto para manutenção, testes ou evolução: o compilador passou a exigir que todo
  subtipo declare o próprio valor. Qualquer Passageiro pode ocupar o lugar da base
  sem que o chamador precise saber o tipo concreto.
Proposta: implementado.
Prioridade: alta (feito)
```

```text
Local: model.Movel, model.Nave, model.Inimigo
Princípio relacionado: LSP
Observação: os limites do mapa fazem parte da assinatura de mover(Direcao, Limites).
  Isso obriga toda implementação a respeitá-los e elimina a possibilidade de existir
  um caminho de movimento que ignore a borda. No tutorial, Movel declara
  mover(int dx, int dy) sem limites e Nave ganha um moverComLimites separado, o que
  permite nave.mover(99, 99) teleportar a nave para fora do mapa (ver D1).
Impacto para manutenção, testes ou evolução: o teste
  testeMovelNaoUltrapassaOsLimites() verifica a mesma garantia em Nave e em Inimigo,
  o que só faz sentido porque o contrato é o mesmo para as duas.
Proposta: implementado.
Prioridade: alta (feito)
```

### ISP

```text
Local: presentation.EntradaJogo, SaidaJogo, EntradaPartida, SaidaPartida
Princípio relacionado: ISP
Observação: em vez de uma interface única de interação com 29 métodos, a apresentação
  expõe quatro contratos pequenos, separados por cliente (JogoService e
  PartidaService) e por direção (entrada e saída). JogoService não enxerga
  lerComando(); PartidaService não enxerga exibirRanking().
Impacto para manutenção, testes ou evolução: os testes implementam só o par que
  precisam. O PilotoAutomatico dos testes implementa EntradaPartida e SaidaPartida e
  ignora completamente as duas interfaces de menu.
Proposta: implementado, com a ressalva do achado M2 abaixo.
Prioridade: alta (feito)
```

```text
Local: repository.RankingRepository
Princípio relacionado: ISP
Observação: três operações (salvar, listarTop, limpar), todas usadas por JogoService.
  O tutorial propõe quatro, sendo uma delas a sobrecarga salvar(String, int) que
  nenhum cliente chama (ver D2).
Impacto para manutenção, testes ou evolução: cada implementação nova do contrato
  escreve três métodos, não quatro, e nenhum deles é um método morto que precisa
  existir só para satisfazer a interface.
Proposta: implementado.
Prioridade: média (feito)
```

### DIP

```text
Local: service.JogoService <- repository.RankingRepository
Princípio relacionado: DIP
Observação: o serviço recebe a abstração pelo construtor e nunca cita arquivo,
  caminho ou JSON. Quem escolhe a implementação concreta é o Main, em uma linha.
Impacto para manutenção, testes ou evolução: a pergunta orientadora da atividade era
  "o que seria necessário alterar para trocar o arquivo por um banco ou por uma
  implementação em memória?". A resposta está no repositório: MemoriaRankingRepository
  existe, é usada nos testes, e nenhuma linha de JogoService, PartidaService ou
  ConsoleUI precisou mudar para ela funcionar. Um repositório em banco entraria do
  mesmo jeito.
Proposta: implementado.
Prioridade: alta (feito)
```

```text
Local: service.PartidaService <- presentation.EntradaPartida / SaidaPartida
Princípio relacionado: DIP
Observação: o DIP foi aplicado também na apresentação, não só na persistência. O
  serviço da partida depende de duas interfaces, nunca de Scanner nem de System.out.
Impacto para manutenção, testes ou evolução: é exatamente o que torna possível o
  teste testePartidaCompletaVenceERegistraNoRanking(), que joga uma partida inteira
  até a vitória sem console, sem disco e em milissegundos. No código original esse
  teste seria impossível de escrever.
Proposta: implementado.
Prioridade: alta (feito)
```

---

## 3. Melhorias adicionais identificadas

### M1. Mapa pequeno demais travava o jogo (defeito real, herdado do original)

```text
Local: exercicio10.Main.criarNovaMissao e solidexercicio10.service.GeradorDeMissao
Princípio relacionado: nenhum diretamente; é um defeito de robustez encontrado
  durante a refatoração
Observação: o sorteio de posição livre é um laço que repete até achar célula vazia.
  Com mapa 1 (9 células) e dificuldade Médio (10 entidades: 1 nave + 5 passageiros
  + 2 asteroides + 2 inimigos) não existe célula livre para a última entidade, e o
  laço nunca termina. Reproduzido nas duas versões: o processo fica preso e precisa
  ser morto. Difícil com mapa 1 tem o mesmo problema (12 entidades).
Impacto para manutenção, testes ou evolução: o jogo trava sem mensagem nenhuma, com
  uma entrada que o próprio menu aceita. Para quem está jogando parece que o
  programa morreu.
Proposta: implementada como divergência deliberada do original. Dificuldade passou a
  saber getTotalDeEntidades() e getTamanhoMinimoDeMapa(); PartidaService ajusta o
  tamanho pedido para o mínimo e avisa o piloto; GeradorDeMissao recusa com
  IllegalArgumentException em vez de entrar no laço infinito. Coberto por
  testeMapaPequenoDemaisNaoTravaOJogo().
Prioridade: ALTA (feito)
```

### M2. `SaidaPartida` tem 13 métodos e é a maior interface do projeto

```text
Local: solidexercicio10.presentation.SaidaPartida
Princípio relacionado: ISP
Observação: é a interface mais aberta a crítica na minha solução. São treze métodos,
  um por evento da partida. Todos são chamados por PartidaService, então o ISP na
  definição estrita ("clientes não dependem de métodos que não usam") está
  respeitado, mas treze métodos é muito para um contrato só, e cada evento novo do
  jogo obriga toda implementação a crescer junto.
Impacto para manutenção, testes ou evolução: os fakes dos testes precisam escrever
  treze métodos vazios, o que é ruído. Se amanhã a partida ganhar um evento novo,
  três implementações mudam ao mesmo tempo.
Proposta: trocar os métodos de evento por um único notificar(EventoPartida evento),
  com EventoPartida sendo uma sealed interface com um record por evento
  (Embarque(passageiro, bonus), Colisao(vidasRestantes), NaveDestruida, e assim por
  diante). O serviço continua sem conhecer texto de tela, a interface cai para dois
  ou três métodos, e a formatação vira um switch de pattern matching dentro do
  ConsoleUI. Não implementei porque acrescenta uma hierarquia de treze records a um
  projeto deste tamanho, e o ganho é de organização, não de comportamento.
Prioridade: MÉDIA
```

### M3. O parser de JSON quebra com vírgula dentro do nome do piloto

```text
Local: solidexercicio10.repository.ArquivoRankingRepository.lerEntrada
Princípio relacionado: SRP (a classe já isola o problema, mas a implementação é frágil)
Observação: o parser divide o objeto com split(","), então um piloto chamado
  "Silva, Arthur" gera um campo quebrado na releitura. O código original tem
  exatamente o mesmo defeito, e eu o mantive para preservar o formato do arquivo.
  A gravação escapa aspas e barras invertidas corretamente, mas a leitura não
  entende essas sequências em todos os casos.
Impacto para manutenção, testes ou evolução: um nome com vírgula corrompe aquela
  linha do ranking silenciosamente. É um caso raro, mas é perda de dado.
Proposta: trocar o parser manual por um de verdade. Sem biblioteca externa, escrever
  um leitor que respeite aspas ao separar os campos, em vez de split(","); com
  biblioteca, usar Gson ou Jackson. O ponto forte do desenho atual é que essa troca
  fica inteiramente dentro de ArquivoRankingRepository, sem tocar em mais nada.
Prioridade: MÉDIA
```

### M4. Falha de leitura do ranking derruba o jogo

```text
Local: solidexercicio10.repository.ArquivoRankingRepository.carregar
Princípio relacionado: SRP e tratamento de erro
Observação: troquei o comportamento do original aqui. Lá, IOException era engolida e
  a resposta era uma lista vazia, ou seja, um arquivo corrompido fazia o ranking
  "sumir" sem avisar ninguém. Na minha versão vira UncheckedIOException, que sobe e
  encerra o jogo. Também não é o ideal: o piloto perde a partida por causa de um
  problema de disco.
Impacto para manutenção, testes ou evolução: hoje o erro aparece, o que é melhor do
  que silêncio, mas aparece do jeito mais bruto possível.
Proposta: JogoService captura a exceção na fronteira e chama um
  saida.exibirFalhaNoRanking(motivo), mantendo a partida viva e o problema visível.
Prioridade: MÉDIA
```

### M5. Nenhum framework de teste e nenhuma automação de build

```text
Local: test/TestesMissaoMarte.java e test/TesteIntegracaoConsole.java
Princípio relacionado: nenhum; é infraestrutura
Observação: os testes são um main() com um método checar() escrito à mão, porque o
  projeto não tem Maven nem Gradle e adicionar um .jar de JUnit ao repositório
  pareceu pior do que trinta linhas de apoio.
Impacto para manutenção, testes ou evolução: sem relatório padronizado, sem execução
  seletiva de um teste, sem integração com a IDE.
Proposta: adicionar um pom.xml mínimo com JUnit 5 e migrar as verificações para
  @Test/assertEquals. A estrutura de classes não muda nada: os testes atuais já são
  unitários de verdade, só falta o arcabouço.
Prioridade: BAIXA
```

### M6. `Missao.getEntidades()` monta uma lista nova a cada chamada

```text
Local: solidexercicio10.model.Missao.getEntidades
Princípio relacionado: nenhum; é desempenho
Observação: o método cria um ArrayList com todas as entidades toda vez que é chamado,
  e posicaoOcupada() o chama dentro do laço de sorteio do gerador. Em um mapa grande
  com muitos sorteios, isso é alocação desnecessária.
Impacto para manutenção, testes ou evolução: irrelevante nas escalas deste jogo
  (dezenas de entidades, dezenas de sorteios). Só viraria problema com mapas ordens
  de grandeza maiores.
Proposta: manter um índice de posições ocupadas em um Set dentro de Missao,
  atualizado nos add* e no embarque. Não fiz porque acrescenta estado duplicado e
  uma chance de inconsistência para resolver um custo que ninguém sente.
Prioridade: BAIXA
```

---

## 4. Decisões do tutorial com as quais **concordo**

### C1. `RankingRepository` como interface, com a implementação por trás

**Benefício:** é o exemplo mais claro de DIP do projeto e o único que consegui
comprovar com código em vez de argumento. Escrevi uma segunda implementação
(`MemoriaRankingRepository`) em 25 linhas e ela entrou no lugar da primeira sem que
`JogoService`, `PartidaService` ou `ConsoleUI` mudassem uma linha. É a resposta
concreta para a pergunta orientadora sobre trocar o arquivo por banco ou por memória.
Além disso, foi essa interface que permitiu testar a regra de entrada no Top 5 sem
tocar em disco.

### C2. `EntidadeMapa` com `getSimbolo()` polimórfico

**Benefício:** apaga a cadeia de `instanceof Engenheiro` / `instanceof Astronauta` do
`desenharMapa` original. A diferença aparece na prática: no código original, um tipo
novo de passageiro exige editar o renderizador; na versão refatorada, o teste
`testeRendererAceitaTipoNovoSemAlteracao()` define um tipo "Médico" dentro do próprio
teste e ele é desenhado corretamente sem nenhuma alteração em classe de produção.

### C3. `Passageiro` como classe abstrata

**Benefício:** o original permitia um passageiro genérico valendo 10 pontos, e
`Professor` sobrescrevia com exatamente o mesmo 10, o que deixa o override sem sentido
e esconde se a pontuação veio da base ou do subtipo. Tornar a classe abstrata e o
método sem corpo transfere essa regra para o compilador.

---

## 5. Decisões do tutorial com as quais **não concordo**

### D1. `Movel.mover(int dx, int dy)` sem limites, com `moverComLimites` ao lado

```text
Local: tutorial, Passo 5 (model/Movel.java, model/Nave.java, model/Inimigo.java)
Princípio relacionado: LSP e ISP
Observação: o tutorial declara Movel com mover(int dx, int dy) e, na mesma classe
  Nave, um moverComLimites(char, int, int, int, int) separado. Existem então dois
  caminhos para mover a nave e só um respeita a borda do mapa. Uma chamada
  nave.mover(99, 99), que é exatamente o que o contrato Movel autoriza, teleporta a
  nave para fora do mapa. O Inimigo do tutorial tem o mesmo buraco: mover(dx, dy)
  soma direto em x e y sem nenhuma verificação.
Impacto para manutenção, testes ou evolução: quem programa contra a abstração Movel
  recebe a versão insegura, e quem quer a versão correta precisa saber que existe um
  método concreto com outro nome. Isso derrota o propósito da interface: não dá para
  tratar Nave e Inimigo como Movel e confiar no resultado.
Proposta: foi o que implementei. Movel declara
  mover(Direcao direcao, Limites limites): boolean, os limites entram no contrato e
  não existe caminho alternativo. Criei também o enum Direcao, que tira o switch de
  teclas de dentro do modelo, e o record Limites, que substitui os quatro parâmetros
  soltos repetidos em oito assinaturas do original.
Prioridade: ALTA (implementado de forma diferente)
```

### D2. Duas sobrecargas de `salvar` em `RankingRepository`

```text
Local: tutorial, Passo 4 (repository/RankingRepository.java)
Princípio relacionado: ISP
Observação: a interface declara salvar(String, int) e
  salvar(String, int, Dificuldade, int, long). A primeira só existe para delegar à
  segunda com valores inventados (MEDIO, 0, 0), e nenhum cliente a chama. Além
  disso, a versão de cinco parâmetros posicionais aceita trocar
  passageirosColetados por tempoJogo sem o compilador reclamar, porque os dois são
  numéricos.
Impacto para manutenção, testes ou evolução: toda implementação nova do contrato é
  obrigada a escrever um método morto. E a assinatura posicional é um convite a erro
  silencioso na hora de chamar.
Proposta: foi o que implementei. A interface tem um salvar(RankingEntry) só, com o
  objeto de valor carregando os campos nomeados. Trocar dois campos de lugar passa a
  ser erro de compilação.
Prioridade: MÉDIA (implementado de forma diferente)
```

### D3. A implementação do repositório chamada `RankingService`

```text
Local: tutorial, Passo 4 (repository/RankingService.java)
Princípio relacionado: SRP, na parte de nomear a responsabilidade
Observação: a classe que lê e escreve o arquivo se chama "Service", enquanto o
  projeto tem uma camada de serviço de verdade no pacote service. Na mesma frase o
  nome "service" passa a significar duas coisas: orquestrar o jogo e persistir dado.
Impacto para manutenção, testes ou evolução: na apresentação, e depois para quem
  pegar o código, "o service chama o service" é confuso. Nome de classe é a primeira
  documentação que alguém lê.
Proposta: nomeei ArquivoRankingRepository, com o detalhe de implementação no nome, e
  MemoriaRankingRepository para a outra. Fica óbvio qual é qual na linha de montagem
  do Main. É uma divergência da estrutura sugerida, permitida pelo enunciado, e está
  refletida nos dois diagramas UML.
Prioridade: BAIXA (implementado de forma diferente)
```

### D4. O tutorial troca comportamento do jogo sem avisar

```text
Local: tutorial, Passo 5 (model/Professor.java, Engenheiro.java, Astronauta.java,
  Asteroide.java, Nave.java)
Princípio relacionado: nenhum; é o requisito de preservar o comportamento
Observação: comparando o tutorial com o exercício 10, encontrei quatro mudanças de
  comportamento que não são apresentadas como mudanças:
    1. pontuação invertida: o tutorial usa Professor 15, Engenheiro 20, Astronauta
       10; o original usa 10, 15 e 20. O Astronauta, que era o passageiro mais
       valioso, vira o menos valioso.
    2. o símbolo do asteroide vira 'A' no lugar de '#', o que ainda colide
       visualmente com a inicial de "Astronauta" na leitura da legenda.
    3. Nave.embarcar passa a devolver void. Com a nave cheia o embarque falha em
       silêncio, e a mensagem "Nave cheia!" do original não tem como ser emitida.
    4. em moverComLimites, 'w' passa a somar 1 em y. Como o mapa é desenhado com y
       crescendo para baixo, o comando de subir passa a descer.
Impacto para manutenção, testes ou evolução: o enunciado exige manter o comportamento
  principal do jogo, e esses quatro pontos são justamente comportamento observável.
  Seguir o tutorial ao pé da letra reprovaria no próprio critério da atividade.
Proposta: não segui nenhum dos quatro. Mantive 10/15/20, '#', embarcar devolvendo
  boolean e 'w' diminuindo y. Os três primeiros estão travados por teste
  (testePontuacaoPorTipoDePassageiro, testeRendererUsaSimboloPolimorfico,
  testeNaveRespeitaCapacidade), para que ninguém os quebre de novo sem perceber.
Prioridade: ALTA (não seguido, de propósito)
```

### D5. Sobre a arquitetura ser grande demais para o projeto

```text
Local: estrutura geral (a minha, não a do tutorial)
Princípio relacionado: todos
Observação: a pergunta orientadora "a arquitetura proposta está adequada ao tamanho
  deste projeto ou introduz complexidade desnecessária?" merece uma resposta honesta.
  Um jogo de console com cinco pacotes, oito interfaces e treze classes de modelo é,
  sim, mais estrutura do que o problema exige se o objetivo fosse apenas fazer o jogo
  rodar. Mantive as abstrações em que consigo apontar o problema concreto que cada
  uma resolve, e o critério foi esse: se eu não conseguia nomear o que quebrava sem a
  abstração, ela não entrou.
Impacto para manutenção, testes ou evolução: o ponto em que a estrutura se paga é o
  teste. São 157 verificações automatizadas que não existiam e não poderiam existir no
  código original. O ponto em que ela cobra o preço é a navegação: entender o fluxo de
  uma partida hoje exige abrir quatro arquivos em vez de um.
Proposta: se o projeto parasse aqui e nunca mais mudasse, parte dessa estrutura seria
  exagero. Como ele é material didático que vai receber extensões (tipos de
  passageiro, dificuldades, persistência), ela se justifica. A abstração que eu
  cortaria primeiro em um projeto menor é a divisão da apresentação em quatro
  interfaces, que viraria uma só.
Prioridade: registro consciente, sem ação
```

---

## 6. Prioridades consolidadas

| # | Melhoria | Prioridade | Situação |
| --- | --- | --- | --- |
| M1 | Mapa pequeno demais trava o jogo | **Alta** | corrigido nesta entrega |
| D1 | `Movel` sem limites no contrato | **Alta** | implementado de outra forma |
| D4 | Mudanças de comportamento do tutorial | **Alta** | não seguidas, travadas por teste |
| M2 | `SaidaPartida` com 13 métodos | Média | proposta descrita, não implementada |
| M3 | Parser de JSON frágil com vírgula | Média | proposta descrita, não implementada |
| M4 | Falha de leitura derruba o jogo | Média | proposta descrita, não implementada |
| D2 | Sobrecarga morta em `salvar` | Média | implementado de outra forma |
| M5 | Sem JUnit e sem build automatizado | Baixa | proposta descrita |
| M6 | `getEntidades()` realoca a cada chamada | Baixa | proposta descrita |
| D3 | Nome `RankingService` para um repositório | Baixa | implementado de outra forma |

**A primeira que eu implementaria numa próxima versão:** M3, o parser de JSON. É a
única das pendentes que pode causar perda de dado, e a refatoração já deixou o
conserto confinado a uma classe só.

---

## 7. Limitações conhecidas

1. **Sem tratamento de concorrência no arquivo de ranking.** Duas instâncias do jogo
   abertas ao mesmo tempo podem sobrescrever o registro uma da outra. O original tem
   a mesma limitação e não é um cenário do enunciado.
2. **Emojis no console do Windows.** As mensagens de vitória e recorde usam emoji,
   como no original. O terminal do Windows com a página de código padrão os exibe
   como `?`. É cosmético e idêntico ao comportamento do jogo original.
3. **O parser de JSON é o do original** (ver M3), com a mesma fragilidade.
4. **Os testes são um `main()` próprio**, não JUnit (ver M5).
5. **`ranking.json` e `ranking-solid-exercicio10.json` são arquivos separados.** As
   duas versões não compartilham ranking, de propósito, para que rodar uma não
   interfira na outra durante a comparação.
6. **O `.gitignore` do repositório foi alterado.** Ele continha `src/solid*` e
   `src/exer*`, que faziam o `git` descartar em silêncio o código-fonte da entrega.
   As duas linhas foram removidas e o motivo está registrado no próprio arquivo e em
   `docs/analise-inicial.md`.
