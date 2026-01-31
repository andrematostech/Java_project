package org.estg.controller;

import org.estg.dto.MembersDTO;
import org.estg.service.MembersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MembersController Tests")
class MembersControllerTest {
    
    private static final Logger log = LoggerFactory.getLogger(MembersControllerTest.class);
    
    @Mock
    private MembersService membersService;
    
    @InjectMocks
    private MembersController controller;
    
    private MembersDTO memberDTO;
    private MembersDTO savedMemberDTO;
    
    @BeforeEach
    void setUp() {
        log.info("═══════════════════════════════════════");
        log.info("🔧 Iniciando Setup do Teste Controller");
        log.info("═══════════════════════════════════════");
        
        // Request DTO
        memberDTO = new MembersDTO();
        memberDTO.setFullName("Jose Soares");
        memberDTO.setEmail("jose.soares@gymhub.com");
        memberDTO.setPhoneNumber("8240047");
        memberDTO.setDateOfBirth(LocalDate.of(1990, 5, 15));
        memberDTO.setExperienceLevel("INTERMEDIARIO");
        
        // Response DTO (com ID gerado)
        savedMemberDTO = new MembersDTO();
        savedMemberDTO.setId("member-001");
        savedMemberDTO.setFullName("Jose Soares");
        savedMemberDTO.setEmail("jose.soares@gymhub.com");
        savedMemberDTO.setPhoneNumber("8240047");
        savedMemberDTO.setDateOfBirth(LocalDate.of(1990, 5, 15));
        savedMemberDTO.setExperienceLevel("INTERMEDIARIO");
        savedMemberDTO.setStatus("ACTIVE");
        
        log.info("✅ DTOs criados para testes");
    }
    
    @Test
    @DisplayName("POST /api/members/register - Deve retornar 201 CREATED ao registrar membro")
    void shouldReturn201WhenRegisteringMember() {
        log.info("\n🧪 TEST: shouldReturn201WhenRegisteringMember");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Mock do Service");
        when(membersService.registerMember(any(MembersDTO.class))).thenReturn(savedMemberDTO);
        log.info("  ✓ Mock membersService.registerMember() → retorna Member com ID");
        
        // ACT
        log.info("\n⚡ ACT - Executando controller.registerMember()");
        ResponseEntity<MembersDTO> response = controller.registerMember(memberDTO);
        log.info("  ✓ Controller executado");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resposta HTTP");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        log.info("  ✓ Status HTTP: {} (CREATED)", response.getStatusCode().value());
        
        assertNotNull(response.getBody());
        log.info("  ✓ Body NOT NULL");
        
        MembersDTO body = java.util.Objects.requireNonNull(response.getBody());
        assertEquals("member-001", body.getId());
        log.info("  ✓ Member ID: {}", body.getId());
        
        assertEquals("Jose Soares", body.getFullName());
        log.info("  ✓ Nome: {}", body.getFullName());
        
        assertEquals("ACTIVE", body.getStatus());
        log.info("  ✓ Status: {}", body.getStatus());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando chamada ao Service");
        verify(membersService, times(1)).registerMember(any(MembersDTO.class));
        log.info("  ✓ membersService.registerMember() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
    
    @Test
    @DisplayName("PUT /api/members/{id}/update - Deve retornar 200 OK ao atualizar membro")
    void shouldReturn200WhenUpdatingMember() {
        log.info("\n🧪 TEST: shouldReturn200WhenUpdatingMember");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Mock do Service");
        MembersDTO updateDTO = new MembersDTO();
        updateDTO.setFullName("Andre Matos");
        updateDTO.setPhoneNumber("8240047");
        
        MembersDTO updatedDTO = new MembersDTO();
        updatedDTO.setId("member-001");
        updatedDTO.setFullName("Andre Matos");
        updatedDTO.setPhoneNumber("8240047");
        updatedDTO.setStatus("ACTIVE");
        
        when(membersService.updateMember(eq("member-001"), any(MembersDTO.class))).thenReturn(updatedDTO);
        log.info("  ✓ Mock membersService.updateMember() → retorna Member atualizado");
        
        // ACT
        log.info("\n⚡ ACT - Executando controller.updateMember()");
        ResponseEntity<MembersDTO> response = controller.updateMember("member-001", updateDTO);
        log.info("  ✓ Controller executado");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resposta HTTP");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        log.info("  ✓ Status HTTP: {} (OK)", response.getStatusCode().value());
        
        assertNotNull(response.getBody());
        log.info("  ✓ Body NOT NULL");
        
        MembersDTO body = java.util.Objects.requireNonNull(response.getBody());
        assertEquals("Andre Matos", body.getFullName());
        log.info("  ✓ Nome atualizado: {}", body.getFullName());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando chamada ao Service");
        verify(membersService, times(1)).updateMember(eq("member-001"), any(MembersDTO.class));
        log.info("  ✓ membersService.updateMember() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
    
    @Test
    @DisplayName("POST /api/members/{id}/activate - Deve retornar 200 OK ao ativar membro")
    void shouldReturn200WhenActivatingMember() {
        log.info("\n🧪 TEST: shouldReturn200WhenActivatingMember");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Mock do Service");
        MembersDTO activatedDTO = new MembersDTO();
        activatedDTO.setId("member-001");
        activatedDTO.setFullName("Jose Soares");
        activatedDTO.setStatus("ACTIVE");
        
        when(membersService.activateMember("member-001")).thenReturn(activatedDTO);
        log.info("  ✓ Mock membersService.activateMember() → retorna Member ativado");
        
        // ACT
        log.info("\n⚡ ACT - Executando controller.activateMember()");
        ResponseEntity<MembersDTO> response = controller.activateMember("member-001");
        log.info("  ✓ Controller executado");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resposta HTTP");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        log.info("  ✓ Status HTTP: {} (OK)", response.getStatusCode().value());
        
        assertNotNull(response.getBody());
        log.info("  ✓ Body NOT NULL");
        
        MembersDTO body = java.util.Objects.requireNonNull(response.getBody());
        assertEquals("ACTIVE", body.getStatus());
        log.info("  ✓ Status do Member: {}", body.getStatus());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando chamada ao Service");
        verify(membersService, times(1)).activateMember("member-001");
        log.info("  ✓ membersService.activateMember() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
}
