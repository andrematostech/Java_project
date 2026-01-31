package org.estg.service;

import org.estg.data.MembersRepository;
import org.estg.data.SessionRecordRepository;
import org.estg.dto.MembersDTO;
import org.estg.exceptions.MemberNotFoundException;
import org.estg.infrastructure.event.EventPublisher;
import org.estg.model.Members;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.estg.domain.valueobject.Email;

@ExtendWith(MockitoExtension.class)
@DisplayName("MembersService Tests")
class MembersServiceTest {
    
    private static final Logger log = LoggerFactory.getLogger(MembersServiceTest.class);
    
    @Mock
    private MembersRepository memberRepository;
    
    @Mock
    private SessionRecordRepository sessionRecordRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private MembersService service;
    
    private Members mockMember;
    private MembersDTO memberDTO;
    
    @BeforeEach
    void setUp() {
        log.info("═══════════════════════════════════════");
        log.info("🔧 Iniciando Setup do Teste");
        log.info("═══════════════════════════════════════");
        
        // Mock Member - Jose Soares
        mockMember = new Members();
        mockMember.setId("member-001");
        mockMember.setFullName("Jose Soares");
        mockMember.setEmail(new Email("jose.soares@gymhub.com"));
        mockMember.setPhoneNumber(new org.estg.domain.valueobject.PhoneNumber("8240047"));
        mockMember.setDateOfBirth(LocalDate.of(1990, 5, 15));
        mockMember.setExperienceLevel("INTERMEDIARIO");
        mockMember.setStatus(Members.MemberStatus.ACTIVE);
        
        // DTO para requests
        memberDTO = new MembersDTO();
        memberDTO.setFullName("Jose Soares");
        memberDTO.setEmail("jose.soares@gymhub.com");
        memberDTO.setPhoneNumber("8240047");
        memberDTO.setDateOfBirth(LocalDate.of(1990, 5, 15));
        memberDTO.setExperienceLevel("INTERMEDIARIO");
        
        log.info("✅ Mock Member criado: {}", mockMember.getFullName());
    }
    
