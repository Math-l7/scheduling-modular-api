package com.example.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.book.appointment.dto.AppointmentInputDTO;
import com.example.book.appointment.dto.AppointmentReturnDTO;
import com.example.book.appointment.enums.AppointmentStatus;
import com.example.book.appointment.model.Appointment;
import com.example.book.appointment.policy.AppointmentValidator;
import com.example.book.appointment.repository.AppointmentRepository;
import com.example.book.appointment.service.AppointmentService;
import com.example.book.business.enums.BusinessType;
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

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentValidator appointmentValidator;
    @Mock
    private BusinessRepository businessRepository;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private ServiceCatalogRepository serviceCatalogRepository;
    @Mock
    private WorkingHoursRepository workingHoursRepository;
    @Mock
    private LoggedUserService loggedUserService;

    @InjectMocks
    private AppointmentService appointmentService;

    private User commonClient;
    private User commonUserStaff;
    private Business commonBusiness;
    private Staff commonStaff;
    private ServiceCatalog commonService;
    private WorkingHours commonWorkingHours;
    private Appointment commonAppointment;
    private AppointmentInputDTO appointmentInputDTO;
    private User userWrong;

    @BeforeEach
    void setUp() {
        userWrong = new User("lala", "lala", "lala");

        commonBusiness = new Business("Barbearia Profissional", BusinessType.BARBER);
        commonBusiness.setId(1);
        commonBusiness.setActive(true);

        commonClient = new User("Matheus Cliente", "math@email.com", "senha123");
        commonClient.setId(2);
        commonClient.setRole(UserRoleEnum.CLIENT);

        commonUserStaff = new User("Thiago Barbeiro", "thiago@barbeiro.com", "senha123");
        commonUserStaff.setId(3);
        commonUserStaff.setRole(UserRoleEnum.STAFF);

        commonStaff = new Staff(commonUserStaff, "Thiago Boss", commonBusiness);
        commonStaff.setId(4);
        commonStaff.setActive(true);

        commonService = new ServiceCatalog("Corte", 30, new BigDecimal("60.00"), commonBusiness);
        commonService.setId(100);

        LocalDateTime startTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);

        commonWorkingHours = new WorkingHours(startTime.getDayOfWeek(),
                java.time.LocalTime.of(8, 0), java.time.LocalTime.of(18, 0), commonBusiness);

        commonAppointment = new Appointment(commonBusiness, commonService, commonStaff, commonClient, startTime,
                startTime);

        appointmentInputDTO = new AppointmentInputDTO(commonBusiness.getId(), commonStaff.getId(),
                commonService.getId(), startTime);

    }

    @Test
    public void createAppointmentTest_Success() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        when(staffRepository.findById(commonStaff.getId())).thenReturn(Optional.of(commonStaff));
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.of(commonService));
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        when(workingHoursRepository.findByBusinessIdAndDayOfWeek(commonBusiness.getId(), any()))
                .thenReturn(Optional.of(commonWorkingHours));
        when(appointmentRepository.findByStaffIdAndStaffActiveTrue(commonStaff.getId())).thenReturn(List.of());

        AppointmentReturnDTO appointmentReturn = appointmentService.createAppointment(appointmentInputDTO);

        assertEquals(AppointmentStatus.SCHEDULED, appointmentReturn.getStatus());
        verify(appointmentRepository).save(any());
        verify(appointmentValidator).validateCreation(any());
    }

    @Test
    public void createAppointmentTest_BusinessNotFound() {
        when(businessRepository.findById(appointmentInputDTO.getBusinessId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> appointmentService.createAppointment(appointmentInputDTO));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    public void createAppointmentTest_StaffNotFound() {
        when(businessRepository.findById(anyInt())).thenReturn(Optional.of(commonBusiness));
        when(staffRepository.findById(appointmentInputDTO.getStaffId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> appointmentService.createAppointment(appointmentInputDTO));

    }

    @Test
    public void createAppointmentTest_ServiceNotFound() {
        when(businessRepository.findById(anyInt())).thenReturn(Optional.of(commonBusiness));
        when(staffRepository.findById(anyInt())).thenReturn(Optional.of(commonStaff));
        when(serviceCatalogRepository.findById(appointmentInputDTO.getServiceId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> appointmentService.createAppointment(appointmentInputDTO));

    }

    @Test
    public void createAppointmentTest_WorkingHoursNotFound() {
        when(businessRepository.findById(anyInt())).thenReturn(Optional.of(commonBusiness));
        when(staffRepository.findById(anyInt())).thenReturn(Optional.of(commonStaff));
        when(serviceCatalogRepository.findById(anyInt())).thenReturn(Optional.of(commonService));
        when(loggedUserService.get()).thenReturn(commonClient);
        when(workingHoursRepository.findByBusinessIdAndDayOfWeek(anyInt(), any())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> appointmentService.createAppointment(appointmentInputDTO));
    }

    @Test
    public void createAppointmentTest_ValidatorFails() {
        when(businessRepository.findById(anyInt())).thenReturn(Optional.of(commonBusiness));
        when(staffRepository.findById(anyInt())).thenReturn(Optional.of(commonStaff));
        when(serviceCatalogRepository.findById(anyInt())).thenReturn(Optional.of(commonService));
        when(loggedUserService.get()).thenReturn(commonClient);
        when(workingHoursRepository.findByBusinessIdAndDayOfWeek(anyInt(), any()))
                .thenReturn(Optional.of(commonWorkingHours));

        doThrow(new BusinessException("Horário indisponível"))
                .when(appointmentValidator).validateCreation(any());

        assertThrows(BusinessException.class,
                () -> appointmentService.createAppointment(appointmentInputDTO));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    public void cancelAppointmentTest_Success() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((commonAppointment.getStaff().getUser()));

        appointmentService.cancelAppointment(commonAppointment.getId());
        verify(appointmentRepository).save(commonAppointment);
    }

    @Test
    public void cancelAppointmentTest_WhenAppointmentNotFound() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> appointmentService.cancelAppointment(commonAppointment.getId()));
    }

    @Test
    public void cancelAppointmentTest_WhenUserNotAuthorized() {

        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((userWrong));

        assertThrows(BusinessException.class, () -> appointmentService.cancelAppointment(commonAppointment.getId()));
    }

    @Test
    public void cancelAppointmentTest_WhenStaffNotAuthorized() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((commonUserStaff));

        assertThrows(BusinessException.class, () -> appointmentService.cancelAppointment(commonAppointment.getId()));
    }

    @Test
    public void cancelAppointmentTest_WhenAppointmentIsntScheduled() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((commonAppointment.getStaff().getUser()));

        commonAppointment.setStatus(AppointmentStatus.COMPLETED);
        assertThrows(BusinessException.class, () -> appointmentService.cancelAppointment(commonAppointment.getId()));
    }

    @Test
    public void completeAppointmentTest_Success() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((commonAppointment.getStaff().getUser()));

        appointmentService.completeAppointment(commonAppointment.getId());
        verify(appointmentRepository).save(any());
    }

    @Test
    public void completeAppointmentTest_WhenAppointmentNotFound() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> appointmentService.completeAppointment(commonAppointment.getId()));
    }

    @Test
    public void completeAppointmentTest_WhenNotAuthorized() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((commonClient));

        assertThrows(BusinessException.class, () -> appointmentService.completeAppointment(commonAppointment.getId()));
    }

    @Test
    public void completeAPpointmentTest_WhenStaffNotAuthorized() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((commonUserStaff));

        assertThrows(BusinessException.class, () -> appointmentService.completeAppointment(commonAppointment.getId()));
    }

    @Test
    public void completeAppointmentTest_WhenAppointmentIsntScheduled() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((commonAppointment.getStaff().getUser()));

        commonAppointment.setStatus(AppointmentStatus.CANCELED);
    }

    @Test
    public void getAppointmentByIdTest_Success() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((commonClient));

        AppointmentReturnDTO appointmentReturn = appointmentService.getAppointmentById(commonAppointment.getId());

        assertEquals(commonAppointment.getService().getId(), appointmentReturn.getServiceId());
        assertEquals(commonAppointment.getStaff().getId(), appointmentReturn.getStaffId());
    }

    @Test
    public void getAppointmentByIdTest_WhenAppointmentNotFound() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> appointmentService.getAppointmentById(commonAppointment.getId()));
    }

    @Test
    public void getAppointmentByIdTest_WhenNotAuthorized() {
        when(appointmentRepository.findById(commonAppointment.getId())).thenReturn(Optional.of(commonAppointment));
        when(loggedUserService.get()).thenReturn((userWrong));

        assertThrows(BusinessException.class, () -> appointmentService.getAppointmentById(commonAppointment.getId()));
    }

    @Test
    public void listByBusinessTest_Success() {
        when(loggedUserService.get()).thenReturn(commonUserStaff);
        when(staffRepository.findById(commonUserStaff.getId())).thenReturn(Optional.of(commonStaff));
        when(appointmentRepository.findByBusinessId(commonBusiness.getId())).thenReturn(List.of(commonAppointment));

        List<AppointmentReturnDTO> appointmentsReturn = appointmentService.listByBusiness(commonBusiness.getId());
        assertEquals(1, appointmentsReturn.size());
        assertEquals(commonAppointment.getService().getId(), appointmentsReturn.get(0).getServiceId());
        assertEquals(commonAppointment.getBusiness().getName(), appointmentsReturn.get(0).getBusinessName());
    }

    @Test
    public void listByBusinessTest_WhenStaffNotFound() {
        when(staffRepository.findById(commonUserStaff.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> appointmentService.listByBusiness(commonBusiness.getId()));
    }

    @Test
    public void listByBusinessTest_WhenStaffNotAuthorized() {
        when(loggedUserService.get()).thenReturn(commonUserStaff);
        when(staffRepository.findById(commonUserStaff.getId())).thenReturn(Optional.of(commonStaff));
        Business otherBusiness = new Business();
        otherBusiness.setId(999);
        commonStaff.setBusiness(otherBusiness);

        assertThrows(BusinessException.class, () -> appointmentService.listByBusiness(commonBusiness.getId()));
    }

    @Test
    public void listByStaffTest_Success() {
        when(loggedUserService.get()).thenReturn(commonUserStaff);
        when(staffRepository.findById(commonUserStaff.getId())).thenReturn(Optional.of(commonStaff));
        when(appointmentRepository.findByStaffId(commonStaff.getId())).thenReturn(List.of(commonAppointment));

        List<AppointmentReturnDTO> result = appointmentService.listByStaff();

        assertEquals(1, result.size());
        verify(appointmentRepository).findByStaffId(commonStaff.getId());
    }

    @Test
    public void listByStaffTest_WhenStaffNotFound() {
        when(loggedUserService.get()).thenReturn(commonUserStaff);
        when(staffRepository.findById(commonUserStaff.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> appointmentService.listByStaff());

    }

    @Test
    public void listByClientTest_Success() {
        when(loggedUserService.get()).thenReturn(commonClient);
        when(appointmentRepository.findByClientId(commonClient.getId())).thenReturn(List.of(commonAppointment));

        List<AppointmentReturnDTO> result = appointmentService.listByClient();

        assertEquals(1, result.size());
        verify(appointmentRepository).findByClientId(commonClient.getId());
    }

}
