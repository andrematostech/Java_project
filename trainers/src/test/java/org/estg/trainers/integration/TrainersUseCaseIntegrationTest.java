package org.estg.trainers.integration;

import org.estg.trainers.data.TrainerRepository;
import org.estg.trainers.dto.CreateTrainerRequest;
import org.estg.trainers.dto.TrainerDTO;
import org.estg.trainers.model.CertificationStatus;
import org.estg.trainers.model.Trainer;
import org.estg.trainers.model.TrainerSpeciality;
import org.estg.trainers.model.TrainerStatus;
import org.estg.trainers.service.TrainersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-2 SUPORTE: VALIDAÇÃO DE TREINADOR
 * Testes de integração para gestão de treinadores
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("UC-2 Suporte: Validação de Treinador - Testes de Integração")
class TrainersUseCaseIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(TrainersUseCaseIntegrationTest.class);

    @Autowired
    private TrainersService trainersService;

    @Autowired
    private TrainerRepository trainerRepository;

    @BeforeEach
    void setUp() {
        log.info("\n╔═══════════════════════════════════════════════════════════════╗");
        log.info("║  SETUP - UC-2 Suporte: Validação de Treinador               ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        trainerRepository.deleteAll();
        log.info("✅ Base de dados trainers_db limpa");
    }

    @Test
    @DisplayName("UC-2 Suporte: Registar Treinador e Verificar Disponibilidade")
    void testRegisterTrainer_CheckAvailability() {
        log.info("\n┌─────────────────────────────────────────────────────────────┐");
        log.info("│ UC-2 SUPORTE: Registar Treinador - Validação para Booking   │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ARRANGE
        log.info("\n📋 ARRANGE: Preparar dados de treinador");
        CreateTrainerRequest request = new CreateTrainerRequest();
        request.setFullName("João Silva");
        request.setEmail("joao.trainer@gym.pt");
        request.setPhoneNumber("+351912345678");
        request.setSpeciality(TrainerSpeciality.STRENGTH);
        request.setYearsExperience(5);
        request.setCertificationStatus(CertificationStatus.APPROVED);

        log.info("  • Nome: {}", request.getFullName());
        log.info("  • Email: {}", request.getEmail());
        log.info("  • Especialidade: {}", request.getSpeciality());

        // ACT
        log.info("\n⚡ ACT: Criar treinador");
        TrainerDTO createdTrainer = trainersService.createTrainer(request);
        log.info("  ✓ Treinador criado com ID: {}", createdTrainer.getId());

        // ASSERT
        log.info("\n✔️  ASSERT: Validações");
        assertNotNull(createdTrainer.getId());
        log.info("  ✓ ID gerado: {}", createdTrainer.getId());

        assertEquals("João Silva", createdTrainer.getFullName());
        log.info("  ✓ Nome: {}", createdTrainer.getFullName());

        assertEquals(TrainerSpeciality.STRENGTH, createdTrainer.getSpeciality());
        log.info("  ✓ Especialidade: {}", createdTrainer.getSpeciality());

        assertEquals(TrainerStatus.ACTIVE, createdTrainer.getStatus());
        log.info("  ✓ Status: ACTIVE");

        // Verificar persistência
        Optional<Trainer> inDB = trainerRepository.findById(createdTrainer.getId());
        assertTrue(inDB.isPresent());
        log.info("  ✓ Persistido em trainers_db");

        log.info("\n✅ UC-2 SUPORTE COMPLETO: Treinador registado e disponível");
    }

    @Test
    @DisplayName("UC-2 Alternativo: Treinador Inativo - Validação Falha")
    void testInactiveTrainer_ValidationFails() {
        log.info("\n┌─────────────────────────────────────────────────────────────┐");
        log.info("│ UC-2 ALTERNATIVO: Treinador Inativo - Bloquear Booking      │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ARRANGE
        log.info("\n📋 ARRANGE: Criar e suspender treinador");
        CreateTrainerRequest request = new CreateTrainerRequest();
        request.setFullName("Pedro Inativo");
        request.setEmail("pedro.inactive@gym.pt");
        request.setSpeciality(TrainerSpeciality.CARDIO);

        TrainerDTO created = trainersService.createTrainer(request);
        trainersService.suspendTrainer(created.getId());
        log.info("  ✓ Treinador suspenso");

        // ACT
        log.info("\n⚡ ACT: Validar status");
        TrainerDTO suspended = trainersService.getTrainerById(created.getId());

        // ASSERT
        log.info("\n✔️  ASSERT: Validações");
        assertEquals(TrainerStatus.INACTIVE, suspended.getStatus());
        log.info("  ✓ Status: INACTIVE");

        log.info("  ✓ UC-2 ALTERNATIVO: Booking bloqueado para treinador inativo");

        log.info("\n✅ UC-2 ALTERNATIVO COMPLETO");
    }

    @Test
    @DisplayName("UC-2 Suporte: Listar Treinadores por Especialidade")
    void testListTrainersBySpecialty() {
        log.info("\n┌─────────────────────────────────────────────────────────────┐");
        log.info("│ UC-2 Suporte: Filtrar Treinadores por Especialidade         │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ARRANGE
        log.info("\n📋 ARRANGE: Criar treinadores com especialidades diferentes");

        CreateTrainerRequest cardioReq = new CreateTrainerRequest();
        cardioReq.setFullName("Ana Cardio");
        cardioReq.setEmail("ana.cardio@gym.pt");
        cardioReq.setSpeciality(TrainerSpeciality.CARDIO);

        CreateTrainerRequest strengthReq = new CreateTrainerRequest();
        strengthReq.setFullName("Carlos Força");
        strengthReq.setEmail("carlos.forca@gym.pt");
        strengthReq.setSpeciality(TrainerSpeciality.STRENGTH);

        trainersService.createTrainer(cardioReq);
        trainersService.createTrainer(strengthReq);
        log.info("  ✓ 2 treinadores criados");

        // ACT
        log.info("\n⚡ ACT: Filtrar por especialidade CARDIO");
        List<TrainerDTO> cardioTrainers = trainersService.getAllTrainers(TrainerSpeciality.CARDIO);

        // ASSERT
        log.info("\n✔️  ASSERT: Validações");
        assertEquals(1, cardioTrainers.size());
        log.info("  ✓ Encontrado {} treinador de CARDIO", cardioTrainers.size());

        assertEquals(TrainerSpeciality.CARDIO, cardioTrainers.get(0).getSpeciality());
        log.info("  ✓ Especialidade confirmada");

        List<TrainerDTO> allTrainers = trainersService.getAllTrainers(null);
        assertEquals(2, allTrainers.size());
        log.info("  ✓ Total: {} treinadores", allTrainers.size());

        log.info("\n✅ UC-2 FILTRO COMPLETO");
    }

    @Test
    @DisplayName("UC-2 Extensão: Reativar Treinador")
    void testReactivateTrainer() {
        log.info("\n┌─────────────────────────────────────────────────────────────┐");
        log.info("│ UC-2 Extensão: Reativar Treinador Suspenso                  │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ARRANGE
        log.info("\n📋 ARRANGE: Criar e suspender treinador");
        CreateTrainerRequest request = new CreateTrainerRequest();
        request.setFullName("Maria Suspensa");
        request.setEmail("maria.suspensa@gym.pt");
        request.setSpeciality(TrainerSpeciality.YOGA);

        TrainerDTO created = trainersService.createTrainer(request);
        trainersService.suspendTrainer(created.getId());
        log.info("  ✓ Treinador suspenso");

        // ACT
        log.info("\n⚡ ACT: Reativar treinador");
        trainersService.activateTrainer(created.getId());
        TrainerDTO reactivated = trainersService.getTrainerById(created.getId());

        // ASSERT
        log.info("\n✔️  ASSERT: Validações");
        assertEquals(TrainerStatus.ACTIVE, reactivated.getStatus());
        log.info("  ✓ Status: ACTIVE");

        Optional<Trainer> inDB = trainerRepository.findById(created.getId());
        assertTrue(inDB.isPresent());
        assertEquals(TrainerStatus.ACTIVE, inDB.get().getStatus());
        log.info("  ✓ Persistido em trainers_db");

        log.info("\n✅ UC-2 REATIVAÇÃO COMPLETO");
    }
}