    @Test
    @DisplayName("Deve registrar novo membro com sucesso")
    void shouldRegisterMemberSuccessfully() {
        log.info("\n🧪 TEST: shouldRegisterMemberSuccessfully");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Mock");
        when(memberRepository.existsByEmailValue(any())).thenReturn(false);
        when(memberRepository.save(any(Members.class))).thenReturn(mockMember);
        doNothing().when(eventPublisher).publish(any());
        log.info("  ✓ Mock repository.save() → retorna Member persistido");
        log.info("  ✓ Mock eventPublisher.publish() → sem retorno");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.registerMember()");
        MembersDTO result = service.registerMember(memberDTO);
        log.info("  ✓ Service executado com sucesso");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resultados");
        assertNotNull(result, "Member não deve ser null");
        log.info("  ✓ Member NOT NULL: {}", result.getFullName());
        
        assertEquals("Jose Soares", result.getFullName());
        log.info("  ✓ Nome correto: {}", result.getFullName());
        
        assertEquals("8240047", result.getPhoneNumber());
        log.info("  ✓ Telefone correto: {}", result.getPhoneNumber());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações com Mock");
        verify(memberRepository, times(1)).save(any(Members.class));
        log.info("  ✓ repository.save() foi chamado exatamente 1 vez");
        
        verify(eventPublisher, times(1)).publish(any());
        log.info("  ✓ eventPublisher.publish() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
    
    @Test
    @DisplayName("Deve buscar Member por ID com sucesso")
    void shouldGetMemberByIdSuccessfully() {
        log.info("\n🧪 TEST: shouldGetMemberByIdSuccessfully");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Mock");
        when(memberRepository.findById("member-001")).thenReturn(Optional.of(mockMember));
        log.info("  ✓ Mock repository.findById('member-001') → retorna Optional com Member");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.getMemberById()");
        MembersDTO result = service.getMemberById("member-001");
        log.info("  ✓ Service executado com sucesso");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resultados");
        assertNotNull(result);
        log.info("  ✓ Member NOT NULL: {}", result.getFullName());
        
        assertEquals("Jose Soares", result.getFullName());
        log.info("  ✓ Nome correto: {}", result.getFullName());
        
        assertEquals("8240047", result.getPhoneNumber());
        log.info("  ✓ Telefone correto: {}", result.getPhoneNumber());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações");
        verify(memberRepository, times(1)).findById("member-001");
        log.info("  ✓ repository.findById() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando Member não existe")
    void shouldThrowExceptionWhenMemberNotFound() {
        log.info("\n🧪 TEST: shouldThrowExceptionWhenMemberNotFound");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Mock para retornar vazio");
        when(memberRepository.findById("invalid-id")).thenReturn(Optional.empty());
        log.info("  ✓ Mock repository.findById('invalid-id') → retorna Optional.empty()");
        
        // ACT & ASSERT
        log.info("\n⚡ ACT & ASSERT - Esperando MemberNotFoundException");
        MemberNotFoundException exception = assertThrows(
            MemberNotFoundException.class,
            () -> service.getMemberById("invalid-id")
        );
        log.info("  ✓ Exceção lançada corretamente: {}", exception.getMessage());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações");
        verify(memberRepository, times(1)).findById("invalid-id");
        log.info("  ✓ repository.findById() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
    
    @Test
    @DisplayName("Deve atualizar Member corretamente")
    void shouldUpdateMemberSuccessfully() {
        log.info("\n🧪 TEST: shouldUpdateMemberSuccessfully");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Criando DTO de atualização");
        MembersDTO updateDTO = new MembersDTO();
        updateDTO.setFullName("Andre Matos");
        updateDTO.setPhoneNumber("8240047");
        log.info("  ✓ DTO criado: {} - {}", updateDTO.getFullName(), updateDTO.getPhoneNumber());
        
        Members updatedMember = new Members();
        updatedMember.setId("member-001");
        updatedMember.setFullName("Andre Matos");
        updatedMember.setPhoneNumber(new org.estg.domain.valueobject.PhoneNumber("8240047"));
        updatedMember.setStatus(Members.MemberStatus.ACTIVE);
        
        when(memberRepository.findById("member-001")).thenReturn(Optional.of(mockMember));
        when(memberRepository.save(any(Members.class))).thenReturn(updatedMember);
        doNothing().when(eventPublisher).publish(any());
        log.info("  ✓ Mock repository.save() → retorna Member atualizado");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.updateMember()");
        MembersDTO result = service.updateMember("member-001", updateDTO);
        log.info("  ✓ Member atualizado com sucesso");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resultados");
        assertEquals("Andre Matos", result.getFullName());
        log.info("  ✓ Nome atualizado: {}", result.getFullName());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações");
        verify(memberRepository, times(1)).save(any(Members.class));
        log.info("  ✓ repository.save() foi chamado exatamente 1 vez");
        
        verify(eventPublisher, times(1)).publish(any());
        log.info("  ✓ eventPublisher.publish() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
    
    @Test
    @DisplayName("Deve ativar Member com sucesso")
    void shouldActivateMemberSuccessfully() {
        log.info("\n🧪 TEST: shouldActivateMemberSuccessfully");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Member suspenso");
        mockMember.setStatus(Members.MemberStatus.SUSPENDED);
        
        Members activatedMember = new Members();
        activatedMember.setId("member-001");
        activatedMember.setFullName("Jose Soares");
        activatedMember.setStatus(Members.MemberStatus.ACTIVE);
        
        when(memberRepository.findById("member-001")).thenReturn(Optional.of(mockMember));
        when(memberRepository.save(any(Members.class))).thenReturn(activatedMember);
        doNothing().when(eventPublisher).publish(any());
        log.info("  ✓ Mock configurado para ativar member");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.activateMember()");
        MembersDTO result = service.activateMember("member-001");
        log.info("  ✓ Member ativado");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resultados");
        assertEquals("ACTIVE", result.getStatus());
        log.info("  ✓ Status correto: {}", result.getStatus());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações");
        verify(memberRepository, times(1)).save(any(Members.class));
        log.info("  ✓ repository.save() foi chamado exatamente 1 vez");
        
        verify(eventPublisher, times(1)).publish(any());
        log.info("  ✓ eventPublisher.publish() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
    
    @Test
    @DisplayName("Deve suspender Member com sucesso")
    void shouldSuspendMemberSuccessfully() {
        log.info("\n🧪 TEST: shouldSuspendMemberSuccessfully");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Member ativo");
        when(memberRepository.findById("member-001")).thenReturn(Optional.of(mockMember));
        when(memberRepository.save(any(Members.class))).thenAnswer(invocation -> {
            Members member = invocation.getArgument(0);
            member.setStatus(Members.MemberStatus.SUSPENDED);
            return member;
        });
        doNothing().when(eventPublisher).publish(any());
        log.info("  ✓ Mock configurado para suspender member");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.suspendMember()");
        service.suspendMember("member-001");
        log.info("  ✓ Member suspenso");
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações");
        verify(memberRepository, times(1)).findById("member-001");
        log.info("  ✓ repository.findById() foi chamado exatamente 1 vez");
        
        verify(memberRepository, times(1)).save(any(Members.class));
        log.info("  ✓ repository.save() foi chamado exatamente 1 vez");
        
        verify(eventPublisher, times(1)).publish(any());
        log.info("  ✓ eventPublisher.publish() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
    
    @Test
    @DisplayName("Deve publicar evento ao registrar Member")
    void shouldPublishEventWhenRegisteringMember() {
        log.info("\n🧪 TEST: shouldPublishEventWhenRegisteringMember");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Mock");
        when(memberRepository.existsByEmailValue(any())).thenReturn(false);
        when(memberRepository.save(any(Members.class))).thenReturn(mockMember);
        doNothing().when(eventPublisher).publish(any());
        log.info("  ✓ Mock repository e eventPublisher configurados");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.registerMember()");
        service.registerMember(memberDTO);
        log.info("  ✓ Member registrado");
        
        // ASSERT & VERIFY
        log.info("\n✔️  ASSERT & VERIFY - Verificando publicação de evento");
        verify(eventPublisher, times(1)).publish(any());
        log.info("  ✓ eventPublisher.publish() foi chamado exatamente 1 vez");
        log.info("  ✓ Evento MemberRegisteredEvent publicado com sucesso");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }

    @Test
    @DisplayName("Deve encerrar Member com sucesso (soft delete)")
    void shouldEncerrarMemberSuccessfully() {
        log.info("\n🧪 TEST: shouldEncerrarMemberSuccessfully");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Configurando Member ativo para encerramento");
        mockMember.setStatus(Members.MemberStatus.ACTIVE);
        
        Members inactiveMember = new Members();
        inactiveMember.setId("member-001");
        inactiveMember.setFullName("Jose Soares");
        inactiveMember.setStatus(Members.MemberStatus.INACTIVE);
        
        when(memberRepository.findById("member-001")).thenReturn(Optional.of(mockMember));
        when(memberRepository.save(any(Members.class))).thenReturn(inactiveMember);
        doNothing().when(eventPublisher).publish(any());
        log.info("  ✓ Mock configurado para encerrar member");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.deactivateMember()");
        MembersDTO result = service.deactivateMember("member-001");
        log.info("  ✓ Member encerrado com sucesso");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resultados");
        assertNotNull(result);
        log.info("  ✓ Member NOT NULL");
        
        assertEquals("INACTIVE", result.getStatus());
        log.info("  ✓ Status atualizado para: {}", result.getStatus());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações");
        verify(memberRepository, times(1)).save(any(Members.class));
        log.info("  ✓ repository.save() foi chamado exatamente 1 vez");
        
        verify(eventPublisher, times(1)).publish(any());
        log.info("  ✓ eventPublisher.publish() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }

    @Test
    @DisplayName("Deve listar Members com paginação com sucesso")
    void shouldListMembersWithPaginationSuccessfully() {
        log.info("\n🧪 TEST: shouldListMembersWithPaginationSuccessfully");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Criando página de Members");
        Members member2 = new Members();
        member2.setId("member-002");
        member2.setFullName("Andre Matos");
        member2.setStatus(Members.MemberStatus.ACTIVE);
        
        java.util.List<Members> membersList = java.util.Arrays.asList(mockMember, member2);
        org.springframework.data.domain.Page<Members> page = 
            new org.springframework.data.domain.PageImpl<>(
                membersList,
                org.springframework.data.domain.PageRequest.of(0, 10),
                2L
            );
        
        when(memberRepository.findByStatus(any(Members.MemberStatus.class), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(page);
        log.info("  ✓ Mock repository.findByStatus() → retorna página com 2 members");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.getMembersByPage()");
        org.springframework.data.domain.Page<MembersDTO> result = 
            service.getMembersByPage(org.springframework.data.domain.PageRequest.of(0, 10));
        log.info("  ✓ Página de members retornada");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resultados");
        assertNotNull(result);
        log.info("  ✓ Page NOT NULL");
        
        assertEquals(2, result.getTotalElements());
        log.info("  ✓ Total de elementos: {}", result.getTotalElements());
        
        assertEquals(1, result.getTotalPages());
        log.info("  ✓ Total de páginas: {}", result.getTotalPages());
        
        assertEquals(2, result.getContent().size());
        log.info("  ✓ Tamanho do conteúdo: {}", result.getContent().size());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações");
        verify(memberRepository, times(1)).findByStatus(any(Members.MemberStatus.class), any(org.springframework.data.domain.Pageable.class));
        log.info("  ✓ repository.findByStatus() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }

    @Test
    @DisplayName("Deve filtrar Members por objetivo de treino com sucesso")
    void shouldFilterMembersByTrainingGoalSuccessfully() {
        log.info("\n🧪 TEST: shouldFilterMembersByTrainingGoalSuccessfully");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Criando Members com objetivo de treino");
        mockMember.setTrainingGoal(new org.estg.domain.valueobject.TrainingGoal("WEIGHT_LOSS"));
        
        Members member2 = new Members();
        member2.setId("member-002");
        member2.setFullName("Andre Matos");
        member2.setTrainingGoal(new org.estg.domain.valueobject.TrainingGoal("WEIGHT_LOSS"));
        member2.setStatus(Members.MemberStatus.ACTIVE);
        
        java.util.List<Members> filteredList = java.util.Arrays.asList(mockMember, member2);
        org.springframework.data.domain.Page<Members> page = 
            new org.springframework.data.domain.PageImpl<>(
                filteredList,
                org.springframework.data.domain.PageRequest.of(0, 10),
                2L
            );
        
        when(memberRepository.findByTrainingGoal(anyString(), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(page);
        log.info("  ✓ Mock repository.findByTrainingGoal() → retorna página filtrada");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.getMembersByTrainingGoal()");
        org.springframework.data.domain.Page<MembersDTO> result = 
            service.getMembersByTrainingGoal("WEIGHT_LOSS", org.springframework.data.domain.PageRequest.of(0, 10));
        log.info("  ✓ Página filtrada retornada");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resultados");
        assertNotNull(result);
        log.info("  ✓ Page NOT NULL");
        
        assertEquals(2, result.getTotalElements());
        log.info("  ✓ Total de elementos filtrados: {}", result.getTotalElements());
        
        assertEquals(2, result.getContent().size());
        log.info("  ✓ Tamanho do conteúdo: {}", result.getContent().size());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações");
        verify(memberRepository, times(1)).findByTrainingGoal(anyString(), any(org.springframework.data.domain.Pageable.class));
        log.info("  ✓ repository.findByTrainingGoal() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }

    @Test
    @DisplayName("Deve retornar sessões do Member com sucesso")
    void shouldGetMemberSessionsSuccessfully() {
        log.info("\n🧪 TEST: shouldGetMemberSessionsSuccessfully");
        log.info("────────────────────────────────────────");
        
        // ARRANGE
        log.info("📋 ARRANGE - Criando histórico de sessões");
        java.util.List<org.estg.model.SessionRecord> sessionsList = new java.util.ArrayList<>();
        
        when(memberRepository.existsById("member-001")).thenReturn(true);
        when(sessionRecordRepository.findByMemberIdOrderBySessionDateTimeDesc("member-001")).thenReturn(sessionsList);
        log.info("  ✓ Mock repository.findByMemberIdOrderBySessionDateTimeDesc() → retorna lista vazia de sessões");
        
        // ACT
        log.info("\n⚡ ACT - Executando service.getMemberSessions()");
        java.util.List<org.estg.dto.SessionRecordDTO> result = service.getMemberSessions("member-001");
        log.info("  ✓ Histórico de sessões retornado");
        
        // ASSERT
        log.info("\n✔️  ASSERT - Verificando resultados");
        assertNotNull(result);
        log.info("  ✓ Lista NOT NULL");
        
        assertEquals(0, result.size());
        log.info("  ✓ Tamanho da lista (vazia inicialmente): {}", result.size());
        
        // VERIFY
        log.info("\n🔍 VERIFY - Verificando interações");
        verify(memberRepository, times(1)).existsById("member-001");
        log.info("  ✓ repository.existsById() foi chamado exatamente 1 vez");
        
        verify(sessionRecordRepository, times(1)).findByMemberIdOrderBySessionDateTimeDesc("member-001");
        log.info("  ✓ sessionRecordRepository.findByMemberId() foi chamado exatamente 1 vez");
        
        log.info("\n✅ ✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅ ✅\n");
    }
}
