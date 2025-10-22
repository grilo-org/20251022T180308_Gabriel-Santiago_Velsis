package velsis.back.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import velsis.back.dtos.CreateUsuarioDTO;
import velsis.back.dtos.UpdateAddressUsuarioDTO;
import velsis.back.dtos.UpdateBirthDateUsuarioDTO;
import velsis.back.dtos.UpdateDocumentUsuarioDTO;
import velsis.back.dtos.UpdateNameUsuarioDTO;
import velsis.back.dtos.UpdateUsuarioDTO;
import velsis.back.dtos.UsuarioDTO;
import velsis.back.dtos.UsuarioForUpdateDTO;
import velsis.back.services.UsuarioService;

import java.util.List;

@RequestMapping("/usuarios")
@RestController
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Cria um novo usuário no sistema com validação completa
     * Realiza consulta de CEP via ViaCEP e validação de documento único
     *
     * @param dto DTO com dados obrigatórios do usuário (nome, documento, CEP, etc.)
     * @return HTTP 201 (Created) em caso de sucesso
     * @throws MethodArgumentNotValidException se os dados de entrada forem inválidos
     * @throws DocumentExistException se o documento já estiver cadastrado
     * @throws CepNotFoundException se o CEP não for encontrado
     */
    @PostMapping
    public ResponseEntity<Void> createUsuario(@RequestBody @Valid CreateUsuarioDTO dto){
        usuarioService.createUsuario(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Recupera lista de todos os usuários cadastrados no sistema
     * Retorna dados resumidos para otimização de performance
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> findAllUsuarios(){
        return new ResponseEntity<>(usuarioService.findAllUsuarios(), HttpStatus.OK);
    }

    /**
     * Recupera o usuário cadastrado no sistema
     * Retorna dados resumidos para atualização
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<UsuarioForUpdateDTO>> findUsuarioForUpdate(@PathVariable("id") Long id){
        return new ResponseEntity<>(usuarioService.findUsuarioForUpdate(id), HttpStatus.OK);
    }

    /**
     * Exclui permanentemente um usuário do sistema
     * Operação irreversível - remove todos os dados do usuário
     *
     * @param id ID do usuário a ser excluído (obrigatório)
     * @return HTTP 200 (OK) em caso de sucesso
     * @throws UsuarioNotFoundException se o usuário não for encontrado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable("id") Long id){
        usuarioService.deleteUsuario(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Atualiza apenas o nome de um usuário existente
     * Valida se o novo nome atende aos requisitos mínimos
     *
     * @param dto DTO contendo ID do usuário e novo nome
     * @return HTTP 200 (OK) em caso de sucesso
     * @throws UsuarioNotFoundException se o usuário não for encontrado
     * @throws UpdateNameException se o nome for nulo ou inválido
     */
    @PatchMapping("/name")
    public ResponseEntity<Void> updateNameUsuario(@RequestBody @Valid UpdateNameUsuarioDTO dto){
        usuarioService.updateNameUsuario(dto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Atualiza apenas a data de nascimento de um usuário
     * Valida se a data é anterior à data atual
     *
     * @param dto DTO contendo ID do usuário e nova data de nascimento
     * @return HTTP 200 (OK) em caso de sucesso
     * @throws UsuarioNotFoundException se o usuário não for encontrado
     * @throws UpdateBirthDateException se a data for nula ou futura
     */
    @PatchMapping("/birthDate")
    public ResponseEntity<Void> updateBirthDateUsuario(@RequestBody @Valid UpdateBirthDateUsuarioDTO dto){
        usuarioService.updateBirthDateUsuario(dto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Atualiza o endereço completo do usuário consultando novo CEP
     * Realiza nova consulta ao ViaCEP para validar e completar endereço
     *
     * @param dto DTO contendo ID do usuário, novo CEP e número do endereço
     * @return HTTP 200 (OK) em caso de sucesso
     * @throws UsuarioNotFoundException se o usuário não for encontrado
     * @throws CepNotFoundException se o novo CEP não for encontrado
     * @throws CepServiceException se ocorrer um erro na requisição
     */
    @PatchMapping("/address")
    public ResponseEntity<Void> updateAddressUsuario(@RequestBody @Valid UpdateAddressUsuarioDTO dto){
        usuarioService.updateAddressUsuario(dto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Atualiza o document (cpf/id) do usuário com validação de unicidade
     * Verifica se o novo documento não pertence a outro usuário
     *
     * @param dto DTO contendo ID do usuário e novo documento
     * @return HTTP 200 (OK) em caso de sucesso
     * @throws UsuarioNotFoundException se o usuário não for encontrado
     * @throws UpdateDocumentException se o documento for nulo ou inválido
     */
    @PatchMapping("/document")
    public ResponseEntity<Void> updateDocumentUsuario(@RequestBody @Valid UpdateDocumentUsuarioDTO dto){
        usuarioService.updateDocumentUsuario(dto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Atualiza todos os dados do usuário em uma única operação
     * Combina validações de documento, CEP e dados básicos
     *
     * @param dto DTO com todos os campos atualizáveis do usuário
     * @return HTTP 200 (OK) em caso de sucesso
     * @throws UsuarioNotFoundException se o usuário não for encontrado
     * @throws CepNotFoundException se o novo CEP não for encontrado
     * @throws CepServiceException se ocorrer um erro na requisição
     */
    @PutMapping
    public ResponseEntity<Void> updateUsuario(@RequestBody @Valid UpdateUsuarioDTO dto){
        usuarioService.updateUsuario(dto);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
