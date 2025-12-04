package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.dto.request.EmpresaRequestDto;
import com.academy.academymanager.dto.response.EmpresaResponseDto;
import com.academy.academymanager.service.EmpresaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for Empresa operations.
 * 
 * Demonstrates:
 * - Query resolvers
 * - Mutation resolvers for CRUD
 * - Authorization at resolver level
 */
@Controller
@RequiredArgsConstructor
public class EmpresaResolver {
    private final EmpresaService empresaService;
    /**
     * Query to get a single empresa by ID.
     * 
     * Example GraphQL query:
     * {
     *   empresa(id: "1") {
     *     cif
     *     nombreLegal
     *     direccionFiscal
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public EmpresaResponseDto empresa(@Argument final Long id) {
        return empresaService.findById(id);
    }
    /**
     * Query to get all empresas.
     * 
     * Example GraphQL query:
     * {
     *   empresas {
     *     cif
     *     nombreLegal
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
    public List<EmpresaResponseDto> empresas() {
        return empresaService.findAll();
    }
    /**
     * Mutation to create a new empresa.
     * 
     * Example GraphQL mutation:
     * mutation {
     *   createEmpresa(input: {
     *     cif: "B12345678"
     *     nombreLegal: "Academia S.L."
     *     direccionFiscal: "Calle Principal 1"
     *   }) {
     *     idEmpresa
     *     cif
     *     nombreLegal
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public EmpresaResponseDto createEmpresa(@Argument final EmpresaInput input) {
        EmpresaRequestDto requestDto = EmpresaRequestDto.builder()
                .cif(input.getCif())
                .nombreLegal(input.getNombreLegal())
                .direccionFiscal(input.getDireccionFiscal())
                .activo(input.getActivo() != null ? input.getActivo() : true)
                .build();
        return empresaService.create(requestDto);
    }
    /**
     * Mutation to update an existing empresa.
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public EmpresaResponseDto updateEmpresa(
            @Argument final Long id,
            @Argument final EmpresaInput input
    ) {
        EmpresaRequestDto requestDto = EmpresaRequestDto.builder()
                .cif(input.getCif())
                .nombreLegal(input.getNombreLegal())
                .direccionFiscal(input.getDireccionFiscal())
                .activo(input.getActivo())
                .build();
        return empresaService.update(id, requestDto);
    }
    /**
     * Mutation to delete an empresa.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteEmpresa(@Argument final Long id) {
        empresaService.delete(id);
        return true;
    }
    /**
     * Input type for Empresa mutations.
     * Maps to the EmpresaInput type defined in schema.graphqls.
     */
    @Data
    public static class EmpresaInput {
        private String cif;
        private String nombreLegal;
        private String direccionFiscal;
        private Boolean activo;
    }
}

