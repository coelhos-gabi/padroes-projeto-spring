package one.digitalinnovation.gof.service;

import one.digitalinnovation.gof.model.Endereco;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
    Cliente HTTP criado via OpenFeign para o consumo da API do Via CEP.
 */
@FeignClient(name ="viacep", url = "https://viacep.com.br/ws")
public interface ViaCepService {

    @GetMapping("/{cep}/json/")
    // Esse json vai ser convertido na classe Endereco
    Endereco consultarCep(@PathVariable("cep")String cep);
}
