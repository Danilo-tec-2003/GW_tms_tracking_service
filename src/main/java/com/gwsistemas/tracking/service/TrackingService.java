package com.gwsistemas.tracking.service;

import com.gwsistemas.tracking.dto.input.OccurrenceCreateDTO;
import com.gwsistemas.tracking.dto.input.OrderCreateDTO;
import com.gwsistemas.tracking.dto.output.OccurrenceDTO;
import com.gwsistemas.tracking.dto.output.OrderDetailsDTO;
import com.gwsistemas.tracking.enums.TrackingStatus;
import com.gwsistemas.tracking.exception.BusinessRuleException;
import com.gwsistemas.tracking.exception.ResourceNotFoundException;
import com.gwsistemas.tracking.mapper.OccurrenceMapper;
import com.gwsistemas.tracking.mapper.OrderMapper;
import com.gwsistemas.tracking.model.Occurrence;
import com.gwsistemas.tracking.model.Order;
import com.gwsistemas.tracking.repository.OccurrenceRepository;
import com.gwsistemas.tracking.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Service
public class TrackingService {

    /**
     * Optei por utilizar a injeção de dependências via construtor,
     * seguindo a convenção de boas práticas do Spring.
     *
     * @param orderRepository O repositório para acesso aos dados da Encomenda.
     * @param occurrenceRepository O repositório para acesso aos dados da Ocorrência.
     * @param orderMapper O mapper para converter (mapear) entidades e DTOs de Order.
     * @param occurrenceMapper O mapper para converter (mapear) entidades e DTOs de Occurrence.
     */

    private final OrderRepository orderRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final OrderMapper orderMapper;
    private final OccurrenceMapper occurrenceMapper;

    public TrackingService(OrderRepository orderRepository, OccurrenceRepository occurrenceRepository, OrderMapper orderMapper, OccurrenceMapper occurrenceMapper) {
        this.orderRepository = orderRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.orderMapper = orderMapper;
        this.occurrenceMapper = occurrenceMapper;
    }

    /**
     * Registra uma nova ocorrência (evento de rastreio) para uma encomenda.
     *
     * Etapas:
     * 1. Localiza a encomenda pelo código de rastreio.
     * 2. Identifica a ocorrência mais recente (último status).
     * 3. Aplica as regras de negócio (validações de impedimento).
     * 4. Cria e salva a nova ocorrência.
     * 5. Retorna o DTO da ocorrência criada.

     * @param trackingCode O código de rastreio...
     * @param dto O DTO de entrada...
     * @return O DTO da ocorrência criada.
     * @throws ResourceNotFoundException se nenhuma encomenda for encontrada.
     * @throws BusinessRuleException se uma regra de negócio for violada.
     */

    @Transactional
    public OccurrenceDTO registerNewOccurrence(String trackingCode, OccurrenceCreateDTO dto) {
        Order order = findOrderByTrackingCode(trackingCode);
        Optional<Occurrence> latestOccurrence = findLatestOccurrence(order);

        validateBusinessRules(latestOccurrence, dto);
        Occurrence saved = createAndSaveOccurrence(order, dto);

        return occurrenceMapper.toDTO(saved);
    }

    /**
     * Busca uma encomenda pelo código de rastreio.
     * Lança exceção se não for encontrada.
     */
    private Order findOrderByTrackingCode(String trackingCode) {
        return orderRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Encomenda não encontrada."));
    }

    /**
     * Retorna a última ocorrência registrada para a encomenda,
     * com base no timestamp mais recente.
     */
    private Optional<Occurrence> findLatestOccurrence(Order order) {
        return order.getOccurrences()
                .stream()
                .max(Comparator.comparing(Occurrence::getOccurrenceTimestamp));
    }

    /**
     * Aplica as regras de negócio que controlam o fluxo de status:
     * - Impede novos eventos após "ENTREGUE".
     * - Após "NÃO ENTREGUE", apenas "SAÍDA PARA ENTREGA" é permitido.
     */
    private void validateBusinessRules(Optional<Occurrence> latestOccurrenceOpt, OccurrenceCreateDTO dto) {
        if (latestOccurrenceOpt.isEmpty()) {
            return;
        }

        TrackingStatus latestStatus = latestOccurrenceOpt.get().getStatus();
        TrackingStatus newStatus = dto.getStatus();

        // Regra 1: Não pode adicionar nada após "ENTREGUE"
        if (latestStatus == TrackingStatus.ENTREGUE) {
            throw new BusinessRuleException("A encomenda já foi marcada como 'ENTREGUE'.");
        }

        // Regra 2: Após "NÃO ENTREGUE", somente "SAÍDA PARA ENTREGA" é permitido
        if (latestStatus == TrackingStatus.NAO_ENTREGUE
                && newStatus != TrackingStatus.SAIDA_PARA_ENTREGA) {
            throw new BusinessRuleException("Após 'NÃO ENTREGUE', o único status permitido é 'SAÍDA PARA ENTREGA'.");
        }
    }

    /**
     * Cria uma nova ocorrência com os dados do DTO,
     * associa à encomenda e salva no banco de dados.
     */
    private Occurrence createAndSaveOccurrence(Order order, OccurrenceCreateDTO dto) {
        Occurrence occurrence = occurrenceMapper.toEntity(dto);
        occurrence.setOrder(order);
        occurrence.setOccurrenceTimestamp(LocalDateTime.now());
        return occurrenceRepository.save(occurrence);
    }

    /**
     * Consulta o status atual e a timeline completa de uma encomenda
     * usando seu código de rastreio.
     *
     * @param trackingCode O código de rastreio (chave de negócio) da encomenda.
     * @return Um DTO (OrderDetailsDTO) contendo os dados da encomenda e a
     * lista completa de suas ocorrências, ordenada por data.
     * @throws ResourceNotFoundException se nenhuma encomenda for encontrada.
     */
    public OrderDetailsDTO getTrackingDetails(String trackingCode) {

        Order order = findOrderByTrackingCode(trackingCode);
        OrderDetailsDTO dto = orderMapper.toDetailsDTO(order);
        //Garantindo a ordenação da timeline, pegando a lista de DTOS de ocorrências e ordenamos usando o occurrenceTimestamp' de cada um.
        dto.getOccurrences().sort(
                Comparator.comparing(OccurrenceDTO::getOccurrenceTimestamp)
        );

        return dto;
    }

    /**
     * Cria uma nova encomenda, validando se o código de rastreio já existe.
     *
     * @param dto O DTO de entrada com os dados da nova encomenda.
     * @return O DTO da encomenda recém-criada.
     * @throws BusinessRuleException se o 'trackingCode' já estiver em uso.
     */
    @Transactional
    public OrderDetailsDTO createOrder(OrderCreateDTO dto) {
        Optional<Order> existingOrderOpt = orderRepository.findByTrackingCode(dto.getTrackingCode());

        if (existingOrderOpt.isPresent()) {
            throw new BusinessRuleException("Já existe uma encomenda cadastrada com o código de rastreio: " + dto.getTrackingCode());
        }

        Order newOrder = orderMapper.toEntity(dto);
        Order savedOrder = orderRepository.save(newOrder);

        return orderMapper.toDetailsDTO(savedOrder);
    }

}
