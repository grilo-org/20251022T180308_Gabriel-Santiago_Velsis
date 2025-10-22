package velsis.back.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import velsis.back.api.Endereco;
import velsis.back.api.ViaCep;
import velsis.back.dtos.CreateUsuarioDTO;
import velsis.back.dtos.UpdateAddressUsuarioDTO;
import velsis.back.dtos.UpdateBirthDateUsuarioDTO;
import velsis.back.dtos.UpdateDocumentUsuarioDTO;
import velsis.back.dtos.UpdateNameUsuarioDTO;
import velsis.back.dtos.UpdateUsuarioDTO;
import velsis.back.dtos.UsuarioDTO;
import velsis.back.dtos.UsuarioForUpdateDTO;
import velsis.back.entities.Usuario;
import velsis.back.exceptions.CepNotFoundException;
import velsis.back.exceptions.CepServiceException;
import velsis.back.exceptions.DocumentExistException;
import velsis.back.exceptions.UpdateBirthDateException;
import velsis.back.exceptions.UpdateDocumentException;
import velsis.back.exceptions.UpdateNameException;
import velsis.back.exceptions.UsuarioNotFoundException;
import velsis.back.repositories.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ViaCep viaCep;

    public UsuarioService(UsuarioRepository usuarioRepository, ViaCep viaCep) {
        this.usuarioRepository = usuarioRepository;
        this.viaCep = viaCep;
    }

    /**
     * Cria um novo usuário validando o document (cpf/id) e consultando o cep com ViaCEP
     *
     * @param dto - DTO com dados do usuário
     * @throws DocumentExistException se o document (cpf/id) já estiver sido cadastrado
     * @throws CepNotFoundException se o CEP não for encontrado
     * @throws CepServiceException se houver erro na consulta do ViaCEP
     */
    @Transactional
    public void createUsuario(CreateUsuarioDTO dto) {
        try {
            if(usuarioRepository.existsByDocument(dto.document())){
                throw new DocumentExistException();
            }

            Endereco endereco = viaCep.viaCep(dto.zip());

            if(endereco.localidade() == null || endereco.uf() == null){
                throw new CepNotFoundException(dto.zip());
            }

            Usuario usuario = new Usuario();
            usuario.setName(dto.name());
            usuario.setBirth_date(dto.birth_date());
            usuario.setDocument(dto.document());

            usuario.setAddress_number(dto.address_number());
            usuario.setZip(dto.zip());
            usuario.setAddress_line(endereco.logradouro());
            usuario.setCity(endereco.localidade());
            usuario.setState(endereco.uf());

            usuario.setCreated_at(LocalDate.now());
            usuario.setUpdated_at(LocalDate.now());

            usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new CepServiceException();
        }
    }

    /**
     * Retorna todos os usuários com dados resumidos para otimização
     *
     * @return Lista de usuários contendo nome, data de aniversário, cidade e estado
     */
    public List<UsuarioDTO> findAllUsuarios(){
        return usuarioRepository
                .findAll()
                .stream()
                .map(dto -> new UsuarioDTO(
                        dto.getId(),
                        dto.getName(),
                        dto.getBirth_date(),
                        dto.getCity(),
                        dto.getState()
                ))
                .toList();
    }

    /**
     * Retorna o usuário com dados resumidos para atualização
     *
     * @return O usuários contendo nome, data de aniversário, document, cep e número de endereço
     */
    public List<UsuarioForUpdateDTO> findUsuarioForUpdate(Long id){
        return usuarioRepository
                .findById(id)
                .stream()
                .map(user -> new UsuarioForUpdateDTO(
                        user.getId(),
                        user.getName(),
                        user.getBirth_date(),
                        user.getDocument(),
                        user.getZip(),
                        user.getAddress_number()
                ))
                .toList();
    }

    /**
     * Exclui um usuário após validar sua existência
     *
     * @param id - ID do usuário a ser removido
     * @throws UsuarioNotFoundException quando o usuário não existe
     */
    @Transactional
    public void deleteUsuario(Long id){
        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(UsuarioNotFoundException::new);

        usuarioRepository.delete(usuario);
    }

    /**
     * Atualiza apenas o nome do usuário
     *
     * @param dto para id do usuário a ser atualizado e o novo nome para o usuário
     * @throws UsuarioNotFoundException se usuário não for encontrado
     * @throws UpdateNameException se o novo nome for nulo ou vazio
     */
    @Transactional
    public void updateNameUsuario(UpdateNameUsuarioDTO dto){
        Usuario usuario = usuarioRepository
                .findById(dto.id())
                .orElseThrow(UsuarioNotFoundException::new);

        if(dto.name() == null || dto.name().isEmpty()){
            throw new UpdateNameException();
        }

        usuario.setName(dto.name());
        usuario.setUpdated_at(LocalDate.now());

        usuarioRepository.save(usuario);
    }

    /**
     * Atualiza apenas a data de aniversário do usuário
     *
     * @param dto para id do usuário a ser atualizado e o nova data de aniversário para o usuário
     * @throws UsuarioNotFoundException se usuário não for encontrado
     * @throws UpdateBirthDateException se a nova data de aniversário for nula
     */
    @Transactional
    public void updateBirthDateUsuario(UpdateBirthDateUsuarioDTO dto){
        Usuario usuario = usuarioRepository
                .findById(dto.id())
                .orElseThrow(UsuarioNotFoundException::new);

        if(dto.birth_date() == null){
            throw new UpdateBirthDateException();
        }

        usuario.setBirth_date(dto.birth_date());
        usuario.setUpdated_at(LocalDate.now());

        usuarioRepository.save(usuario);
    }

    /**
     * Atualiza apenas o endereço do usuário
     *
     * @param dto para id do usuário a ser atualizado e o novo endereço para o usuário
     * @throws UsuarioNotFoundException se usuário não for encontrado
     * @throws CepNotFoundException se o CEP não for encontrado
     * @throws CepServiceException se houver erro na consulta do ViaCEP
     */
    @Transactional
    public void updateAddressUsuario(UpdateAddressUsuarioDTO dto){

        Usuario usuario = usuarioRepository
                .findById(dto.id())
                .orElseThrow(UsuarioNotFoundException::new);

        try {
            Endereco endereco = viaCep.viaCep(dto.zip());

            if(endereco.localidade() == null || endereco.uf() == null){
                throw new CepNotFoundException(dto.zip());
            }

            usuario.setZip(dto.zip());
            usuario.setAddress_number(dto.address_number());
            usuario.setAddress_line(endereco.logradouro());
            usuario.setCity(endereco.localidade());
            usuario.setState(endereco.uf());
            usuario.setUpdated_at(LocalDate.now());

        } catch (CepNotFoundException e) {
            throw e;
        } catch (Exception e){
            throw new CepServiceException();
        }
    }

    /**
     * Atualiza apenas o document (cpf/id) do usuário
     *
     * @param dto para id do usuário a ser atualizado e o novo document (cpf/id) para o usuário
     * @throws UsuarioNotFoundException se usuário não for encontrado
     * @throws UpdateDocumentException se o novo document (cpf/id) for nulo
     */
    @Transactional
    public void updateDocumentUsuario(UpdateDocumentUsuarioDTO dto){
        Usuario usuario = usuarioRepository
                .findById(dto.id())
                .orElseThrow(UsuarioNotFoundException::new);

        if(dto.document() == null){
            throw new UpdateDocumentException();
        }

        usuario.setDocument(dto.document());
        usuario.setUpdated_at(LocalDate.now());

        usuarioRepository.save(usuario);
    }

    /**
     * Atualiza todos os dados do usuário
     *
     * @param dto para id do usuário a ser atualizado e os novos dados
     * @throws UsuarioNotFoundException se usuário não for encontrado
     * @throws CepNotFoundException se o CEP não for encontrado
     * @throws CepServiceException se houver erro na consulta do ViaCEP
     */
    @Transactional
    public void updateUsuario(UpdateUsuarioDTO dto){
        Usuario usuario = usuarioRepository
                .findById(dto.id())
                .orElseThrow(UsuarioNotFoundException::new);

        try {
            Endereco endereco = viaCep.viaCep(dto.zip());

            if(endereco.localidade() == null || endereco.uf() == null){
                throw new CepNotFoundException(dto.zip());
            }

            usuario.setName(dto.name());
            usuario.setBirth_date(dto.birth_date());
            usuario.setDocument(dto.document());
            usuario.setZip(dto.zip());
            usuario.setAddress_number(dto.address_number());
            usuario.setAddress_line(endereco.logradouro());
            usuario.setCity(endereco.localidade());
            usuario.setState(endereco.uf());
            usuario.setUpdated_at(LocalDate.now());

        } catch (CepNotFoundException e) {
            throw e;
        } catch (Exception e){
            throw new CepServiceException();
        }
    }
}
