package com.example.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.book.appointment.repository.AppointmentRepository;
import com.example.book.business.enums.BusinessType;
import com.example.book.business.model.Business;
import com.example.book.business.repository.BusinessRepository;
import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.exceptions.ResourceNotFoundException;
import com.example.book.servicecatalog.dto.ServiceCatalogInputDTO;
import com.example.book.servicecatalog.dto.ServiceCatalogReturnDTO;
import com.example.book.servicecatalog.dto.ServiceCatalogUpdateDTO;
import com.example.book.servicecatalog.model.ServiceCatalog;
import com.example.book.servicecatalog.repository.ServiceCatalogRepository;
import com.example.book.servicecatalog.service.ServiceCatalogService;
import com.example.book.staff.access.StaffAccessService;

@ExtendWith(MockitoExtension.class)
public class ServiceCatalogServiceTest {

    @Mock
    private ServiceCatalogRepository serviceCatalogRepository;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private StaffAccessService staffAccessService;

    @InjectMocks
    private ServiceCatalogService serviceCatalogService;

    private Business commonBusiness;
    private ServiceCatalog commonService;
    private ServiceCatalogInputDTO serviceInputDTO;
    private ServiceCatalogUpdateDTO serviceUpdateDTO;

    @BeforeEach
    void setUp() {
        // 1. Setup do Business
        commonBusiness = new Business("Barbearia do Matheus", BusinessType.BARBER);
        commonBusiness.setId(1);
        commonBusiness.setActive(true);

        // 2. Setup do Serviço (ServiceCatalog)
        commonService = new ServiceCatalog("Corte Degradê", 30, new BigDecimal("50.00"), commonBusiness);
        commonService.setId(50);
        commonService.setActivate(true);

        // 3. DTO de Entrada (Criação)
        serviceInputDTO = new ServiceCatalogInputDTO("Corte Degradê", 30, new BigDecimal("50.00"), 1);

        // 4. DTO de Update
        serviceUpdateDTO = new ServiceCatalogUpdateDTO("Corte e Barba", 60, new BigDecimal("80.00"));

        // 5. Comportamento Global de Acesso (Staff)
        // Como o service valida acesso em quase tudo, deixamos passar por padrão
        lenient().doNothing().when(staffAccessService).validateStaffAccessToBusiness(anyInt());
    }

    @Test
    public void createServiceCatalogTest_Success() {
        when(serviceCatalogRepository.existsByNameAndBusinessId(serviceInputDTO.getName(),
                serviceInputDTO.getBusinessId())).thenReturn(false);
        when(businessRepository.findById(serviceInputDTO.getBusinessId())).thenReturn(Optional.of(commonBusiness));

        ServiceCatalogReturnDTO serviceReturn = serviceCatalogService.createServiceCatalog(serviceInputDTO);

        assertEquals(serviceInputDTO.getName(), serviceReturn.getName());
        assertEquals(serviceInputDTO.getDuration(), serviceReturn.getDuration());
        verify(serviceCatalogRepository).save(any());
    }

    @Test
    public void createServiceCatalogTest_WhenServiceAlreadyCreated() {
        when(serviceCatalogRepository.existsByNameAndBusinessId(serviceInputDTO.getName(),
                serviceInputDTO.getBusinessId())).thenReturn(true);

        assertThrows(BusinessException.class, () -> serviceCatalogService.createServiceCatalog(serviceInputDTO));
    }

    @Test
    public void createServiceCatalogTest_WhenDurationIsWrong() {
        when(serviceCatalogRepository.existsByNameAndBusinessId(serviceInputDTO.getName(),
                serviceInputDTO.getBusinessId())).thenReturn(true);
        serviceInputDTO.setDuration(0);

        assertThrows(BusinessException.class, () -> serviceCatalogService.createServiceCatalog(serviceInputDTO));
    }

