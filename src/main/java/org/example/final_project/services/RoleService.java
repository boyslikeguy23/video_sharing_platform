package org.example.final_project.services;

import lombok.RequiredArgsConstructor;
import org.example.final_project.dtos.requests.RoleRequestDTO;
import org.example.final_project.dtos.responses.RoleResponseDTO;
import org.example.final_project.entities.Role;
import org.example.final_project.exceptions.ResourceNotFoundException;
import org.example.final_project.repositories.RoleRepository;
import org.example.final_project.services.interfaces.IRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO request) {
        Role role = Role.builder()
                .name(request.getName().toUpperCase())
                .build();
        return toResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        return toResponse(role);
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        role.setName(request.getName().toUpperCase());
        return toResponse(roleRepository.save(role));
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        roleRepository.delete(role);
    }

    private RoleResponseDTO toResponse(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}