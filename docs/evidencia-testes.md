# Evidência dos testes

Saída completa das três suítes, gerada em 24/09/2026 a partir do código deste
repositório. Reproduzível com os comandos do `README.md`.

| Suíte | Verificações | Falhas |
| --- | ---: | ---: |
| Unidade (`TestesMissaoMarte`) | 71 | 0 |
| Integração do console (`TesteIntegracaoConsole`) | 17 | 0 |
| Funcional do jogo (`teste-funcional.sh`) | 69 | 0 |
| **Total** | **157** | **0** |

## 1. Testes de unidade

```
java -cp "out-solid;out-test" TestesMissaoMarte
```

```text
  ok   Professor vale 10 pontos, como no original
  ok   Engenheiro vale 15 pontos, como no original
  ok   Astronauta vale 20 pontos, como no original
  ok   Passageiro Professor informa pontuação positiva
  ok   Passageiro Professor informa tipo preenchido
  ok   Passageiro Professor informa símbolo de mapa
  ok   Passageiro Engenheiro informa pontuação positiva
  ok   Passageiro Engenheiro informa tipo preenchido
  ok   Passageiro Engenheiro informa símbolo de mapa
  ok   Passageiro Astronauta informa pontuação positiva
  ok   Passageiro Astronauta informa tipo preenchido
  ok   Passageiro Astronauta informa símbolo de mapa
  ok   Primeiro embarque aceito
  ok   Segundo embarque aceito
  ok   Terceiro embarque recusado com a nave cheia
  ok   Nave ficou com exatamente 2 passageiros
  ok   getPassageiros() não permite furar a capacidade por fora
  ok   Nave move dentro do mapa
  ok   Nave recusa o movimento que sairia do mapa
  ok   Nave continua na borda
  ok   Inimigo recusa o movimento que sairia do mapa
  ok   Inimigo continua na borda
  ok   Sem perigos não há colisão
  ok   Asteroide na posição da nave gera colisão
  ok   Inimigo na posição da nave gera colisão
  ok   Sem passageiro sob a nave não há quem embarcar
  ok   Embarque falha longe do passageiro
  ok   Passageiro continua na superfície
  ok   Embarque funciona sobre o passageiro
  ok   Passageiro saiu da superfície
  ok   Passageiro está a bordo
  ok   Missão só termina com a nave na plataforma
  ok   Mapa desenha a nave
  ok   Mapa desenha o engenheiro
  ok   Mapa desenha o asteroide
  ok   Mapa desenha o inimigo
  ok   Mapa mostra a pontuação recebida
  ok   Mapa mostra o piloto recebido
  ok   Legenda mantém a ordem do original
  ok   Tipo novo aparece na legenda sem tocar no MapaRenderer
  ok   Tipo novo é desenhado no mapa
  ok   Top respeita o limite pedido
  ok   Maior pontuação vem primeiro
  ok   Segunda colocação correta
  ok   Limpar esvazia o ranking em memória
  ok   Ranking inexistente começa vazio
  ok   Arquivo guarda no máximo o limite configurado
  ok   Arquivo mantém os melhores
  ok   Dados sobrevivem a uma instância nova
  ok   Pontuação persistida corretamente
  ok   Dificuldade persistida corretamente
  ok   Limpar apaga o arquivo
  ok   Fácil precisa de 7 posições
  ok   Médio precisa de 10 posições
  ok   Difícil precisa de 12 posições
  ok   Fácil cabe no mapa 1 (9 células)
  ok   Médio exige mapa 2 (25 células)
  ok   Difícil exige mapa 2 (25 células)
  ok   Gerador recusa mapa que não comporta o cenário, em vez de travar
  ok   Partida com mapa pequeno termina em vez de travar
  ok   Piloto foi avisado do ajuste de tamanho
  ok   Partida terminou em vitória
  ok   Todos os 4 passageiros da dificuldade Fácil foram resgatados
  ok   Pontuação final positiva
  ok   Movimentos foram contados
  ok   Resultado é elegível para o ranking
  ok   Piloto preservado no resultado
  ok   Dificuldade preservada no resultado
  ok   Vitória exige nave na plataforma
  ok   Resultado entrou no ranking
  ok   Ranking guardou a pontuação da partida

71 verificações, 0 falha(s).
TODOS OS TESTES PASSARAM
```

## 2. Teste de integração do console

Monta o jogo exatamente como o `Main`, com `ConsoleUI` e arquivo de verdade.

```
java -cp "out-solid;out-test" TesteIntegracaoConsole
```

