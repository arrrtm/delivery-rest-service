package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.dto.user.request.EditAccountRequestDTO;
import kg.banksystem.deliverybackend.dto.user.request.ResetPasswordRequestDTO;

public interface AccountService {

    boolean editAccount(Long id, EditAccountRequestDTO requestDTO);

    boolean editPassword(Long id, EditAccountRequestDTO requestDTO);

    boolean resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}