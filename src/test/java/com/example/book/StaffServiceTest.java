package com.example.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.book.business.enums.BusinessType;
import com.example.book.business.model.Business;
import com.example.book.business.repository.BusinessRepository;
import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.exceptions.ResourceNotFoundException;
import com.example.book.staff.dto.StaffInputDTO;
import com.example.book.staff.dto.StaffReturnDTO;
import com.example.book.staff.model.Staff;
import com.example.book.staff.repository.StaffRepository;
import com.example.book.staff.service.StaffService;
import com.example.book.user.enums.UserRoleEnum;
import com.example.book.user.model.User;
import com.example.book.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StaffService staffService;

    private User commonUser;
    private Business commonBusiness;
    private Staff commonStaff;
    private StaffInputDTO staffInputDTO;

    @BeforeEach
    void setUp() {
        // 1. Setup do Usuário (precisa ser ROLE_STAFF para passar na sua validação)
        commonUser = new User("Matheus Staff", "staff@barba.com", "senha123");
        commonUser.setId(10);
        commonUser.setRole(UserRoleEnum.STAFF);

        // 2. Setup do Business
        commonBusiness = new Business("Barbearia Premium", BusinessType.BARBER);
        commonBusiness.setId(1);
        commonBusiness.setActive(true);

        // 3. Setup do Staff (Vinculando User e Business)
        commonStaff = new Staff(commonUser, "Matheus Barbeiro", commonBusiness);
        commonStaff.setId(100);
        commonStaff.setActive(true);
        // Inicializa a lista de agendamentos como vazia para não quebrar o
        // deactivateStaff
        commonStaff.setAppointments(new java.util.ArrayList<>());

        // 4. DTO de entrada para os testes de criação
        staffInputDTO = new StaffInputDTO(1, "Matheus Barbeiro", 10); // businessId, publicName, userId
    }

    @Test
    public void createStaffTest_Success() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));
        when(staffRepository.existsByUserIdAndBusinessId(commonUser.getId(), commonBusiness.getId())).thenReturn(false);
        when(staffRepository.existsByPublicNameAndBusinessId(commonUser.getName(), commonBusiness.getId()))
                .thenReturn(false);

        StaffReturnDTO staffReturn = staffService.createStaff(staffInputDTO);

        assertEquals(staffInputDTO.getPublicName(), staffReturn.getPublicName());
        verify(staffRepository).save(any());
    }

    @Test
    public void createStaffTest_WhenBusinessNotFound() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> staffService.createStaff(staffInputDTO));
    }

    @Test
    public void createStaffTest_WhenUserNotFound() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> staffService.createStaff(staffInputDTO));
    }

    @Test
    public void createStaffTest_WhenUserAlreadyStaff() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));
        when(staffRepository.existsByUserIdAndBusinessId(commonUser.getId(), commonBusiness.getId())).thenReturn(true);

        assertThrows(BusinessException.class, () -> staffService.createStaff(staffInputDTO));

    }

    @Test
    public void createStaffTest_WhenUserIsntStaff() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));
        when(staffRepository.existsByUserIdAndBusinessId(commonUser.getId(), commonBusiness.getId())).thenReturn(false);
        commonUser.setRole(UserRoleEnum.CLIENT);

        assertThrows(BusinessException.class, () -> staffService.createStaff(staffInputDTO));
    }

    @Test
    public void createStaffTest_WhenBusinessDesactive() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));
        when(staffRepository.existsByUserIdAndBusinessId(commonUser.getId(), commonBusiness.getId())).thenReturn(false);

        commonBusiness.setActive(false);
        assertThrows(BusinessException.class, () -> staffService.createStaff(staffInputDTO));

    }

    @Test
    public void createStaffTest_WhenBusinessAlreadyHasStaffWithName() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));
        when(staffRepository.existsByUserIdAndBusinessId(commonUser.getId(), commonBusiness.getId())).thenReturn(false);
        when(staffRepository.existsByPublicNameAndBusinessId(commonUser.getName(), commonBusiness.getId()))
                .thenReturn(true);

        assertThrows(BusinessException.class, () -> staffService.createStaff(staffInputDTO));
    }

    @Test
    public void findByIdTest_Success() {
        when(staffRepository.findById(commonStaff.getId())).thenReturn(Optional.of(commonStaff));

        StaffReturnDTO staffReturn = staffService.findById(commonStaff.getId());

        assertEquals(commonStaff.getPublicName(), staffReturn.getPublicName());
    }

    @Test
    public void findByIdTest_WhenNotFound() {
        when(staffRepository.findById(commonStaff.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> staffService.findById(commonStaff.getId()));
    }

    @Test
    public void findByIdAndBusinessIdTest_Success() {
        when(staffRepository.findByIdAndBusinessId(commonStaff.getId(), commonBusiness.getId()))
                .thenReturn(Optional.of(commonStaff));

        StaffReturnDTO staffReturn = staffService.findByIdAndBusinessId(commonStaff.getId(), commonBusiness.getId());

        assertEquals(commonStaff.getPublicName(), staffReturn.getPublicName());
    }

    @Test
    public void findByIdAndBusinessIdTest_WhenNotFound() {
        when(staffRepository.findByIdAndBusinessId(commonStaff.getId(), commonBusiness.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> staffService.findByIdAndBusinessId(commonStaff.getId(), commonBusiness.getId()));
    }

    @Test
    public void listActivateStaffByBusinessTest_Success() {
        when(staffRepository.findByBusinessIdAndActiveTrue(commonBusiness.getId())).thenReturn(List.of(commonStaff));

        List<StaffReturnDTO> staffReturn = staffService.listActiveStaffByBusiness(commonBusiness.getId());
        assertEquals(1, staffReturn.size());
        assertEquals(commonStaff.getPublicName(), staffReturn.get(0).getPublicName());

    }

    @Test
    public void belongToBusinessTest_Success() {
        when(staffRepository.existsByIdAndBusinessId(commonStaff.getId(), commonBusiness.getId())).thenReturn(true);

        boolean resultReturn = staffService.belongsToBusiness(commonStaff.getId(), commonBusiness.getId());

        assertTrue(resultReturn);
    }

    @Test
    public void activateStaffTest_Success() {
        when(staffRepository.findById(commonStaff.getId())).thenReturn(Optional.of(commonStaff));
        commonStaff.setActive(false);
        staffService.activateStaff(commonStaff.getId());

        assertTrue(commonStaff.isActive());
    }

    @Test
    public void activateStaffTest_WhenNotFound() {
        when(staffRepository.findById(commonStaff.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> staffService.activateStaff(commonStaff.getId()));
    }

    @Test
    public void desactivateStaffTest_Success() {
        when(staffRepository.findById(commonStaff.getId())).thenReturn(Optional.of(commonStaff));
        commonStaff.setActive(true);
        staffService.deactivateStaff(commonStaff.getId());

        assertFalse(commonStaff.isActive());
    }

    @Test
    public void desactivateStaffTest_WhenNotFond() {
        when(staffRepository.findById(commonStaff.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> staffService.deactivateStaff(commonStaff.getId()));
    }

}
