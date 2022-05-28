package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.dto.bank.request.ClientRequestDTO;
import kg.banksystem.deliverybackend.entity.ClientEntity;

import java.util.List;

public interface ClientService {

    List<ClientEntity> getAllClients(int page);

    ClientEntity getClientById(Long clientId);

    boolean addClient(ClientRequestDTO clientRequestDTO);

    boolean editClient(ClientRequestDTO clientRequestDTO);

    boolean deleteClient(ClientRequestDTO clientRequestDTO);

    int clientPageCalculation(int page);

    List<ClientEntity> getClients();
}