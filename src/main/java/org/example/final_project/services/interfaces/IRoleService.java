package org.example.final_project.services.interfaces;

import org.example.final_project.dtos.requests.RoleRequestDTO;
import org.example.final_project.dtos.responses.RoleResponseDTO;

import java.util.List;

public interface IRoleService {
    RoleResponseDTO createRole(RoleRequestDTO request);
    RoleResponseDTO getRoleById(Long id);
    List<RoleResponseDTO> getAllRoles();
    RoleResponseDTO updateRole(Long id, RoleRequestDTO request);
    void deleteRole(Long id);
}
