package kg.banksystem.deliverybackend.entity.response;

import kg.banksystem.deliverybackend.enums.RestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {

    private String message;
    private Object data;
    private RestStatus status;
}