package com.example.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.book.business.dto.BusinessInputDTO;
import com.example.book.business.dto.BusinessReturnDTO;
import com.example.book.business.dto.WorkingHoursInputDTO;
import com.example.book.business.dto.WorkingHoursReturnDTO;
import com.example.book.business.enums.BusinessType;
import com.example.book.business.model.Business;
import com.example.book.business.model.WorkingHours;
import com.example.book.business.repository.BusinessRepository;
import com.example.book.business.repository.WorkingHoursRepository;
import com.example.book.business.service.BusinessService;
import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.exceptions.ResourceNotFoundException;
import com.example.book.staff.access.StaffAccessService;

@ExtendWith(MockitoExtension.class)
public class BusinessServiceTest {

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private WorkingHoursRepository workingHoursRepository;

    @Mock
    private StaffAccessService staffAccessService;

    @InjectMocks
    private BusinessService businessService;

    private Business commonBusiness;
    private BusinessInputDTO businessInput;
    private WorkingHoursInputDTO workingHoursInput;
    private WorkingHours commonWorkingHours;

    @BeforeEach
    void setUp() {
        // 1. Instância de Business padrão
        commonBusiness = new Business("Barbearia do Matheus", BusinessType.BARBER);
        commonBusiness.setId(1);
        commonBusiness.setActive(true);

        // 2. DTO de entrada para criação
        businessInput = new BusinessInputDTO("Barbearia do Matheus", BusinessType.BARBER);

        // 3. Setup de Horário de Trabalho (WorkingHours)
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(18, 0);

        workingHoursInput = new WorkingHoursInputDTO(DayOfWeek.MONDAY, start, end, 1);

        commonWorkingHours = new WorkingHours(DayOfWeek.MONDAY, start, end, commonBusiness);
        commonWorkingHours.setId(10);

        // 4. Comportamento Global do StaffAccess
        // Por padrão, não faz nada (deixa o acesso ser validado com sucesso)
        lenient().doNothing().when(staffAccessService).validateStaffAccessToBusiness(anyInt());
    }

    @Test
    public void createBusinessTest_Success() {
        when(businessRepository.existsByName(businessInput.getName())).thenReturn(false);

        BusinessReturnDTO businessReturn = businessService.createBusiness(businessInput);

        assertEquals(businessInput.getName(), businessReturn.getName());
        verify(businessRepository).save(any());
    }

    @Test
    public void createBusinessTest_WhenNameAlreadyExists() {
        when(businessRepository.existsByName(businessInput.getName())).thenReturn(true);

        assertThrows(BusinessException.class, () -> businessService.createBusiness(businessInput));
    }

    @Test
    public void getBusinessByIdTest_Success() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));

        BusinessReturnDTO businessReturn = businessService.getBusinessById(commonBusiness.getId());

        assertEquals(commonBusiness.getName(), businessReturn.getName());
        assertEquals(commonBusiness.getType().name(), businessReturn.getType());
    }

    @Test
    public void getBusinessByIdTest_WhenNotFound() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> businessService.getBusinessById(commonBusiness.getId()));
    }

    @Test
    public void getBusinessByNameTest_Success() {
        when(businessRepository.findByName(commonBusiness.getName())).thenReturn(Optional.of(commonBusiness));

        BusinessReturnDTO businessReturn = businessService.getBusinessByName(commonBusiness.getName());

        assertEquals(commonBusiness.getId(), businessReturn.getId());
        assertEquals(commonBusiness.getName(), businessReturn.getName());
    }

    @Test
    public void getBusinessByNameTest_WhenNotFound() {
        when(businessRepository.findByName(commonBusiness.getName())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> businessService.getBusinessByName(commonBusiness.getName()));
    }

    @Test
    public void updateWorkingHoursTest_Succcess() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        when(workingHoursRepository.findByBusinessIdAndDayOfWeek(commonBusiness.getId(),
                workingHoursInput.getDayOfWeek())).thenReturn(Optional.of(commonWorkingHours));

        WorkingHoursReturnDTO workingHoursReturn = businessService.updateWorkingHours(workingHoursInput);

        assertEquals(workingHoursInput.getDayOfWeek(), workingHoursReturn.getDayOfWeek());
        assertEquals(workingHoursInput.getStartTime(), workingHoursReturn.getStartTime());
        assertEquals(workingHoursInput.getEndTime(), workingHoursReturn.getEndTime());
        verify(workingHoursRepository).save(any());
    }

    @Test
    public void updateWorkingHoursTest_WhenBusinessNotFound() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> businessService.updateWorkingHours(workingHoursInput));
    }

    @Test
    public void updateWorkingHoursTest_WhenTimeIsWrong() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        WorkingHoursInputDTO invalidInput = new WorkingHoursInputDTO(
                DayOfWeek.MONDAY,
                LocalTime.of(20, 0),
                LocalTime.of(18, 0),
                1);
        assertThrows(BusinessException.class, () -> businessService.updateWorkingHours(invalidInput));
    }

    @Test
    public void desactivateBusinessTest_Success() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        businessService.desactivateBusiness(commonBusiness.getId());

        assertEquals(false, commonBusiness.isActive());
        verify(businessRepository).save(any());
    }

    @Test
    public void desactivateBusinessTest_WhenNotFound() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> businessService.desactivateBusiness(commonBusiness.getId()));
    }

    @Test
    public void activateBusinessTest_Success() {
        when(businessRepository.findById(commonBusiness.getId())).thenReturn(Optional.of(commonBusiness));
        commonBusiness.setActive(false);
        businessService.activateBusiness(commonBusiness.getId());

        assertEquals(true, commonBusiness.isActive());
        verify(businessRepository).save(any());
    }
}