    @Test
    public void createServiceCatalogTest_WhenBusinessNotFound() {
        when(serviceCatalogRepository.existsByNameAndBusinessId(serviceInputDTO.getName(),
                serviceInputDTO.getBusinessId())).thenReturn(false);
        when(businessRepository.findById(serviceInputDTO.getBusinessId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> serviceCatalogService.createServiceCatalog(serviceInputDTO));
    }

    @Test
    public void findByBusinessTest_Success() {
        when(businessRepository.findByName(commonBusiness.getName())).thenReturn(Optional.of(commonBusiness));
        when(serviceCatalogRepository.findByBusinessId(commonBusiness.getId())).thenReturn(List.of(commonService));

        List<ServiceCatalogReturnDTO> serviceReturn = serviceCatalogService.findByBusiness(commonBusiness.getName());

        assertEquals(1, serviceReturn.size());
        assertEquals(commonService.getName(), serviceReturn.get(0).getName());
    }

    @Test
    public void findByBusinessTest_WhenBusinessNotFound() {
        when(businessRepository.findByName(serviceInputDTO.getName())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceCatalogService.findByBusiness(commonBusiness.getName()));
    }

    @Test
    public void findByIdTest_Success() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.of(commonService));

        ServiceCatalogReturnDTO serviceReturn = serviceCatalogService.findById(commonService.getId());

        assertEquals(commonService.getName(), serviceReturn.getName());
        assertEquals(commonService.getDuration(), serviceReturn.getDuration());
    }

    @Test
    public void findByIdTest_WhenNotFound() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceCatalogService.findById(commonService.getId()));
    }

    @Test
    public void updateServiceCatalogTest_Success() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.of(commonService));
        when(serviceCatalogRepository.existsByNameAndBusinessId(serviceUpdateDTO.getName(), commonBusiness.getId()))
                .thenReturn(false);

        ServiceCatalogReturnDTO serviceReturn = serviceCatalogService.updateServiceCatalog(commonService.getId(),
                serviceUpdateDTO);

        assertEquals(serviceUpdateDTO.getName(), serviceReturn.getName());
        assertEquals(serviceUpdateDTO.getDurationMinutes(), serviceReturn.getDuration());
        assertEquals(serviceUpdateDTO.getPrice(), serviceReturn.getPrice());

        verify(serviceCatalogRepository).save(any());
    }

    @Test
    public void updateServiceCatalogTest_WhenPriceIsInvalid() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.of(commonService));

        serviceUpdateDTO.setPrice(new BigDecimal("-10.00"));

        assertThrows(BusinessException.class,
                () -> serviceCatalogService.updateServiceCatalog(commonService.getId(), serviceUpdateDTO));

    }

    @Test
    public void updateServiceCatalogTest_WhenNameAlreadyExists() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.of(commonService));
        when(serviceCatalogRepository.existsByNameAndBusinessId(serviceUpdateDTO.getName(), commonBusiness.getId()))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> serviceCatalogService.updateServiceCatalog(commonService.getId(), serviceUpdateDTO));

    }

    @Test
    public void updateServiceCatalogTest_WhenServiceNotFound() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceCatalogService.updateServiceCatalog(commonService.getId(), serviceUpdateDTO));
    }

    @Test
    public void desactivateServiceCatalogTest_Success() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.of(commonService));
        when(appointmentRepository.existsByServiceId(commonService.getId())).thenReturn(false);

        commonService.setActivate(true);
        serviceCatalogService.desactivateServiceCatalog(commonService.getId());
        assertFalse(commonService.isActivate());
        verify(serviceCatalogRepository).save(any());
    }

    @Test
    public void desactivateServiceCatalogTest_WhenNotFound() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceCatalogService.desactivateServiceCatalog(commonService.getId()));
    }

    @Test
    public void desactivateServiceCatalogTest_WhenStaffHasAppointments() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.of(commonService));
        when(appointmentRepository.existsByServiceId(commonService.getId())).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> serviceCatalogService.desactivateServiceCatalog(commonService.getId()));
    }

    @Test
    public void activateServiceCatalogTest_Success() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.of(commonService));

        commonService.setActivate(false);
        serviceCatalogService.activateServiceCatalog(commonService.getId());
        assertTrue(commonService.isActivate());
        verify(serviceCatalogRepository).save(any());
    }

    @Test
    public void activateServiceCatalogTest_WhenNotFound() {
        when(serviceCatalogRepository.findById(commonService.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceCatalogService.activateServiceCatalog(commonService.getId()));
    }

}
