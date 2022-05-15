package kg.banksystem.deliverybackend.dto.admin.response;

import lombok.Data;

import java.util.Map;

@Data
public class BranchReportResponseDTO {
    private String orderNumber;
    private String addressPickup;
    private String addressDelivery;
    private String typeDelivery;
    private String orderStatus;
    private String orderCreated;
    private String cardType;
    private String cardDescription;
    private String clientPin;
    private String clientFullName;
    private String clientPhoneNumber;
    private String completedStoryNumber;
    private String userFullName;
    private String userPhoneNumber;
    private String userEmail;
    private String orderCompleted;
    private String storyStatus;
    private String storyComment;
    private String branchName;
    private String branchAddress;

    public static BranchReportResponseDTO reportData(Map<String, Object> report) {
        BranchReportResponseDTO branchReportResponseDTO = new BranchReportResponseDTO();
        branchReportResponseDTO.setOrderNumber(report.get("orderNumber").toString());
        branchReportResponseDTO.setAddressPickup(report.get("addressPickup").toString());
        branchReportResponseDTO.setAddressDelivery(report.get("addressDelivery").toString());

        switch (report.get("typeDelivery").toString()) {
            case "PICKUP": branchReportResponseDTO.setTypeDelivery("Самовывоз"); break;
            case "COURIER_DELIVERY": branchReportResponseDTO.setTypeDelivery("Доставка курьером"); break;
            default: branchReportResponseDTO.setTypeDelivery("Тип не указан"); break;
        }

        switch (report.get("orderStatus").toString()) {
            case "NEW_ORDER": branchReportResponseDTO.setOrderStatus("Новый заказ"); break;
            case "IN_PROCESS": branchReportResponseDTO.setOrderStatus("В обработке"); break;
            case "SENT_TO_FILIAL": branchReportResponseDTO.setOrderStatus("Отправлен в филиал"); break;
            case "READY_FROM_DELIVERY": branchReportResponseDTO.setOrderStatus("Готов к выдаче"); break;
            case "TAKEN_BY_COURIER": branchReportResponseDTO.setOrderStatus("Заказ взят курьером"); break;
            case "HANDED_OVER_TO_THE_COURIER": branchReportResponseDTO.setOrderStatus("Заказ передан курьеру"); break;
            case "RECEIVED_BY_CLIENT": branchReportResponseDTO.setOrderStatus("Получен клиентом"); break;
            case "DESTROYED": branchReportResponseDTO.setOrderStatus("Карта уничтожена"); break;
            default: branchReportResponseDTO.setOrderStatus("Статус не указан"); break;
        }

        branchReportResponseDTO.setOrderCreated(report.get("orderCreated").toString());
        branchReportResponseDTO.setCardType(report.get("cardType").toString());
        branchReportResponseDTO.setCardDescription(report.get("cardDescription").toString());
        branchReportResponseDTO.setClientPin(report.get("clientPin").toString());
        branchReportResponseDTO.setClientFullName(report.get("clientFullName").toString());
        branchReportResponseDTO.setClientPhoneNumber(report.get("clientPhoneNumber").toString());
        branchReportResponseDTO.setCompletedStoryNumber(report.get("completedStoryNumber").toString());
        branchReportResponseDTO.setUserFullName(report.get("userFullName").toString());
        branchReportResponseDTO.setUserPhoneNumber(report.get("userPhoneNumber").toString());
        branchReportResponseDTO.setUserEmail(report.get("userEmail").toString());
        branchReportResponseDTO.setOrderCompleted(report.get("orderCompleted").toString());

        switch (report.get("storyStatus").toString()) {
            case "IDENTIFICATION_CLIENT_ERROR": branchReportResponseDTO.setStoryStatus("Ошибка идентификации клиента"); break;
            case "UNABLE_TO_FIND_CLIENT": branchReportResponseDTO.setStoryStatus("Клиент не найден"); break;
            case "SUCCESSFUL_DELIVERY": branchReportResponseDTO.setStoryStatus("Успешная доставка карты и передача клиенту"); break;
            default: branchReportResponseDTO.setStoryStatus("Статус не указан"); break;
        }

        branchReportResponseDTO.setStoryComment(report.get("storyComment").toString());
        branchReportResponseDTO.setBranchName(report.get("branchName").toString());
        branchReportResponseDTO.setBranchAddress(report.get("branchAddress").toString());
        return branchReportResponseDTO;
    }
}