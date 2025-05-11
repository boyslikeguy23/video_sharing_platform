package org.example.final_project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
public class Role {
    @Id
    private Integer id;
    private String name;
}
