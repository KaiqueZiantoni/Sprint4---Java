#!/bin/bash
# Testa todos os endpoints da API SoulMove.
# Uso: bash testes/testar_api.sh http://localhost:8080
# Precisa do curl. Com o banco H2 (sem DB_URL) os ids 1 e 2 ja existem.

URL=${1:-http://localhost:8080}
ERROS=0

chamar() {
    # chamar <status esperado> <metodo> <rota> [json]
    ESPERADO=$1; METODO=$2; ROTA=$3; CORPO=$4
    if [ -z "$CORPO" ]; then
        RESPOSTA=$(curl -s -w "\n%{http_code}" -X "$METODO" "$URL$ROTA")
    else
        RESPOSTA=$(curl -s -w "\n%{http_code}" -X "$METODO" "$URL$ROTA" -H "Content-Type: application/json" -d "$CORPO")
    fi
    STATUS=$(echo "$RESPOSTA" | tail -n1)
    CORPO_RESP=$(echo "$RESPOSTA" | sed '$d')
    if [ "$STATUS" = "$ESPERADO" ]; then
        echo "[OK ] $METODO $ROTA -> $STATUS"
    else
        echo "[ERRO] $METODO $ROTA -> esperado $ESPERADO, recebido $STATUS"
        echo "       $CORPO_RESP"
        ERROS=$((ERROS+1))
    fi
    ULTIMO_CORPO="$CORPO_RESP"
}

echo "== Status =="
chamar 200 GET /

echo "== Usuarios =="
chamar 200 GET /usuarios
chamar 200 GET /usuarios/1
chamar 404 GET /usuarios/9999
chamar 201 POST /usuarios '{"nome":"Carlos Teste","cpf":"390.533.447-05","email":"carlos.teste@exemplo.com","numeroBilheteTransporte":"BU-TESTE-01"}'
ID_USUARIO=$(echo "$ULTIMO_CORPO" | sed 's/.*"idUsuario":\([0-9]*\).*/\1/')
echo "     usuario criado: id $ID_USUARIO"
chamar 400 POST /usuarios '{"nome":"Fraude","cpf":"111.111.111-11","email":"x@x.com","numeroBilheteTransporte":"BU-0"}'
chamar 400 POST /usuarios '{"nome":"Repetido","cpf":"390.533.447-05","email":"outro@exemplo.com","numeroBilheteTransporte":"BU-1"}'
chamar 200 PUT /usuarios/$ID_USUARIO '{"nome":"Carlos Teste Alterado","cpf":"390.533.447-05","email":"carlos.novo@exemplo.com","numeroBilheteTransporte":"BU-TESTE-01","cadastroAtivo":true}'

echo "== Transportes =="
chamar 200 GET /transportes
chamar 200 GET /transportes/1
chamar 404 GET /transportes/9999
chamar 201 POST /transportes '{"tipoModal":"METRO","identificacao":"Linha 4 - Amarela","combustivelUtilizado":"Energia eletrica","fatorEmissaoCo2":0.055,"consumo":0.09,"participaDoPrograma":true}'
ID_TRANSPORTE=$(echo "$ULTIMO_CORPO" | sed 's/.*"idTransporte":\([0-9]*\).*/\1/')
echo "     transporte criado: id $ID_TRANSPORTE"
chamar 400 POST /transportes '{"tipoModal":"BARCO","identificacao":"x","combustivelUtilizado":"x","fatorEmissaoCo2":1,"consumo":1}'
chamar 200 PUT /transportes/$ID_TRANSPORTE '{"tipoModal":"METRO","identificacao":"Linha 4 - Amarela (alterada)","combustivelUtilizado":"Energia eletrica","fatorEmissaoCo2":0.05,"consumo":0.09,"participaDoPrograma":true}'

echo "== Viagens =="
chamar 201 POST /viagens "{\"idUsuario\":$ID_USUARIO,\"idTransporte\":$ID_TRANSPORTE,\"distanciaEmKm\":12,\"duracaoEmMinutos\":22,\"dataViagem\":\"2026-10-20\"}"
ID_VIAGEM=$(echo "$ULTIMO_CORPO" | sed 's/.*"idViagem":\([0-9]*\).*/\1/')
echo "     viagem criada: id $ID_VIAGEM"
echo "     $ULTIMO_CORPO"
chamar 200 GET /viagens
chamar 200 "GET" "/viagens?idUsuario=$ID_USUARIO"
chamar 200 GET /viagens/$ID_VIAGEM
chamar 404 GET /viagens/9999
chamar 400 POST /viagens "{\"idUsuario\":$ID_USUARIO,\"idTransporte\":$ID_TRANSPORTE,\"distanciaEmKm\":-5,\"duracaoEmMinutos\":22,\"dataViagem\":\"2026-10-20\"}"
chamar 404 POST /viagens "{\"idUsuario\":9999,\"idTransporte\":$ID_TRANSPORTE,\"distanciaEmKm\":5,\"duracaoEmMinutos\":22,\"dataViagem\":\"2026-10-20\"}"
chamar 200 PUT /viagens/$ID_VIAGEM "{\"idUsuario\":$ID_USUARIO,\"idTransporte\":$ID_TRANSPORTE,\"distanciaEmKm\":15,\"duracaoEmMinutos\":30,\"dataViagem\":\"2026-10-21\"}"
chamar 200 GET /usuarios/$ID_USUARIO
echo "     saldo apos a viagem: $ULTIMO_CORPO"

echo "== Resgate de pontos =="
chamar 200 POST /usuarios/$ID_USUARIO/resgates '{"pontos":10}'
chamar 400 POST /usuarios/$ID_USUARIO/resgates '{"pontos":999999}'

echo "== Impacto (calculadora do site) =="
chamar 200 GET "/impacto?kmSemanais=50"
chamar 400 GET "/impacto?kmSemanais=-1"
chamar 400 GET "/impacto"

echo "== Regras de exclusao =="
chamar 400 DELETE /usuarios/$ID_USUARIO
chamar 204 DELETE /viagens/$ID_VIAGEM
chamar 204 DELETE /transportes/$ID_TRANSPORTE
chamar 204 DELETE /usuarios/$ID_USUARIO
chamar 404 DELETE /usuarios/$ID_USUARIO
chamar 404 GET /rota-inexistente

echo
if [ $ERROS -eq 0 ]; then
    echo "Todos os testes passaram."
else
    echo "$ERROS teste(s) falharam."
    exit 1
fi
