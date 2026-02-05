package com.example.book.appointment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.book.appointment.dto.AppointmentInputDTO;
import com.example.book.appointment.dto.AppointmentReturnDTO;
import com.example.book.appointment.enums.AppointmentStatus;
import com.example.book.appointment.model.Appointment;
import com.example.book.appointment.policy.AppointmentContext;
import com.example.book.appointment.policy.AppointmentValidator;
import com.example.book.appointment.repository.AppointmentRepository;
import com.example.book.business.model.Business;
import com.example.book.business.model.WorkingHours;
import com.example.book.business.repository.BusinessRepository;
import com.example.book.business.repository.WorkingHoursRepository;
import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.security.LoggedUserService;
import com.example.book.servicecatalog.model.ServiceCatalog;
import com.example.book.servicecatalog.repository.ServiceCatalogRepository;
import com.example.book.staff.model.Staff;
import com.example.book.staff.repository.StaffRepository;
import com.example.book.user.enums.UserRoleEnum;
import com.example.book.user.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentValidator appointmentValidator;
    private final BusinessRepository businessRepository;
    private final StaffRepository staffRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;
    private final WorkingHoursRepository workingHoursRepository;
    private final LoggedUserService loggedUserService;

    private AppointmentReturnDTO toDto(Appointment appointment) {
        AppointmentReturnDTO dto = new AppointmentReturnDTO();

        dto.setAppointmentId(appointment.getId());
        dto.setBusinessId(appointment.getBusiness().getId());
        dto.setServiceId(appointment.getService().getId());
        dto.setClientId(appointment.getClient().getId());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setStatus(appointment.getStatus());

        return dto;
    }

    public AppointmentReturnDTO createAppointment(AppointmentInputDTO input) {
        Business business = businessRepository.findById(input.getBusinessId())
                .orElseThrow(() -> new BusinessException("ID inválido."));
        Staff staff = staffRepository.findById(input.getStaffId())
                .orElseThrow(() -> new BusinessException("ID inválido."));
        ServiceCatalog serviceCatalog = serviceCatalogRepository.findById(input.getServiceId())
                .orElseThrow(() -> new BusinessException("ID inválido."));
        User client = loggedUserService.get();

        LocalDateTime start = input.getStartTime();
        LocalDateTime end = start.plusMinutes(serviceCatalog.getDuration());

        WorkingHours workingHours = workingHoursRepository.findByBusinessIdAndDayOfWeek(business.getId(),
                start.getDayOfWeek()).orElseThrow(() -> new BusinessException("ID inválido."));

        List<Appointment> staffAppointments = appointmentRepository.findByStaffIdAndStaffActiveTrue(staff.getId());

        AppointmentContext context = new AppointmentContext(
                business, staff, serviceCatalog, start, end, workingHours, staffAppointments);

        appointmentValidator.validateCreation(context);

        Appointment appointment = new Appointment(business, serviceCatalog, staff, client, AppointmentStatus.SCHEDULED,
                start, end);
        appointmentRepository.save(appointment);
        return toDto(appointment);
    }

    public void cancelAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("ID inválido."));

        User userLogged = loggedUserService.get();

        if (!appointment.getClient().getId().equals(userLogged.getId())
                && !userLogged.getRole().equals(UserRoleEnum.STAFF)) {
            throw new BusinessException("Não autorizado.");
        }

        if (userLogged.getRole().equals(UserRoleEnum.STAFF)
                && !appointment.getStaff().getId().equals(userLogged.getId())) {
            throw new BusinessException("Não autorizado.");
        }

        if (!appointment.getStatus().equals(AppointmentStatus.SCHEDULED)) {
            throw new BusinessException("Não é possível cancelar esse agendamento.");
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);
    }

    public void completeAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("ID inválido."));

        User logged = loggedUserService.get();

        if (!logged.getRole().equals(UserRoleEnum.STAFF)) {
            throw new BusinessException("Não autorizado.");
        }

        if (!appointment.getStaff().getId().equals(logged.getId())) {
            throw new BusinessException("Não autorizado.");
        }

        if (!appointment.getStatus().equals(AppointmentStatus.SCHEDULED)) {
            throw new BusinessException("Não autorizado.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }

    public AppointmentReturnDTO getAppointmentById(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("ID inválido."));

        User user = loggedUserService.get();

        if (user.getRole().equals(UserRoleEnum.CLIENT) && !appointment.getClient().getId().equals(user.getId())) {
            throw new BusinessException("Acesso negado.");
        }

        return toDto(appointment);
    }

    public List<AppointmentReturnDTO> listByBusiness(Integer businessId) {
        User logged = loggedUserService.get();

        if (logged.getRole().equals(UserRoleEnum.STAFF)) {
            Staff staff = staffRepository.findById(logged.getId())
                    .orElseThrow(() -> new BusinessException("ID inválido."));

            if (!staff.getBusiness().getId().equals(businessId)) {
                throw new BusinessException("Acesso negado.");
            }
        }

        List<Appointment> appointments = appointmentRepository.findByBusinessId(businessId);

        return appointments.stream().map(a -> toDto(a)).toList();
    }

    public List<AppointmentReturnDTO> listByStaff() {

        User logged = loggedUserService.get();

        Staff staff = staffRepository.findById(logged.getId())
                .orElseThrow(() -> new BusinessException("Staff inválido."));

        List<Appointment> appointments = appointmentRepository.findByStaffId(staff.getId());

        return appointments.stream().map(a -> toDto(a)).toList();

    }

    public List<AppointmentReturnDTO> listByClient() {
        User logged = loggedUserService.get();
        List<Appointment> appointments = appointmentRepository.findByClientId(logged.getId());

        return appointments.stream().map(a -> toDto(a)).toList();
    }

}
