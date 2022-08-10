package one.digitalinnovation.gof.service;

import one.digitalinnovation.gof.exception.ClienteException;
import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.repository.ClienteRepository;
import one.digitalinnovation.gof.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl implements ClienteService{
    //Singleton: Injetar os componentes do Spring com @Autowired
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ViaCepService viaCepService;
    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository, ViaCepService viaCepService) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.viaCepService = viaCepService;
    }
    // Strategy: implementar os métodos dddddddddefinidos na interface
    @Override
    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(()
                        -> new ClienteException("Cliente não encontrado"));
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        //Buscar cliente por ID, caso exista;
        //Verificar se o endereco do Cliente já existe (pelo CEP).
        // Caso não exista, integrar com o ViaCEP e persistir o retorno
        // Alterar Cliente, vinculando o Endereco (novo ou existente).
        var clienteBd = clienteRepository.findById(id);
        if(clienteBd.isPresent()){
            salvarClienteComCep(cliente);
        }
    }

    @Override
    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }
    private void salvarClienteComCep(Cliente cliente) {
    //Verificar se o endereco do cliente já existe (pelo CEP)
    //Caso não exista, integrar com o ViaCEP e persistir o retorno.
    //Inserir Cliente, vinculando o Endereco (novo ou existente).

        String cep = cliente.getEndereco().getCep();
        var endereco = enderecoRepository.findById(cep)
                .orElseGet(()->{
                    var novoEndereco = viaCepService.consultarCep(cep);
                    return  enderecoRepository.save(novoEndereco);
                });
        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }
}
