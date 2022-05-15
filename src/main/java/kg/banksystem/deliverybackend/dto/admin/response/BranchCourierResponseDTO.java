package kg.banksystem.deliverybackend.dto.admin.response;

import lombok.Data;

import java.util.Map;

@Data
public class BranchCourierResponseDTO {
    private String courierFullName;
    private String courierPhoneNumber;
    private Long countOfCompletedOrdersPerDay;
    private Long countOfCompletedOrdersPerWeek;
    private Long countOfCompletedOrdersPerMonth;
    private String lastOrderDate;

    public static BranchCourierResponseDTO courierData(Map<String, Object> statistics) {
        BranchCourierResponseDTO branchCourierResponseDTO = new BranchCourierResponseDTO();
        branchCourierResponseDTO.setCourierFullName(statistics.get("courierFullName").toString());
        branchCourierResponseDTO.setCourierPhoneNumber(statistics.get("courierPhoneNumber").toString());
        branchCourierResponseDTO.setCountOfCompletedOrdersPerDay(Long.valueOf(statistics.get("countOfCompletedOrdersPerDay").toString()));
        branchCourierResponseDTO.setCountOfCompletedOrdersPerWeek(Long.valueOf(statistics.get("countOfCompletedOrdersPerWeek").toString()));
        branchCourierResponseDTO.setCountOfCompletedOrdersPerMonth(Long.valueOf(statistics.get("countOfCompletedOrdersPerMonth").toString()));
        branchCourierResponseDTO.setLastOrderDate(statistics.get("lastOrderDate").toString());
        return branchCourierResponseDTO;
    }
}