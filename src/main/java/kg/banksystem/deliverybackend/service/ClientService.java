package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.entity.Client;

import java.util.List;

public interface ClientService {

    // in progress
    List<Client> getAllClients();

    // in progress
    Client getClientById();

    // in progress
    Client addClient(Client client);

    // in progress
    Client editClient(Client client);

    // in progress
    void deleteClient(Long id);
}