package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.dto.bank.request.ClientRequestDTO;
import kg.banksystem.deliverybackend.entity.ClientEntity;
import kg.banksystem.deliverybackend.repository.ClientRepository;
import kg.banksystem.deliverybackend.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<ClientEntity> getAllClients(int page) {
        Page<ClientEntity> clientEntities = clientRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
        log.info("{} clients found.", clientEntities.getContent().size());
        return clientEntities.getContent();
    }

    @Override
    public ClientEntity getClientById(Long clientId) {
        ClientEntity client = clientRepository.findById(clientId).orElse(null);
        if (client == null) {
            log.error("No client found by clientId: {}.", clientId);
            return null;
        } else {
            log.info("Client: {} successfully found by clientId: {}.", client, clientId);
            return client;
        }
    }

    @Override
    public boolean addClient(ClientRequestDTO clientRequestDTO) {
        try {
            ClientEntity clientEntity = new ClientEntity(clientRequestDTO.getClientPin(), clientRequestDTO.getClientFullName(), clientRequestDTO.getClientPhoneNumber());
            clientEntity.setCreatedDate(LocalDateTime.now());
            clientEntity.setUpdatedDate(LocalDateTime.now());
            clientRepository.save(clientEntity);
            log.info("Client: {} was successfully added.", clientRequestDTO.getClientFullName());
            return true;
        } catch (Exception ex) {
            log.error("Client: {} was not added.", clientRequestDTO.getClientFullName());
            System.out.println(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean editClient(ClientRequestDTO clientRequestDTO) {
        ClientEntity clientEntity = getClientById(clientRequestDTO.getId());
        if (clientEntity == null) {
            log.error("Client with clientId: {} was not found.", clientRequestDTO.getId());
            return false;
        } else {
            try {
                clientEntity.setClientPin(clientRequestDTO.getClientPin());
                clientEntity.setClientFullName(clientRequestDTO.getClientFullName());
                clientEntity.setClientPhoneNumber(clientRequestDTO.getClientPhoneNumber());
                clientEntity.setUpdatedDate(LocalDateTime.now());
                clientRepository.save(clientEntity);
                log.info("Client: {} was successfully updated.", clientRequestDTO.getClientFullName());
                return true;
            } catch (Exception ex) {
                log.error("Client: {} was not updated.", clientRequestDTO.getClientFullName());
                System.out.println(ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public boolean deleteClient(ClientRequestDTO clientRequestDTO) {
        ClientEntity clientEntity = getClientById(clientRequestDTO.getId());
        if (clientEntity == null) {
            log.error("Client with clientId: {} was not found.", clientRequestDTO.getId());
            return false;
        } else {
            try {
                clientEntity.setUpdatedDate(LocalDateTime.now());
                clientEntity.setDeletedDate(LocalDateTime.now());
                clientEntity.setDeleted(true);
                clientRepository.save(clientEntity);
                log.info("Client with clientId: {} was successfully deleted. It can be viewed in the database.", clientRequestDTO.getId());
                return true;
            } catch (Exception ex) {
                log.error("Client with clientId: {} was not deleted.", clientRequestDTO.getId());
                System.out.println(ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public int clientPageCalculation(int page) {
        Page<ClientEntity> clientEntities = clientRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
        return clientEntities.getTotalPages();
    }

    @Override
    public List<ClientEntity> getClients() {
        return clientRepository.findAll().stream().filter(client -> !client.isDeleted()).collect(Collectors.toList());
    }
}