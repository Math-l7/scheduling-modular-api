package com.example.book.staff.access;

import org.springframework.stereotype.Service;

import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.security.LoggedUserService;
import com.example.book.staff.repository.StaffRepository;
import com.example.book.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffAccessService {

    private final StaffRepository staffRepository;
    private final LoggedUserService loggedUserService;

    public void validateStaffAccessToBusiness(Integer businessId) {
        User user = loggedUserService.get();

        if (!staffRepository.existsByUserIdAndBusinessId(user.getId(), businessId)) {
            throw new BusinessException("Acesso negado ao business.");
        }
    }

}
