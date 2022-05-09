package kg.banksystem.deliverybackend.dto.admin.response;

import lombok.Data;

import java.util.Map;

@Data
public class BranchStatisticResponseDTO {
    private String branchName;
    private String branchAddress;
    private Long totalCountOfOrdersActive;
    private Long totalCountOfOrdersComplete;
    private Long totalCountOfCouriersActive;
    private Long totalCountOfCouriersInactive;
    private Long countOfCompletedOrdersPerWeek;
    private Long countOfCompletedOrdersPerMonth;
    private Long countOfCompletedOrdersPerYear;

    public static BranchStatisticResponseDTO statisticsData(Map<String, Object> statistics) {
        BranchStatisticResponseDTO branchStatisticResponseDTO = new BranchStatisticResponseDTO();
        branchStatisticResponseDTO.setBranchName(statistics.get("branchName").toString());
        branchStatisticResponseDTO.setBranchAddress(statistics.get("branchAddress").toString());
        branchStatisticResponseDTO.setTotalCountOfOrdersActive(Long.valueOf(statistics.get("totalCountOfOrdersActive").toString()));
        branchStatisticResponseDTO.setTotalCountOfOrdersComplete(Long.valueOf(statistics.get("totalCountOfOrdersComplete").toString()));
        branchStatisticResponseDTO.setTotalCountOfCouriersActive(Long.valueOf(statistics.get("totalCountOfCouriersActive").toString()));
        branchStatisticResponseDTO.setTotalCountOfCouriersInactive(Long.valueOf(statistics.get("totalCountOfCouriersInactive").toString()));
        branchStatisticResponseDTO.setCountOfCompletedOrdersPerWeek(Long.valueOf(statistics.get("countOfCompletedOrdersPerWeek").toString()));
        branchStatisticResponseDTO.setCountOfCompletedOrdersPerMonth(Long.valueOf(statistics.get("countOfCompletedOrdersPerMonth").toString()));
        branchStatisticResponseDTO.setCountOfCompletedOrdersPerYear(Long.valueOf(statistics.get("countOfCompletedOrdersPerYear").toString()));
        return branchStatisticResponseDTO;
    }
}