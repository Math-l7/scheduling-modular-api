package com.example.book.servicecatalog.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.book.appointment.repository.AppointmentRepository;
import com.example.book.business.model.Business;
import com.example.book.business.repository.BusinessRepository;
import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.exceptions.ResourceNotFoundException;
import com.example.book.servicecatalog.dto.ServiceCatalogInputDTO;
import com.example.book.servicecatalog.dto.ServiceCatalogReturnDTO;
import com.example.book.servicecatalog.dto.ServiceCatalogUpdateDTO;
import com.example.book.servicecatalog.model.ServiceCatalog;
import com.example.book.servicecatalog.repository.ServiceCatalogRepository;
import com.example.book.staff.access.StaffAccessService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;
    private final BusinessRepository businessRepository;
    private final AppointmentRepository appointmentRepository;
    private final StaffAccessService staffAccessService;

    private ServiceCatalogReturnDTO toDto(ServiceCatalog input) {
        return new ServiceCatalogReturnDTO(input.getId(), input.getName(), input.getDuration(), input.getPrice(),
                input.getBusiness().getId(), input.isActivate());
    }

    public ServiceCatalogReturnDTO createServiceCatalog(ServiceCatalogInputDTO input) {
        if (serviceCatalogRepository.existsByNameAndBusinessId(input.getName(), input.getBusinessId())) {
            throw new BusinessException("Serviço já criado.");
        }

        staffAccessService.validateStaffAccessToBusiness(input.getBusinessId());

        if (input.getDuration() <= 0 || input.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Duração deve ser maior que zero.");
        }

        Business business = businessRepository.findById(input.getBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business não encontrado."));

        ServiceCatalog serviceCatalog = new ServiceCatalog(input.getName(), input.getDuration(), input.getPrice(),
                business);

        serviceCatalogRepository.save(serviceCatalog);
        return toDto(serviceCatalog);
    }

    public List<ServiceCatalogReturnDTO> findByBusiness(String name) {
        Business business = businessRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Business não encontrado."));

        return serviceCatalogRepository.findByBusinessId(business.getId()).stream().map(s -> toDto(s))
                .filter(f -> f.isActivate()).toList();

    }

    public ServiceCatalogReturnDTO findById(Integer id) {
        ServiceCatalog serviceCatalog = serviceCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado."));

        return toDto(serviceCatalog);
    }

    public ServiceCatalogReturnDTO updateServiceCatalog(Integer serviceId, ServiceCatalogUpdateDTO dto) {
        ServiceCatalog service = serviceCatalogRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        staffAccessService.validateStaffAccessToBusiness(service.getBusiness().getId());

        if (dto.getName() != null) {
            service.setName(dto.getName());
        }

        if (dto.getDurationMinutes() != null) {
            if (dto.getDurationMinutes() <= 0) {
                throw new BusinessException("Duração inválida");
            }
            service.setDuration(dto.getDurationMinutes());
        }

        if (dto.getPrice() != null) {
            if (dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Preço inválido");
            }
            service.setPrice(dto.getPrice());
        }

        if (dto.getName() != null &&
                serviceCatalogRepository.existsByNameAndBusinessId(
                        dto.getName(),
                        service.getBusiness().getId())) {
            throw new BusinessException("Já existe um serviço com esse nome");
        }

        serviceCatalogRepository.save(service);
        return toDto(service);
    }

    public void desactivateServiceCatalog(Integer id) {
        ServiceCatalog service = serviceCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        staffAccessService.validateStaffAccessToBusiness(service.getBusiness().getId());

        if (appointmentRepository.existsByServiceId(id)) {
            throw new BusinessException("Serviço possui agendamentos");
        }

        service.setActivate(false);
        serviceCatalogRepository.save(service);
    }

    public void activateServiceCatalog(Integer id) {
        ServiceCatalog service = serviceCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        staffAccessService.validateStaffAccessToBusiness(service.getBusiness().getId());

        service.setActivate(true);
        serviceCatalogRepository.save(service);
    }

}
