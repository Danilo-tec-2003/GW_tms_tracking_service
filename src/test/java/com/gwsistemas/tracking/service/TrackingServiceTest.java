package com.gwsistemas.tracking.service;

import com.gwsistemas.tracking.dto.input.OccurrenceCreateDTO;
import com.gwsistemas.tracking.dto.output.OccurrenceDTO;
import com.gwsistemas.tracking.enums.TrackingStatus;
import com.gwsistemas.tracking.exception.BusinessRuleException;
import com.gwsistemas.tracking.exception.ResourceNotFoundException; // Import necessário
import com.gwsistemas.tracking.mapper.OccurrenceMapper;
import com.gwsistemas.tracking.mapper.OrderMapper;
import com.gwsistemas.tracking.model.Occurrence;
import com.gwsistemas.tracking.model.Order;
import com.gwsistemas.tracking.repository.OccurrenceRepository;
import com.gwsistemas.tracking.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OccurrenceRepository occurrenceRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OccurrenceMapper occurrenceMapper;

    @InjectMocks
    private TrackingService trackingService;

    private Order encomendaFalsa;
    private OccurrenceCreateDTO dtoEntrada;
    private final String CODIGO_RASTREIO = "BR100";

    /**
     * Este método @BeforeEach corre automaticamente ANTES de cada @Test.
     * ele "arruma" os objetos comuns que todos os testes usam,
     * evitando código duplicado.
     */
    @BeforeEach
    void setUp() {
        // Criamos os objetos "base" aqui
        encomendaFalsa = new Order();
        encomendaFalsa.setTrackingCode(CODIGO_RASTREIO);
        encomendaFalsa.setCustomerName("Cliente Teste");

        dtoEntrada = new OccurrenceCreateDTO();
    }


    /**
     * Teste  Prova que o sistema impede o registo
     * se a encomenda já foi ENTREGUE.
     */
    @Test
    void deveLancarExcecao_QuandoStatusJaEstiverEntregue() {
        Occurrence ultimaOcorrencia = new Occurrence();
        ultimaOcorrencia.setStatus(TrackingStatus.ENTREGUE);
        ultimaOcorrencia.setOccurrenceTimestamp(LocalDateTime.now().minusDays(1));

        encomendaFalsa.setOccurrences(List.of(ultimaOcorrencia));

        dtoEntrada.setStatus(TrackingStatus.EM_TRANSITO);

        when(orderRepository.findByTrackingCode(CODIGO_RASTREIO))
                .thenReturn(Optional.of(encomendaFalsa));

        BusinessRuleException excecao = assertThrows(BusinessRuleException.class, () -> {
            trackingService.registerNewOccurrence(CODIGO_RASTREIO, dtoEntrada);
        });

        String mensagemEsperada = "A encomenda já foi marcada como 'ENTREGUE'.";
        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    /**
     * Teste  Prova que o sistema impede um status INVÁLIDO
     * após um status de NAO_ENTREGUE.
     */
    @Test
    void deveLancarExcecao_QuandoStatusInvalidoAposNaoEntregue() {

        Occurrence ultimaOcorrencia = new Occurrence();
        ultimaOcorrencia.setStatus(TrackingStatus.NAO_ENTREGUE);
        ultimaOcorrencia.setOccurrenceTimestamp(LocalDateTime.now().minusDays(1));

        encomendaFalsa.setOccurrences(List.of(ultimaOcorrencia));

        dtoEntrada.setStatus(TrackingStatus.ENTREGUE);

        when(orderRepository.findByTrackingCode(CODIGO_RASTREIO))
                .thenReturn(Optional.of(encomendaFalsa));

        BusinessRuleException excecao = assertThrows(BusinessRuleException.class, () -> {
            trackingService.registerNewOccurrence(CODIGO_RASTREIO, dtoEntrada);
        });

        String mensagemEsperada = "Após 'NÃO ENTREGUE', o único status permitido é 'SAÍDA PARA ENTREGA'.";
        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    /**
     * Teste  Prova que o sistema lança a exceção correta
     * (que o ControllerAdvice pode apanhar) se a encomenda não existir.
     */
    @Test
    void deveLancarExcecao_QuandoEncomendaNaoForEncontrada() {
        when(orderRepository.findByTrackingCode(CODIGO_RASTREIO))
                .thenReturn(Optional.empty()); // Retorna um Optional vazio

        assertThrows(ResourceNotFoundException.class, () -> {
            trackingService.registerNewOccurrence(CODIGO_RASTREIO, dtoEntrada);
        });
    }

    /**
     * Teste (Caminho Feliz): Prova que o sistema salva com sucesso
     * quando as regras são válidas. (Ex: de EM_TRANSITO para ENTREGUE)
     */
    @Test
    void deveRegistrarComSucesso_QuandoRegrasValidas() {

        Occurrence ultimaOcorrencia = new Occurrence();
        ultimaOcorrencia.setStatus(TrackingStatus.EM_TRANSITO);
        encomendaFalsa.setOccurrences(List.of(ultimaOcorrencia));

        dtoEntrada.setStatus(TrackingStatus.ENTREGUE);

        Occurrence ocorrenciaSalva = new Occurrence();
        OccurrenceDTO dtoDeSaida = new OccurrenceDTO();
        dtoDeSaida.setStatus(TrackingStatus.ENTREGUE);

        when(orderRepository.findByTrackingCode(CODIGO_RASTREIO))
                .thenReturn(Optional.of(encomendaFalsa));

        when(occurrenceMapper.toEntity(dtoEntrada))
                .thenReturn(new Occurrence());

        when(occurrenceRepository.save(any(Occurrence.class)))
                .thenReturn(ocorrenciaSalva);

        when(occurrenceMapper.toDTO(ocorrenciaSalva))
                .thenReturn(dtoDeSaida);

        OccurrenceDTO resultadoDTO = trackingService.registerNewOccurrence(CODIGO_RASTREIO, dtoEntrada);

        assertNotNull(resultadoDTO);
        assertEquals(TrackingStatus.ENTREGUE, resultadoDTO.getStatus());

        verify(occurrenceRepository, times(1)).save(any(Occurrence.class));
    }
}