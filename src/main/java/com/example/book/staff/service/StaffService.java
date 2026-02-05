package com.example.book.staff.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.book.business.model.Business;
import com.example.book.business.repository.BusinessRepository;
import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.exceptions.ResourceNotFoundException;
import com.example.book.staff.dto.StaffInputDTO;
import com.example.book.staff.dto.StaffReturnDTO;
import com.example.book.staff.model.Staff;
import com.example.book.staff.repository.StaffRepository;
import com.example.book.user.enums.UserRoleEnum;
import com.example.book.user.model.User;
import com.example.book.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    private StaffReturnDTO toDto(Staff staff) {
        return new StaffReturnDTO(staff.getId(), staff.getPublicName(), staff.isActive(), staff.getBusiness().getId());
    }

    // depois da camada appointment, alterar e utilizar metodo do
    // AppointmentRepository. if
    // (appointmentRepository.existsFutureByStaffId(staff.getId())) { ... }
    private void staffHasFutureAppointments(Staff staff) {

        if (staff.getAppointments().size() > 0) {
            throw new BusinessException("Staff possui agendamentos futuros.");
        }
    }

    public StaffReturnDTO createStaff(StaffInputDTO input) {

        Business business = businessRepository.findById(input.getBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("ID inválido."));

        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("ID inválido."));

        if (staffRepository.existsByUserIdAndBusinessId(user.getId(), business.getId())) {
            throw new BusinessException("Usuário já é staff deste business.");
        }

        if (user.getRole() != UserRoleEnum.STAFF) {
            throw new BusinessException("Usuário não possui role STAFF.");
        }

        if (!business.isActive()) {
            throw new BusinessException("Business desativado");
        }

        if (staffRepository.existsByPublicNameAndBusinessId(input.getPublicName(), input.getBusinessId())) {
            throw new BusinessException("Este business já possui um staff com o mesmo nome.");
        }

        Staff staff = new Staff(user, input.getPublicName(), business);

        staffRepository.save(staff);
        return toDto(staff);
    }

    public StaffReturnDTO findById(Integer id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ID inválido."));
        return toDto(staff);
    }

    public StaffReturnDTO findByIdAndBusinessId(Integer staffId, Integer businessId) {
        Staff staff = staffRepository.findByIdAndBusinessId(staffId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("ID inválido."));
        return toDto(staff);
    }

    public List<StaffReturnDTO> listActiveStaffByBusiness(Integer id) {
        return staffRepository.findByBusinessIdAndActiveTrue(id).stream().map(s -> toDto(s)).toList();
    }

    public boolean belongsToBusiness(Integer staffId, Integer businessId) {
        return staffRepository.existsByIdAndBusinessId(staffId, businessId);
    }

    public void activateStaff(Integer id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID inválido."));
        staff.setActive(true);
    }

    public void deactivateStaff(Integer id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID inválido."));

        staffHasFutureAppointments(staff);
        staff.setActive(false);
    }

}
