package com.example.book.business.service;

import org.springframework.stereotype.Service;

import com.example.book.business.dto.BusinessInputDTO;
import com.example.book.business.dto.BusinessReturnDTO;
import com.example.book.business.dto.WorkingHoursInputDTO;
import com.example.book.business.dto.WorkingHoursReturnDTO;
import com.example.book.business.model.Business;
import com.example.book.business.model.WorkingHours;
import com.example.book.business.repository.BusinessRepository;
import com.example.book.business.repository.WorkingHoursRepository;
import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.exceptions.ResourceNotFoundException;
import com.example.book.staff.access.StaffAccessService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BusinessService {

    // Após conclusão da camada Staff, incluir validação de Business if(staff) nos
    // metodos de update e get

    private final BusinessRepository businessRepository;
    private final WorkingHoursRepository workingHoursRepository;
    private final StaffAccessService staffAccessService;

    private BusinessReturnDTO toDTO(Business business) {
        return new BusinessReturnDTO(
                business.getId(),
                business.getName(),
                business.getType().name(),
                business.isActive());
    }

    public BusinessReturnDTO createBusiness(BusinessInputDTO input) {
        if (businessRepository.existsByName(input.getName())) {
            throw new BusinessException("Nome já existente.");
        }

        Business business = new Business(input.getName(), input.getType());

        businessRepository.save(business);

        return toDTO(business);
    }

    public BusinessReturnDTO getBusinessById(Integer id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business não encontrado."));

        staffAccessService.validateStaffAccessToBusiness(id);

        return toDTO(business);
    }

    public BusinessReturnDTO getBusinessByName(String name) {
        Business business = businessRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Business não encontrado."));

        staffAccessService.validateStaffAccessToBusiness(business.getId());

        return toDTO(business);
    }

    public WorkingHoursReturnDTO updateWorkingHours(WorkingHoursInputDTO input) {
        Business business = businessRepository.findById(input.getBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business não encontrado."));

        staffAccessService.validateStaffAccessToBusiness(business.getId());

        if (!input.getStartTime().isBefore(input.getEndTime())) {
            throw new BusinessException("Horário inicial deve ser antes do final.");
        }

        WorkingHours workingHours = workingHoursRepository
                .findByBusinessIdAndDayOfWeek(business.getId(), input.getDayOfWeek())
                .orElseGet(() -> new WorkingHours(input.getDayOfWeek(), input.getStartTime(), input.getEndTime(),
                        business));

        workingHours.setStartTime(input.getStartTime());
        workingHours.setEndTime(input.getEndTime());

        workingHoursRepository.save(workingHours);

        return new WorkingHoursReturnDTO(workingHours.getId(), workingHours.getDayOfWeek(), workingHours.getStartTime(),
                workingHours.getEndTime(), business.getId());
    }

    public void desactivateBusiness(Integer businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business não encontrado."));

        business.setActive(false);
        businessRepository.save(business);
    }

    public void activateBusiness(Integer businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business não encontrado."));

        business.setActive(true);
        businessRepository.save(business);
    }

}