```text
  ok   Sequência descoberta termina em vitória
  ok   Piloto automático produziu uma sequência de comandos
  ok   Jogo real exibiu o menu principal
  ok   Jogo real desenhou o mapa
  ok   Jogo real embarcou passageiro
  ok   Jogo real concluiu a missão
  ok   Jogo real exibiu as estatísticas
  ok   Jogo real anunciou a entrada no Top 5
  ok   Jogo real listou o ranking com o piloto
  ok   Jogo real se despediu ao sair
  ok   Arquivo de ranking foi criado em disco
  ok   Arquivo contém o nome do piloto
  ok   Arquivo contém a dificuldade da partida
  ok   Arquivo contém os passageiros coletados
  ok   Ranking relido tem uma entrada
  ok   Ranking relido tem o piloto certo
  ok   Ranking relido tem pontuação positiva

Trecho da saída real do jogo:
Comando (w/s/a/d/c/q): 
================================================================
🚀 DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0).
Retornando à órbita marciana com todos os passageiros. Missão cumprida!
================================================================
Estatísticas da Partida:
 - Pontuação Final: 66 pontos
 - Movimentos Efetuados: 14
 - Tempo de Jogo: 0 segundos
 - Passageiros Resgatados: 4
================================================================
Parabéns! Você entrou para o Top 5 de pilotos!

--- MENU PRINCIPAL ---
1. Iniciar Nova Missão
2. Visualizar Ranking Top 5
3. Resetar Histórico de Ranking
4. Sair do Jogo
----------------------
Escolha uma opção: 
====== RANKING TOP 5 PILOTOS ======
1. Arthur - 66 pts | Dificuldade: Fácil | Coletados: 4 | Tempo: 0s | 2026-09-24 17:00:31
===================================

--- MENU PRINCIPAL ---
1. Iniciar Nova Missão
2. Visualizar Ranking Top 5
3. Resetar Histórico de Ranking
4. Sair do Jogo
----------------------
Escolha uma opção: 
Obrigado por jogar a Missão Marte Unifor!
Conteúdo do arquivo de ranking:
  [{"name":"Arthur","score":66,"dificuldade":"FACIL","passageirosColetados":4,"dataHora":"2026-09-24 17:00:31","tempoJogo":0}]

17 verificações, 0 falha(s).
INTEGRACAO OK
```

## 3. Teste funcional do jogo

Roda o console de verdade com entrada roteirizada e confere a saída, cobrindo
menu, ranking, reset, as três dificuldades, entradas inválidas, os comandos da
partida, o encerramento e a robustez do arquivo.

```
bash test/teste-funcional.sh
```

```text

[1] MENU PRINCIPAL
  ok    abre com as boas-vindas
  ok    mostra as 4 opcoes do menu
  ok    sai pela opcao 4
  ok    recusa opcao invalida
  ok    recusa opcao nao numerica

[2] RANKING VAZIO
  ok    ranking vazio avisa que nao ha registro
  ok    exibe o cabecalho do ranking

[3] RANKING COM DADOS (leitura do arquivo)
  ok    le o ranking do arquivo e ordena por pontuacao
  ok    segunda colocacao correta
  ok    quinta colocacao correta
  ok    corta no top 5, sexto nao aparece
  ok    exibe dificuldade na linha do ranking
  ok    exibe passageiros coletados
  ok    exibe tempo de jogo
  ok    exibe data e hora

[4] RESET DO RANKING
  ok    reset cancelado com n
  ok    arquivo continua existindo apos cancelar
  ok    reset confirmado com s
  ok    arquivo foi apagado
  ok    reset confirmado com a palavra sim
  ok    ranking fica vazio depois do reset

[5] ESCOLHA DE PILOTO
  ok    nome vazio vira Piloto Anônimo
  ok    aceita nome com espaco

[6] ESCOLHA DE DIFICULDADE
  ok    dificuldade facil sem acento
  ok    facil comeca com 30 pontos
  ok    dificuldade facil com acento
  ok    dificuldade medio em maiuscula
  ok    medio comeca com 20 pontos
  ok    dificuldade dificil sem acento
  ok    dificil comeca com 15 pontos
  ok    dificuldade dificil com acento
  ok    dificuldade invalida cai em Medio

[7] TAMANHO DO MAPA
  ok    tamanho nao numerico avisa
  ok    tamanho nao numerico usa mapa 5
  ok    tamanho zero usa mapa 5
  ok    tamanho negativo usa mapa 5
  ok    tamanho 2 monta mapa de -2 a 2

[8] MAPA PEQUENO DEMAIS (defeito corrigido)
  ok    medio com mapa 1 NAO trava (terminou dentro do timeout)
  ok    avisa que o mapa nao comporta o cenario
  ok    joga no mapa 2
  ok    dificil com mapa 1 NAO trava
  ok    dificil tambem avisa o ajuste
  ok    facil cabe no mapa 1 e nao e ajustado

[9] DESENHO DO MAPA
  ok    cabecalho do mapa com pontos e piloto
  ok    legenda completa na ordem do original
  ok    lista de comandos
  ok    lista os passageiros na superficie
  ok    linha de status da nave
  ok    desenha a nave

[10] COMANDOS DA PARTIDA
  ok    comando invalido e recusado
  ok    embarcar sem passageiro na posicao
  ok    abortar com q
  ok    mover para a direita custa 1 ponto
  ok    nave foi para (1,0)
  ok    plataforma L aparece quando a nave sai
  ok    mover para baixo aumenta y
  ok    mover para cima diminui y
  ok    mover para a esquerda diminui x
  ok    borda do mapa bloqueia o movimento
  ok    movimento bloqueado ainda custa ponto (como no original)

[11] ENCERRAMENTO POR PONTUACAO ZERADA
  ok    30 movimentos zeram a pontuacao da dificuldade Facil
  ok    partida perdida nao mostra estatisticas

[12] VARIAS PARTIDAS SEGUIDAS NA MESMA EXECUCAO
  ok    primeira partida roda
  ok    segunda partida roda na mesma sessao
  ok    menu volta e o ranking ainda responde
  ok    sai normalmente no fim

[13] ROBUSTEZ DO ARQUIVO DE RANKING
  ok    arquivo com lista vazia nao quebra
  ok    arquivo vazio nao quebra
  ok    arquivo inexistente nao quebra

================================================
69 verificacoes, 0 falha(s).
TESTE FUNCIONAL PASSOU
```
