package org.example.globaledugroup.service;

import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.dto.UserFunDto;
import org.example.globaledugroup.entity.FunUser;
import org.example.globaledugroup.mapper.FunUserMapper;
import org.example.globaledugroup.repository.FunUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FunUserService {
    private final FunUserRepository funUserRepository;
    private final FunUserMapper funUserMapper;

    public void save(UserFunDto userFunDto) {
        FunUser entity = funUserMapper.toEntity(userFunDto);
        FunUser save = funUserRepository.save(entity);
        System.out.println(save);
    }

    public List<FunUser> findByPhone(String phone) {
        return funUserRepository.findByPhone(phone);
    }

    public List<FunUser> findByName(String name) {
        return funUserRepository.findByFullName(name);
    }

    public List<FunUser> searchFunUser(String text) {
        return funUserRepository.searchUser(text);
    }
}